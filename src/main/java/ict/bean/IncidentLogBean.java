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
public class IncidentLogBean implements Serializable {
    
    private int incidentId;
    private int staffId;
    private int clinicId;
    private Integer serviceId;
    private String title;
    private String description;
    private String severity;
    private String status;
    private String occurred;
    private String createdAt;

    public IncidentLogBean() {
    }

    public IncidentLogBean(int incidentId, int staffId, int clinicId, Integer serviceId, String title, String description,
            String severity, String status, String occurred, String createdAt) {
        this.incidentId = incidentId;
        this.staffId = staffId;
        this.clinicId = clinicId;
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.occurred = occurred;
        this.createdAt = createdAt;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOccurred() {
        return occurred;
    }

    public void setOccurred(String occurred) {
        this.occurred = occurred;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
