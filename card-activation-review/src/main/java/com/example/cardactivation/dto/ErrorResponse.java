package com.example.cardactivation.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {


    private String message;
    private String code;

}
