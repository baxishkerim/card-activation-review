package com.example.cardactivation.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Error {


    NETWORK_ERROR("Network error", 1),
    ACCESS_DENIED("Access denied", 2),
    INTERNAL_ERROR("Internal error", 3),
    VALIDATION_ERROR("Validation error", 4),
    SERVICE_ERROR("Service error", 5),
    PROCESSING_ERROR("Processing error", 6)
    ;

    private final String errorDescription;
    private final Integer errorCode;
}
