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
public class PatientProfileBean implements Serializable {

    private int patientId;
    private int userId;
    private String HKID;
    private String firstName;
    private String lastName;
    private String gender;
    private String DOB;
    private String phone;
    private String email;
    private String address;
    private String emergencyContactFullName;
    private String emergencyContact;

    public PatientProfileBean() {
    }

    public PatientProfileBean(int patientId, int userId, String HKID, String firstName, String lastName,
            String gender, String DOB, String phone, String email, String address, String emergencyContactFullName, String emergencyContact) {
        this.patientId = patientId;
        this.userId = userId;
        this.HKID = HKID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.DOB = DOB;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyContactFullName = emergencyContactFullName;
        this.emergencyContact = emergencyContact;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactFullName() {
        return emergencyContactFullName;
    }

    public void setEmergencyContactFullName(String emergencyContactFullName) {
        this.emergencyContactFullName = emergencyContactFullName;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

}
