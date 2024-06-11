package com.vlkevheniy.carmanagement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageDto {
    private String to;
    private String subject;
    private String content;
}
