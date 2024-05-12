package com.vlkevheniy.carmanagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlkevheniy.carmanagement.model.Brand;
import com.vlkevheniy.carmanagement.model.Car;
import com.vlkevheniy.carmanagement.dto.*;
import com.vlkevheniy.carmanagement.exception.CarUploadException;
import com.vlkevheniy.carmanagement.repository.BrandRepository;
import com.vlkevheniy.carmanagement.repository.CarRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    @Transactional
    public CarResponseDto addCar(CarRequestDto carDto) {
        Brand brand = brandRepository.findById(carDto.getBrandId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with ID: " + carDto.getBrandId()));

        Car car = new Car();
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setColor(carDto.getColor());
        car.setPrice(carDto.getPrice());
        car.setBrand(brand);

        return modelMapper.map(carRepository.save(car), CarResponseDto.class);
    }

    @Override
    public CarResponseDto getCarById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found with ID: " + carId));

        return modelMapper.map(car, CarResponseDto.class);
    }

    @Override
    @Transactional
    public CarResponseDto updateCar(Long id, CarRequestDto carDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found with ID: " + id));

        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setColor(carDto.getColor());
        car.setPrice(carDto.getPrice());

        Brand brand = brandRepository.findById(carDto.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with ID: " + carDto.getBrandId()));

        car.setBrand(brand);

        return modelMapper.map(carRepository.save(car), CarResponseDto.class);
    }

    @Override
    @Transactional
    public void removeCar(Long id) {
        if(!carRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found with ID: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    public CarListResponseDto getCarList(CarSearchRequestDto criteria) {
        Specification<Car> specification = getCarSpecification(criteria);

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Car> carPage = carRepository.findAll(specification, pageable);
        List<CarResponseDto> carResponseDtoList = carPage.getContent()
                .stream()
                .map(car -> modelMapper.map(car, CarResponseDto.class))
                .collect(Collectors.toList());
        return new CarListResponseDto(carResponseDtoList, carPage.getTotalPages());
    }

    @Override
    public List<Car> findAllCarsByCriteria(CarSearchRequestDto criteria) {
        Specification<Car> specification = getCarSpecification(criteria);
        return carRepository.findAll(specification);
    }

    private Specification<Car> getCarSpecification(CarSearchRequestDto criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.getBrandId() != null) {
                predicates.add(cb.equal(root.join("brand").get("id"), criteria.getBrandId()));
            }
            if (criteria.getMinPrice() != null) {
                predicates.add(cb.ge(root.get("price"), criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.le(root.get("price"), criteria.getMaxPrice()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional
    public UploadResponseDto uploadCars(MultipartFile file) {
        try {
            InputStream bufferedInputStream = new BufferedInputStream(file.getInputStream());

            List<CarRequestDto> cars = objectMapper.readValue(bufferedInputStream, new TypeReference<>(){});
            int successful = 0;
            int failed = 0;

            for (CarRequestDto carDto : cars) {
                if (brandRepository.existsById(carDto.getBrandId()) && validateCarDto(carDto)) {
                    try {
                        Car car = mapDtoToCar(carDto);
                        carRepository.save(car);
                        successful++;
                    } catch (Exception e) {
                        failed++;
                    }
                } else {
                    failed++;
                }
            }
            return new UploadResponseDto(successful, failed, "Import completed");
        } catch (IOException e) {
            throw new CarUploadException("Failed to process upload file", e);
        }

    }

    private boolean validateCarDto(CarRequestDto carDto) {
        BindingResult errors = new BeanPropertyBindingResult(carDto, "carRequestDto");
        validator.validate(carDto, errors);
        return !errors.hasErrors();
    }

    private Car mapDtoToCar(CarRequestDto carDto) {
        Car car = new Car();
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setColor(carDto.getColor());
        car.setPrice(carDto.getPrice());
        car.setBrand(brandRepository.findById(carDto.getBrandId()).orElse(null));
        return car;
    }
}
