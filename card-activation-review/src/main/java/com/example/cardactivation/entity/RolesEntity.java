package com.example.cardactivation.entity;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class RolesEntity {


    private Long id;

    private String name;

    private Timestamp created;

    private Timestamp updated;

}
