package com.vlkevheniy.carmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponseDto {
    private int successful;
    private int failed;
    private String message;
}
