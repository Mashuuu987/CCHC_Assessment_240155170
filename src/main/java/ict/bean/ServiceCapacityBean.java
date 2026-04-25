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
public class ServiceCapacityBean implements Serializable {
    
    private int capacityId;
    private int clinicId;
    private int serviceId;
    private String timeSlot;
    private int quota;

    public ServiceCapacityBean() {
    }

    public ServiceCapacityBean(int capacityId, int clinicId, int serviceId, String timeSlot, int quota) {
        this.capacityId = capacityId;
        this.clinicId = clinicId;
        this.serviceId = serviceId;
        this.timeSlot = timeSlot;
        this.quota = quota;
    }

    public int getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(int capacityId) {
        this.capacityId = capacityId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

}
