package com.bdt.bdtemacasademo.Entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private int doctorID;
    private String email;
    private String password;

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
