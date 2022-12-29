package com.example.cardactivation.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BanksEntity {

    private Long id;
    private String name;
    private String initiatorRid;
    private Long institutionId;
    private Boolean isAgrigator;
    private LocalDateTime created;
    private LocalDateTime updated;






}
