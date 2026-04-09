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
public class ServiceBean implements Serializable {
    
    private int serviceId;
    private String name;
    private String description;
    private String serviceType;
    private int durationMins;

    public ServiceBean() {
    }

    public ServiceBean(int serviceId, String name, String description, String serviceType, int durationMins) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.serviceType = serviceType;
        this.durationMins = durationMins;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(int durationMins) {
        this.durationMins = durationMins;
    }
    
}
