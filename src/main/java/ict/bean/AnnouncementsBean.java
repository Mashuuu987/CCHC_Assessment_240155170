/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.bean;

/**
 *
 * @author amzte
 */
public class AnnouncementsBean {
    
    private int announcementId;
    private String title;
    private String content;
    private String type; //ENUM('NORMAL','URGENT','IMPORTANT')
    private String status; // ENUM('PUBLISHED','DRAFT')
    private String publishTime;
    private String createdAt;
    private String updatedAt;

    public AnnouncementsBean() {
    }

    public AnnouncementsBean(int announcementId, String title, String content, String type, String status, String publishTime, String createdAt, String updatedAt) {
        this.announcementId = announcementId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.publishTime = publishTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    
}
