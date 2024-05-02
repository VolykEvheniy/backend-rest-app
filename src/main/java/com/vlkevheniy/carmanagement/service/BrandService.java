package com.vlkevheniy.carmanagement.service;

import com.vlkevheniy.carmanagement.dto.BrandRequestDto;
import com.vlkevheniy.carmanagement.dto.BrandResponseDto;

import java.util.List;

public interface BrandService {

    List<BrandResponseDto> getAllBrands();

    BrandResponseDto addBrand(BrandRequestDto brandRequestDto);

    BrandResponseDto updateBrand(Long id, BrandRequestDto brandRequestDto);

    void removeBrand(Long id);
}
