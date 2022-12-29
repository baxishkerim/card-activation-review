package com.example.cardactivation.entity;


import com.example.cardactivation.dto.enums.Status;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UsersEntity {


    private Long id;

    private  String username;

    private String  password;

    private Status status;

    private Timestamp created;

    private Timestamp updated;

    private String executor;



}
