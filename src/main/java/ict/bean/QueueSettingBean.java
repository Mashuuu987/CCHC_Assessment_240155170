package ict.bean;

import java.io.Serializable;

public class QueueSettingBean implements Serializable {

    private int settingId;
    private int clinicId;
    private String clinicName;
    private String clinicOpenTime;
    private String clinicCloseTime;
    private String clinicCloseDay;
    private int serviceId;
    private String serviceName;
    private String serviceType;
    private int durationMins;
    private boolean allowIssueTicket;
    private boolean enabled;
    private int maxTicketsPerDay;

    public QueueSettingBean() {
    }

    public QueueSettingBean(int settingId, int clinicId, String clinicName, String clinicOpenTime,
            String clinicCloseTime, String clinicCloseDay, int serviceId, String serviceName,
            String serviceType, int durationMins, boolean allowIssueTicket, boolean enabled,
            int maxTicketsPerDay) {
        this.settingId = settingId;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.clinicOpenTime = clinicOpenTime;
        this.clinicCloseTime = clinicCloseTime;
        this.clinicCloseDay = clinicCloseDay;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.durationMins = durationMins;
        this.allowIssueTicket = allowIssueTicket;
        this.enabled = enabled;
        this.maxTicketsPerDay = maxTicketsPerDay;
    }

    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicOpenTime() {
        return clinicOpenTime;
    }

    public void setClinicOpenTime(String clinicOpenTime) {
        this.clinicOpenTime = clinicOpenTime;
    }

    public String getClinicCloseTime() {
        return clinicCloseTime;
    }

    public void setClinicCloseTime(String clinicCloseTime) {
        this.clinicCloseTime = clinicCloseTime;
    }

    public String getClinicCloseDay() {
        return clinicCloseDay;
    }

    public void setClinicCloseDay(String clinicCloseDay) {
        this.clinicCloseDay = clinicCloseDay;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public boolean isAllowIssueTicket() {
        return allowIssueTicket;
    }

    public void setAllowIssueTicket(boolean allowIssueTicket) {
        this.allowIssueTicket = allowIssueTicket;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxTicketsPerDay() {
        return maxTicketsPerDay;
    }

    public void setMaxTicketsPerDay(int maxTicketsPerDay) {
        this.maxTicketsPerDay = maxTicketsPerDay;
    }
}
