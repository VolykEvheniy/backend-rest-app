package com.vlkevheniy.carmanagement.service;

import com.vlkevheniy.carmanagement.data.Brand;
import com.vlkevheniy.carmanagement.dto.BrandRequestDto;
import com.vlkevheniy.carmanagement.dto.BrandResponseDto;
import com.vlkevheniy.carmanagement.exception.DuplicateBrandException;
import com.vlkevheniy.carmanagement.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BrandResponseDto> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brands.stream()
                .map(brand -> modelMapper.map(brand, BrandResponseDto.class))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public BrandResponseDto addBrand(BrandRequestDto brandDto) {
        if (brandRepository.existsByName(brandDto.getName())) {
            throw new DuplicateBrandException("A brand with name " + brandDto.getName() + " already exists.");
        }

        Brand brand = new Brand();
        brand.setName(brandDto.getName());
        brand.setCountry(brandDto.getCountry());

        return modelMapper.map(brandRepository.save(brand), BrandResponseDto.class);
    }

    @Override
    @Transactional
    public BrandResponseDto updateBrand(Long id, BrandRequestDto brandDto) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with ID: " + id));

        if (!brand.getName().equals(brandDto.getName()) && brandRepository.existsByName(brandDto.getName())) {
            throw new DuplicateBrandException("A brand with name " + brandDto.getName() + " already exists.");
        }

        brand.setName(brandDto.getName());
        brand.setCountry(brandDto.getCountry());

        return modelMapper.map(brandRepository.save(brand), BrandResponseDto.class);
    }

    @Override
    @Transactional
    public void removeBrand(Long id) {
        if(!brandRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with ID: " + id);
        }
        brandRepository.deleteById(id);
    }
}
