/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.bean;

import java.io.Serializable;

/**
 *
 * @author amzte
 */
public class ClinicBean implements Serializable {
    
    private int clinicId;
    private String name;
    private String district;
    private String address;
    private String openTime;
    private String closeTime;
    private String closeDay;

    public ClinicBean() {
    }

    public ClinicBean(int clinicId, String name, String district, String address,
            String openTime, String closeTime, String closeDay) {
        this.clinicId = clinicId;
        this.name = name;
        this.district = district;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.closeDay = closeDay;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCloseDay() {
        return closeDay;
    }

    public void setCloseDay(String closeDay) {
        this.closeDay = closeDay;
    }
    
    
    
}
