package com.vlkevheniy.carmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CarListResponseDto {

    private List<CarResponseDto> carResponseDtoList;
    private int totalPages;
}
