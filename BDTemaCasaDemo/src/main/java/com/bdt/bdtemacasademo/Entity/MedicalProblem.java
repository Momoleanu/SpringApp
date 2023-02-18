package com.bdt.bdtemacasademo.Entity;

import lombok.Data;

@Data
public class MedicalProblem {

    private int prob_id;
    private String prob_name;
    private int grade;

    public MedicalProblem(int prob_id, String prob_name, int grade) {
        this.prob_id = prob_id;
        this.prob_name = prob_name;
        this.grade = grade;

    }

    public MedicalProblem(){}
}
