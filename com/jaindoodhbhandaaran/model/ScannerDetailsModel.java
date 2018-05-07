package com.jaindoodhbhandaaran.model;

import java.io.Serializable;

public class ScannerDetailsModel implements Serializable {
    String aadhar;
    String address;
    String id;
    String milk_quality;
    String name;
    String phone;
    String type;
    String work;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getWork() {
        return this.work;
    }

    public void setWork(String str) {
        this.work = str;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String str) {
        this.phone = str;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public String getAadhar() {
        return this.aadhar;
    }

    public void setAadhar(String str) {
        this.aadhar = str;
    }

    public String getMilk_quality() {
        return this.milk_quality;
    }

    public void setMilk_quality(String str) {
        this.milk_quality = str;
    }
}
