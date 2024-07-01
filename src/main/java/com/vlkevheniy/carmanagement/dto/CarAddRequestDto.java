package com.vlkevheniy.carmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CarAddRequestDto {

    @NotBlank(message = "Model cannot be empty")
    private String model;

    @Min(value = 1950, message = "Year must be greater than or equal to 1990")
    @Max(value = 2024, message = "Year must be less than or equal to 2023")
    private int year;

    @NotBlank(message = "Color cannot be empty")
    private String color;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    @NotNull(message = "Brand name cannot be null")
    private String brandName;

    private String brandCountry;
}
