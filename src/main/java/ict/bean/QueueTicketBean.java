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
public class QueueTicketBean implements Serializable {
    
    private int ticketId;
    private int patientId;
    private int clinicId;
    private int serviceId;
    private String queueDate;
    private int queueNumber;
    private String status;
    private String createdAt;

    public QueueTicketBean() {
    }

    public QueueTicketBean(int ticketId, int patientId, int clinicId, int serviceId, String queueDate, int queueNumber, String status, String createdAt) {
        this.ticketId = ticketId;
        this.patientId = patientId;
        this.clinicId = clinicId;
        this.serviceId = serviceId;
        this.queueDate = queueDate;
        this.queueNumber = queueNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
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

    public String getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(String queueDate) {
        this.queueDate = queueDate;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
