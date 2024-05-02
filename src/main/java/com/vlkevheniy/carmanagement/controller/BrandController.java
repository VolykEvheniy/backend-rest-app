package com.vlkevheniy.carmanagement.controller;

import com.vlkevheniy.carmanagement.dto.BrandRequestDto;
import com.vlkevheniy.carmanagement.dto.BrandResponseDto;
import com.vlkevheniy.carmanagement.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brand")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandResponseDto>> getAllBrands() {
        List<BrandResponseDto> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @PostMapping
    public ResponseEntity<BrandResponseDto> addBrand(@Valid @RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto savedBrand = brandService.addBrand(brandRequestDto);
        return ResponseEntity.ok(savedBrand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponseDto> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto updatedBrand = brandService.updateBrand(id, brandRequestDto);
        return ResponseEntity.ok(updatedBrand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeBrand(@PathVariable Long id) {
        brandService.removeBrand(id);
        return ResponseEntity.ok("Brand was deleted successfully");
    }


}
