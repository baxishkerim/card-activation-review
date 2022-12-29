package com.example.cardactivation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CardActivateRequestDTO {

    @NotNull(message = "Please, insert pan")
    @NotBlank(message = "Please, insert pan")
    private String pan;
    @JsonProperty("activate_sms_notification")
    private Boolean activateSmsNotification;

}
