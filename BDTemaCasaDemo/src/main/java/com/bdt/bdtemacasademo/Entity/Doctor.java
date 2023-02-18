package com.bdt.bdtemacasademo.Entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Doctor {

    private int doctorID;
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date hireDate;

    public Doctor()
    {

    }

    public Doctor(int doctorID, String name, Date hireDate) {
        this.doctorID = doctorID;
        this.name = name;
        this.hireDate = hireDate;
    }
}
