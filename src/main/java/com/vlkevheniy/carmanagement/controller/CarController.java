package com.vlkevheniy.carmanagement.controller;

import com.vlkevheniy.carmanagement.data.Car;
import com.vlkevheniy.carmanagement.dto.*;
import com.vlkevheniy.carmanagement.service.CarService;
import com.vlkevheniy.carmanagement.util.CSVGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car")
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> addCar(@Valid @RequestBody CarRequestDto carDto) {
        CarResponseDto carResponseDto = carService.addCar(carDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCar(@PathVariable Long id) {
        CarResponseDto carResponseDto = carService.getCarById(id);
        return ResponseEntity.ok(carResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable Long id, @Valid @RequestBody CarRequestDto carRequestDto) {
        CarResponseDto updatedCar = carService.updateCar(id, carRequestDto);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeCar(@PathVariable Long id) {
        carService.removeCar(id);
        return ResponseEntity.ok("Car was deleted successfully");
    }

    @PostMapping("/_list")
    public ResponseEntity<CarListResponseDto> listCars(@Valid @RequestBody CarSearchRequestDto criteria) {
        CarListResponseDto carList = carService.getCarList(criteria);
        return ResponseEntity.ok(carList);
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> generateCarReport(@Valid @RequestBody CarSearchRequestDto criteria) {
        List<Car> cars = carService.findAllCarsByCriteria(criteria);
        ByteArrayResource csvFile = CSVGenerator.generateCsvReport(cars);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\"")
                .body(csvFile);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> uploadCars(@RequestParam("file")MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponseDto(0, 0, "Empty file provided"));
        }
        UploadResponseDto result = carService.uploadCars(file);
        return ResponseEntity.ok(result);
    }
}
