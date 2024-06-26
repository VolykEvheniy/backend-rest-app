package com.vlkevheniy.carmanagement.service;

import com.vlkevheniy.carmanagement.model.Car;
import com.vlkevheniy.carmanagement.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarService {

    CarResponseDto addCar(CarAddRequestDto carDto);

    CarResponseDto getCarById(Long id);

    CarResponseDto updateCar(Long id, CarRequestDto carDto);

    void removeCar(Long id);

    CarListResponseDto getCarList(CarSearchRequestDto criteria);

    List<Car> findAllCarsByCriteria(CarSearchRequestDto criteria);

    UploadResponseDto uploadCars(MultipartFile file);
}
