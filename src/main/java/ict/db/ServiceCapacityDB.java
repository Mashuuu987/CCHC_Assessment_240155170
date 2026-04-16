/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.ServiceCapacityBean;
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
public class ServiceCapacityDB {

    private String url;
    private String username;
    private String password;

    public ServiceCapacityDB(String url, String username, String password) {
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

    public void createServiceCapacityTable() {
        String sql = "CREATE TABLE IF NOT EXISTS service_capacity ("
                + "capacityId INT AUTO_INCREMENT, "
                + "clinicId INT NOT NULL, "
                + "serviceId INT NOT NULL, "
                + "timeSlot VARCHAR(20) NOT NULL, "
                + "quota INT NOT NULL,"
                + "PRIMARY KEY (capacityId),"
                + "UNIQUE (clinicId, serviceId, timeSlot),"
                + "CONSTRAINT fk_cap_clinic FOREIGN KEY (clinicId) REFERENCES clinic(clinicId),"
                + "CONSTRAINT fk_cap_service FOREIGN KEY (serviceId) REFERENCES service(serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createCapacityRule(int clinicId, int serviceId, String timeSlot, int quota) {
        int capacityId = -1;
        String sql = "INSERT INTO service_capacity (clinicId, serviceId, timeSlot, quota) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, timeSlot);
            ps.setInt(4, quota);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        capacityId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return capacityId;
    }

    public void insertDefaultCapacitiesIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM service_capacity";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                String insertSql = "INSERT INTO service_capacity "
                        + "(clinicId, serviceId, timeSlot, quota) VALUES "
                        + "(1, 1, '09:00', 2),(1, 1, '10:00', 10),(1, 1, '11:00', 10),(1, 1, '12:00', 10),(1, 1, '13:00', 10),"
                        + "(1, 1, '14:00', 10),(1, 1, '15:00', 10),(1, 1, '16:00', 10),(1, 1, '17:00', 10),(1, 1, '18:00', 10),"
                        + "(1, 2, '14:00', 10),(1, 2, '15:00', 10),(1, 2, '16:00', 10),(1, 3, '17:00', 10),(1, 3, '18:00', 10),"
                        + "(2, 1, '08:00', 8),"
                        + "(2, 1, '10:00', 8),"
                        + "(3, 1, '09:00', 12),"
                        + "(4, 1, '09:00', 6),"
                        + "(5, 1, '09:00', 10)";

                try (PreparedStatement psInsert = c.prepareStatement(insertSql)) {
                    psInsert.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServiceCapacityBean getCapacity(int clinicId, int serviceId, String timeSlot) {
        ServiceCapacityBean bean = null;
        String sql = "SELECT * FROM service_capacity "
                + "WHERE clinicId = ? AND serviceId = ? AND timeSlot = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, timeSlot);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int capacityId = rs.getInt("capacityId");
                    int cid = rs.getInt("clinicId");
                    int sid = rs.getInt("serviceId");
                    String ts = rs.getString("timeSlot");
                    int quota = rs.getInt("quota");
                    bean = new ServiceCapacityBean(capacityId, cid, sid, ts, quota);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean updateCapacity(int capacityId, int newQuota) {
        String sql = "UPDATE service_capacity SET quota = ? WHERE capacityId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, newQuota);
            ps.setInt(2, capacityId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ServiceCapacityBean> getCapacityByClinicService(int clinicId, int serviceId) {
        List<ServiceCapacityBean> list = new ArrayList<>();
        String sql = "SELECT * FROM service_capacity "
                + "WHERE clinicId = ? AND serviceId = ? "
                + "ORDER BY timeSlot";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int capacityId = rs.getInt("capacityId");
                    String ts = rs.getString("timeSlot");
                    int quota = rs.getInt("quota");
                    ServiceCapacityBean bean
                            = new ServiceCapacityBean(capacityId, clinicId, serviceId, ts, quota);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<ServiceCapacityBean> getAllServiceCapacity() {
        List<ServiceCapacityBean> list = new ArrayList<>();
        String sql = "SELECT * FROM service_capacity";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int capacityId = rs.getInt("capacityId");
                int clinicId = rs.getInt("clinicId");
                int serviceId = rs.getInt("serviceId");
                String timeSlot = rs.getString("timeSlot");
                int quota = rs.getInt("quota");

                ServiceCapacityBean bean = new ServiceCapacityBean(
                        capacityId, clinicId, serviceId, timeSlot, quota);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}