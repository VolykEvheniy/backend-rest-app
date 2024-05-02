package com.vlkevheniy.carmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
@AllArgsConstructor
public class CarSearchRequestDto {

    private Long brandId;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Min(value = 0, message = "The minimum price must be non-negative")
    private Double minPrice;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Min(value = 0, message = "The maximum price must be non-negative")
    private Double maxPrice;

    @NotNull(message = "Page number cannot be null")
    @Min(value = 0, message = "Page number must be non-negative")
    private int page;

    @NotNull(message = "Page size cannot be null")
    @Min(value = 1, message = "Page size must be at least 1")
    private int size;
}
