package com.bdt.bdtemacasademo.Entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Patient {

    private int patientID;
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date requestTime;
    private String email;

    public Patient(int patientID, String name, Date requestTime, String email) {
        this.patientID = patientID;
        this.name = name;
        this.requestTime = requestTime;
        this.email = email;
    }

    public Patient(){}
}
