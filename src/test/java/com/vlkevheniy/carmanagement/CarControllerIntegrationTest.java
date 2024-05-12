package com.vlkevheniy.carmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlkevheniy.carmanagement.model.Brand;
import com.vlkevheniy.carmanagement.model.Car;
import com.vlkevheniy.carmanagement.dto.CarResponseDto;
import com.vlkevheniy.carmanagement.dto.CarRequestDto;
import com.vlkevheniy.carmanagement.dto.CarSearchRequestDto;
import com.vlkevheniy.carmanagement.repository.BrandRepository;
import com.vlkevheniy.carmanagement.repository.CarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarRepository carRepository;

    @AfterEach
    public void afterEach() {
        carRepository.deleteAll();
    }

    @Test
    public void testWhenPostCar_thenCreateCar() throws Exception {
        CarRequestDto carDto = new CarRequestDto();
        carDto.setModel("Fiesta");
        carDto.setYear(2022);
        carDto.setColor("Blue, White, Green");
        carDto.setPrice(19530.00);
        carDto.setBrandId(brandRepository.findByName("Ford")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"))
                .getId());

        mockMvc.perform(post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.model").value("Fiesta"))
                .andExpect(jsonPath("$.brand.name").value("Ford"));
    }

    @Test
    public void testWhenPostCar_withInvalidData_thenError() throws Exception {
        CarRequestDto carDto = new CarRequestDto();
        carDto.setModel("");
        carDto.setYear(1899);
        carDto.setColor("");
        carDto.setPrice(-1000);
        carDto.setBrandId(null);

        mockMvc.perform(post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    public void testWhenPostCar_withNonExistentBrand_thenNotFound() throws Exception {
        CarRequestDto carDto = new CarRequestDto();
        carDto.setModel("Mustang");
        carDto.setYear(2022);
        carDto.setColor("Red, Black, White");
        carDto.setPrice(35670.00);
        carDto.setBrandId(999L);

        mockMvc.perform(post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCar_ReturnsCarDetails() throws Exception {
        CarRequestDto carDto = new CarRequestDto();
        carDto.setModel("Model X");
        carDto.setYear(2020);
        carDto.setColor("Red, Black, White");
        carDto.setPrice(35670.00);
        carDto.setBrandId(2L);

        MvcResult result = mockMvc.perform(post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        CarResponseDto responseDto = objectMapper.readValue(responseContent, CarResponseDto.class);

        mockMvc.perform(get("/api/car/" + responseDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.model").value("Model X"))
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.color").value("Red, Black, White"))
                .andExpect(jsonPath("$.price").value(35670.00))
                .andExpect(jsonPath("$.brand.id").value(2L))
                .andExpect(jsonPath("$.brand.name").value("Tesla"))
                .andExpect(jsonPath("$.brand.country").value("USA"));;
    }

    @Test
    public void testGetCar_NotFound() throws Exception {
        mockMvc.perform(get("/api/car/99999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testUpdateCar_Successful() throws Exception {
        Brand existingBrand = brandRepository.findByName("Tesla")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        Car car = new Car();
        car.setModel("Model S");
        car.setYear(2021);
        car.setColor("Black");
        car.setPrice(75000.00);
        car.setBrand(existingBrand);

        Car savedCar = carRepository.save(car);
        CarRequestDto updateDto = new CarRequestDto();
        updateDto.setModel("Audi Updated Model");
        updateDto.setYear(2022);
        updateDto.setColor("White");
        updateDto.setPrice(76000.00);
        updateDto.setBrandId(1L);


        mockMvc.perform(put("/api/car/" + savedCar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Audi Updated Model"))
                .andExpect(jsonPath("$.year").value(2022))
                .andExpect(jsonPath("$.color").value("White"))
                .andExpect(jsonPath("$.price").value(76000.00))
                .andExpect(jsonPath("$.brand.id").value(1L))
                .andExpect(jsonPath("$.brand.name").value("Audi"))
                .andExpect(jsonPath("$.brand.country").value("Germany"));
    }

    @Test
    public void testUpdateCar_ValidationFailure() throws Exception {
        Brand existingBrand = brandRepository.findByName("Tesla")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        Car car = new Car();
        car.setModel("Model S");
        car.setYear(2021);
        car.setColor("Black");
        car.setPrice(75000.00);
        car.setBrand(existingBrand);

        Car savedCar = carRepository.save(car);
        CarRequestDto invalidUpdateDto = new CarRequestDto();
        invalidUpdateDto.setModel(""); // invalid data
        invalidUpdateDto.setYear(-1);
        invalidUpdateDto.setColor("");
        invalidUpdateDto.setPrice(-100.00);
        invalidUpdateDto.setBrandId(1L);

        mockMvc.perform(put("/api/car/" + savedCar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testUpdateCar_NotFound() throws Exception {
        long nonExistentId = 999L;

        CarRequestDto updateDto = new CarRequestDto();
        updateDto.setModel("Audi Updated Model");
        updateDto.setYear(2022);
        updateDto.setColor("White");
        updateDto.setPrice(76000.00);
        updateDto.setBrandId(1L);

        mockMvc.perform(put("/api/car/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCar_Successful() throws Exception {
        Car car = new Car();
        car.setModel("Test Model");
        car.setYear(2020);
        car.setColor("Blue");
        car.setPrice(20000);
        car = carRepository.save(car);

        mockMvc.perform(delete("/api/car/" + car.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Car was deleted successfully"));
    }

    @Test
    public void testDeleteCar_NotFound() throws Exception {
        mockMvc.perform(delete("/api/car/99999"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testListCars_Successful() throws Exception {
        Brand existingBrand = brandRepository.findByName("Toyota")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        Car car = new Car();
        car.setModel("Corolla");
        car.setYear(2022);
        car.setColor("Blue");
        car.setPrice(20000.00);
        car.setBrand(existingBrand);

        carRepository.save(car);

        CarSearchRequestDto criteria = new CarSearchRequestDto(existingBrand.getId(), 15000.0, 25000.0, 0, 10);

        mockMvc.perform(post("/api/car/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.carResponseDtoList[0].model").value("Corolla"));
    }

    @Test
    public void testListCars_InvalidCriteria() throws Exception {
        CarSearchRequestDto criteria = new CarSearchRequestDto(null, -1.0, -1.0, -1, -1);

        mockMvc.perform(post("/api/car/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testListCars_NoResults() throws Exception {
        Brand existingBrand = brandRepository.findByName("Toyota")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        Car car = new Car();
        car.setModel("Corolla");
        car.setYear(2022);
        car.setColor("Blue");
        car.setPrice(20000.00);
        car.setBrand(existingBrand);

        carRepository.save(car);
        CarSearchRequestDto criteria = new CarSearchRequestDto(existingBrand.getId(), 30000.0, 50000.0, 0, 10);

        mockMvc.perform(post("/api/car/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.carResponseDtoList").isEmpty());
    }

    @Test
    public void testGenerateCarReport() throws Exception {
        Brand existingBrand = brandRepository.findByName("Toyota")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        Car car = new Car();
        car.setModel("Corolla");
        car.setYear(2022);
        car.setColor("Blue");
        car.setPrice(20000.00);
        car.setBrand(existingBrand);

        carRepository.save(car);

        CarSearchRequestDto criteria = new CarSearchRequestDto(existingBrand.getId(), 15000.0, 25000.0, 0, 10);

        mockMvc.perform(post("/api/car/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andDo(result -> {
                    String content = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
                    assertTrue(content.contains("Id,Model,Year,Brand,Price"));
                });
    }

    @Test
    public void testWhenValidUpload_thenSucceeds() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.json",
                MediaType.TEXT_PLAIN_VALUE,
                "[{\"model\":\"Camry\",\"year\":2022,\"color\":\"White, Black\",\"price\":25000.00,\"brandId\":5}]".getBytes());

        mockMvc.perform(multipart("/api/car/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(1))
                .andExpect(jsonPath("$.failed").value(0))
                .andExpect(jsonPath("$.message").value("Import completed"));

        assertEquals(1, carRepository.count());
    }

    @Test
    public void testUploadWhenInvalidData_thenFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.json",
                MediaType.TEXT_PLAIN_VALUE,
                "[{\"model\":\"\",\"year\":1999,\"color\":\"\",\"price\":-10,\"brandId\":5}]".getBytes());

        mockMvc.perform(multipart("/api/car/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(0))
                .andExpect(jsonPath("$.failed").value(1));
    }

    @Test
    public void testWhenEmptyFile_thenBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.json",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]);

        mockMvc.perform(multipart("/api/car/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Empty file provided"));
    }
}
