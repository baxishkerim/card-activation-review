package com.example.cardactivation.entity;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RangeEntity {

    private Long id;

    private String from;

    private String to;

    private boolean isActive;




}
