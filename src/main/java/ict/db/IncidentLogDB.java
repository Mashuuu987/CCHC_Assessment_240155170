/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.IncidentLogBean;
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
public class IncidentLogDB {

    private String url;
    private String username;
    private String password;

    public IncidentLogDB(String url, String username, String password) {
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

    public void createIncidentLogTable() {
        String sql = "CREATE TABLE IF NOT EXISTS incident_log ("
                + "incidentId INT AUTO_INCREMENT, "
                + "staffId INT NOT NULL, "
                + "clinicId INT NOT NULL, "
                + "serviceId INT NULL, "
                + "description VARCHAR(255) NOT NULL, "
                + "severity VARCHAR(20), "
                + "status VARCHAR(20) DEFAULT 'OPEN', "
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY (incidentId), "
                + "CONSTRAINT fk_incident_staff FOREIGN KEY (staffId) REFERENCES staff_profile(staffId), "
                + "CONSTRAINT fk_incident_clinic FOREIGN KEY (clinicId) REFERENCES clinic(clinicId), "
                + "CONSTRAINT fk_incident_service FOREIGN KEY (serviceId) REFERENCES service(serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createIncident(int staffId, int clinicId, Integer serviceId,
            String description, String severity) {
        int incidentId = -1;
        String sql = "INSERT INTO incident_log "
                + "(staffId, clinicId, serviceId, description, severity) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, staffId);
            ps.setInt(2, clinicId);
            if (serviceId == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, serviceId);
            }
            ps.setString(4, description);
            ps.setString(5, severity);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        incidentId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return incidentId;
    }

    public boolean closeIncident(int incidentId) {
        String sql = "UPDATE incident_log SET status = 'CLOSED' WHERE incidentId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, incidentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<IncidentLogBean> getIncidentsByClinic(int clinicId) {
        List<IncidentLogBean> list = new ArrayList<>();
        String sql = "SELECT * FROM incident_log "
                + "WHERE clinicId = ? "
                + "ORDER BY createdAt DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clinicId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int incidentId = rs.getInt("incidentId");
                    int staffId = rs.getInt("staffId");
                    Integer serviceId = (Integer) rs.getObject("serviceId");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, clinicId, serviceId,
                            description, severity, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<IncidentLogBean> getIncidentsByMonth(int year, int month, Integer clinicId) {
        List<IncidentLogBean> list = new ArrayList<>();
        String sql = "SELECT * FROM incident_log "
                + "WHERE YEAR(createdAt) = ? AND MONTH(createdAt) = ? ";
        if (clinicId != null) {
            sql += "AND clinicId = ? ";
        }
        sql += "ORDER BY createdAt DESC";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            if (clinicId != null) {
                ps.setInt(3, clinicId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int incidentId = rs.getInt("incidentId");
                    int staffId = rs.getInt("staffId");
                    int cid = rs.getInt("clinicId");
                    Integer serviceId = (Integer) rs.getObject("serviceId");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, cid, serviceId,
                            description, severity, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<IncidentLogBean> getIncidentsByStaff(int staffId) {
        List<IncidentLogBean> list = new ArrayList<>();
        String sql = "SELECT * FROM incident_log "
                + "WHERE staffId = ? "
                + "ORDER BY createdAt DESC";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int incidentId = rs.getInt("incidentId");
                    int clinicId = rs.getInt("clinicId");
                    Integer serviceId = (Integer) rs.getObject("serviceId");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, clinicId, serviceId,
                            description, severity, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
