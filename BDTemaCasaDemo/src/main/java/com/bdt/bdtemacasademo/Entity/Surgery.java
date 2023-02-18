package com.bdt.bdtemacasademo.Entity;

import lombok.Data;

@Data
public class Surgery {

    private int surgery_id;
    private String surgery_name;
    private int price;

    public Surgery(int surgery_id, String surgery_name, int price) {
        this.surgery_id = surgery_id;
        this.surgery_name = surgery_name;
        this.price = price;
    }
    public Surgery(){}
}
