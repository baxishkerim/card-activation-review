package com.example.cardactivation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardActivateResponseDTO {

    private String status;
    private int  code;
    private ResponseToken token;

}
