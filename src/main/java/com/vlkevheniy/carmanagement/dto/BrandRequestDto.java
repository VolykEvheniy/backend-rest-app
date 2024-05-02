package com.vlkevheniy.carmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BrandRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 255 characters")
    private final String name;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 255 characters")
    private final String country;

}
