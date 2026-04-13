/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.AppointmentBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amzte
 */
public class AppointmentDB {

    private String url;
    private String username;
    private String password;

    public AppointmentDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
        return DriverManager.getConnection(url, username, password);
    }

    public void createAppointmentTable() {
        String sql = "CREATE TABLE IF NOT EXISTS appointment ("
                + "appointmentId INT AUTO_INCREMENT, "
                + "patientId INT NOT NULL, "
                + "clinicId INT NOT NULL, "
                + "serviceId INT NOT NULL, "
                + "appointmentDate DATE NOT NULL,"
                + "timeSlot VARCHAR(20) NOT NULL,"
                + "status ENUM('REQUESTED','CONFIRMED','COMPLETED','NO_SHOW','CANCELLED_BY_PATIENT','CANCELLED_BY_CLINIC') NOT NULL, "
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (appointmentId),"
                + "CONSTRAINT fk_appt_patient FOREIGN KEY (patientId) REFERENCES patient_profile(patientId),"
                + "CONSTRAINT fk_appt_clinic FOREIGN KEY (clinicId) REFERENCES clinic(clinicId),"
                + "CONSTRAINT fk_appt_service FOREIGN KEY (serviceId) REFERENCES service(serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AppointmentBean> getAppointmentsByPatientId(int patientId) {
        List<AppointmentBean> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE patientId = ? ORDER BY appointmentDate, timeSlot";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int appointmentId = rs.getInt("appointmentId");
                    int clinicId = rs.getInt("clinicId");
                    int serviceId = rs.getInt("serviceId");
                    String date = rs.getString("appointmentDate");
                    String timeSlot = rs.getString("timeSlot");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    AppointmentBean bean = new AppointmentBean(
                            appointmentId, patientId, clinicId, serviceId,
                            date, timeSlot, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<AppointmentBean> getAppointmentsById(int appointmentsId) {
        List<AppointmentBean> list = new ArrayList<>();
        String sql = "SELECT * FROM appointment WHERE appointmentId = ? ORDER BY appointmentDate, timeSlot";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, appointmentsId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int appointmentId = rs.getInt("appointmentId");
                    int clinicId = rs.getInt("clinicId");
                    int serviceId = rs.getInt("serviceId");
                    String date = rs.getString("appointmentDate");
                    String timeSlot = rs.getString("timeSlot");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    AppointmentBean bean = new AppointmentBean(
                            appointmentId, appointmentsId, clinicId, serviceId,
                            date, timeSlot, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // For admin
    public boolean delAppointment(int appointmentId) {
        String sql = "DELETE FROM appointment WHERE appointmentId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointment SET status = ? WHERE appointmentId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean rescheduleAppointment(int appointmentId, String newDate, String newTimeSlot) {
        String sql = "UPDATE appointment SET appointmentDate = ?, timeSlot = ? WHERE appointmentId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newDate);
            ps.setString(2, newTimeSlot);
            ps.setInt(3, appointmentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int countAppointments(int clinicId, int serviceId, String date, String timeSlot){
        String sql = "SELECT COUNT(*) FROM appointment "
                + "WHERE clinicId = ? AND serviceId = ? "
                + "AND appointmentDate = ? AND timeSlot = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, date);
            ps.setString(4, timeSlot);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return rs.getInt(1);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public int createAppointment(int patientId, int clinicId, int serviceId, String date, String timeSlot, String status){
        int appointmentId = -1;
        String sql = "INSERT INTO appointment "
                + "(patientId, clinicId, serviceId, appointmentDate, timeSlot, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, patientId);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            ps.setString(4, date);
            ps.setString(5, timeSlot);
            ps.setString(6, status);
            
            int rows = ps.executeUpdate();
            if (rows > 0){
                try (ResultSet rs = ps.getGeneratedKeys()){
                    if (rs.next()){
                        appointmentId = rs.getInt(1);
                    }
                }
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        return appointmentId;
    }
}
