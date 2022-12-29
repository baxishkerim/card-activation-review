package com.example.cardactivation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TxResponse {


    private String id;
    private String result;
    private String approvalCode;

    private String declineReason;

}
