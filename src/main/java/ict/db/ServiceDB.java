/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.ServiceBean;
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
public class ServiceDB {

    private String url;
    private String username;
    private String password;

    public ServiceDB(String url, String username, String password) {
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

    public void createServiceTable() {
        String sql = "CREATE TABLE IF NOT EXISTS service ("
                + "serviceId INT AUTO_INCREMENT, "
                + "name VARCHAR(100) NOT NULL, "
                + "description VARCHAR(255), "
                + "serviceType VARCHAR(50), "
                + "durationMins INT NOT NULL, "
                + "PRIMARY KEY (serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultServicesIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM service";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                String insertSql = "INSERT INTO service (name, description, serviceType, durationMins) VALUES "
                        + "('General Consultation', 'General outpatient consultation with doctor', 'CONSULTATION', 15),"
                        + "('Vaccination', 'Standard vaccination service', 'VACCINATION', 10),"
                        + "('Basic Health Screening', 'Basic health check: BP, BMI, simple tests', 'SCREENING', 20)";

                try (PreparedStatement psInsert = c.prepareStatement(insertSql)) {
                    psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ServiceBean> getAllServices() {
        List<ServiceBean> list = new ArrayList<>();
        String sql = "SELECT * FROM service ORDER BY serviceId";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int serviceId = rs.getInt("serviceId");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String serviceType = rs.getString("serviceType");
                int durationMins = rs.getInt("durationMins");

                ServiceBean bean = new ServiceBean(
                        serviceId, name, description, serviceType, durationMins);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ServiceBean getServiceById(int id) {
        ServiceBean bean = null;
        String sql = "SELECT * FROM service WHERE serviceId = ?";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int serviceId = rs.getInt("serviceId");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String serviceType = rs.getString("serviceType");
                    int durationMins = rs.getInt("durationMins");

                    bean = new ServiceBean(
                            serviceId, name, description, serviceType, durationMins);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean delService(int serviceId) {
        String sql = "DELETE FROM service WHERE serviceId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean editService(ServiceBean service) {
        String sql = "UPDATE service SET name = ?, description = ?, serviceType = ? , durationMins = ? WHERE serviceId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, service.getName());
            ps.setString(2, service.getDescription());
            ps.setString(3, service.getServiceType());
            ps.setInt(4, service.getDurationMins());
            ps.setInt(5, service.getServiceId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createService(String name, String description, String serviceType, int durationMins) {
        int serviceId = -1;
        String sql = "INSERT INTO service (name, description, serviceType, durationMins) VALUES (?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, serviceType);
            ps.setInt(4, durationMins);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        serviceId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceId;
    }
}
