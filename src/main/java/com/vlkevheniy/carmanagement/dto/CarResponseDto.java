package com.vlkevheniy.carmanagement.dto;

import lombok.Data;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private int year;
    private String color;
    private double price;
    private BrandResponseDto brand;
}
