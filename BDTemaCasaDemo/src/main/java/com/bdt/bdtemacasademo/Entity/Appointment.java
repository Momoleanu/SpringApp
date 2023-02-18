package com.bdt.bdtemacasademo.Entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Appointment {

    private int appID;
    private int patientID;
    private int doctorID;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date rezultDate;
    private int surgeryID;

    private String hour;

    private String patient_name;

    private String surgery_name;

    private int prob_id;

    public Appointment(int appID, int patientID, int doctorID, Date appDate, Date rezultDate,
                       int surgeryID, String patient_name, String surgery_name,String hour) {

        this.appID = appID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appDate =appDate;
        this.rezultDate = rezultDate;
        this.surgeryID = surgeryID;
        this.patient_name=patient_name;
        this.surgery_name=surgery_name;
        this.hour=hour;
    }

    public Appointment(){}
}
