package com.vlkevheniy.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class MessageDto {
    private String to;
    private String subject;
    private String content;
}
