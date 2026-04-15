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
public class StaffProfileBean implements Serializable {
    
    private int staffId;
    private int userId;
    private String HKID;
    private String firstName;
    private String lastName;
    private String gender; //ENUM('M','F')
    private String DOB;
    private Integer clinicId;
    private String position;

    public StaffProfileBean() {
    }

    public StaffProfileBean(int staffId, int userId, String HKID, String firstName, String lastName, 
            String gender, String DOB, Integer clinicId, String position) {
        this.staffId = staffId;
        this.userId = userId;
        this.HKID = HKID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.DOB = DOB;
        this.clinicId = clinicId;
        this.position = position;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHKID() {
        return HKID;
    }

    public void setHKID(String HKID) {
        this.HKID = HKID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getGender(){
        return gender;
    }
    
    public void setGender(String gender){
        this.gender = gender;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    } 

    public Integer getClinicId() {
        return clinicId;
    }

    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    
    
}
