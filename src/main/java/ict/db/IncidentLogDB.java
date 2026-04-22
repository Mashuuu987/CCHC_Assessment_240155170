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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                + "title VARCHAR(50) NOT NULL,"
                + "description TEXT NOT NULL, "
                + "severity ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL, "
                + "status ENUM('OPEN','CLOSED') DEFAULT 'OPEN', "
                + "occurred DATETIME NOT NULL,"
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

    public int createIncident(int staffId, int clinicId, Integer serviceId, String title,
            String description, String severity, String occurred) {
        int incidentId = -1;
        String sql = "INSERT INTO incident_log "
                + "(staffId, clinicId, serviceId, title, description, severity, occurred) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, staffId);
            ps.setInt(2, clinicId);
            if (serviceId == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, serviceId);
            }
            ps.setString(4, title);
            ps.setString(5, description);
            ps.setString(6, severity);

            // yyyy-MM-ddTHH:mm → yyyy-MM-dd HH:mm:ss
            String fixedOccurred = occurred.replace('T', ' ') + ":00";
            ps.setString(7, fixedOccurred);

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

    public void insertDefaultIncidentIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM incident_log";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                String t1 = LocalDateTime.now().minusHours(3).format(fmt);
                String t2 = LocalDateTime.now().minusHours(1).format(fmt);

                createIncident(
                        1, 1, null, "Doctor unavailable",
                        "Doctor is unavailable temporarily. Please advise patients and adjust queue/appointments if needed.",
                        "HIGH", t1);

                createIncident(1, 1, 2, "Service temporarily suspended",
                        "Vaccination service is temporarily suspended due to supply or staffing constraints.",
                        "MEDIUM", t2);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public IncidentLogBean getIncidentById(int incidentId) {
        IncidentLogBean bean = null;
        String sql = "SELECT * FROM incident_log WHERE incidentId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new IncidentLogBean(
                            rs.getInt("incidentId"),
                            rs.getInt("staffId"),
                            rs.getInt("clinicId"),
                            (Integer) rs.getObject("serviceId"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("severity"),
                            rs.getString("status"),
                            rs.getString("occurred"),
                            rs.getString("createdAt")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean closeIncidentForClinic(int incidentId, int clinicId) {
        String sql = "UPDATE incident_log SET status = 'CLOSED' WHERE incidentId = ? AND clinicId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            ps.setInt(2, clinicId);
            return ps.executeUpdate() > 0;
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
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String occurred = rs.getString("occurred");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, clinicId, serviceId, title,
                            description, severity, status, occurred, createdAt);
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
                + "WHERE YEAR(occurred) = ? AND MONTH(occurred) = ? ";
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
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String occurred = rs.getString("occurred");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, cid, serviceId, title,
                            description, severity, status, occurred, createdAt);
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
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String severity = rs.getString("severity");
                    String status = rs.getString("status");
                    String occurred = rs.getString("occurred");
                    String createdAt = rs.getString("createdAt");

                    IncidentLogBean bean = new IncidentLogBean(
                            incidentId, staffId, clinicId, serviceId, title,
                            description, severity, status, occurred, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<IncidentLogBean> searchIncidentsfitter(
            Integer clinicId, String staffName, Integer serviceId, boolean clinicOnly,
            Integer occurredYear, Integer occurredMonth,
            Integer createdYear, Integer createdMonth,
            String status, String severity, String keyword) {

        List<IncidentLogBean> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT il.* FROM incident_log il "
                + "JOIN staff_profile sp ON il.staffId = sp.staffId "
                + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (clinicId != null) {
            sql.append(" AND il.clinicId = ? ");
            params.add(clinicId);
        }

        if (staffName != null && !staffName.isBlank()) {
            sql.append(" AND (sp.firstName LIKE ? OR sp.lastName LIKE ? OR CONCAT(sp.firstName,' ',sp.lastName) LIKE ?) ");
            String kw = "%" + staffName.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (clinicOnly) {
            sql.append(" AND il.serviceId IS NULL ");
        } else if (serviceId != null) {
            sql.append(" AND il.serviceId = ? ");
            params.add(serviceId);
        }

        if (occurredYear != null) {
            sql.append(" AND YEAR(il.occurred) = ? ");
            params.add(occurredYear);
        }
        if (occurredMonth != null) {
            sql.append(" AND MONTH(il.occurred) = ? ");
            params.add(occurredMonth);
        }

        if (createdYear != null) {
            sql.append(" AND YEAR(il.createdAt) = ? ");
            params.add(createdYear);
        }
        if (createdMonth != null) {
            sql.append(" AND MONTH(il.createdAt) = ? ");
            params.add(createdMonth);
        }

        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND il.status = ? ");
            params.add(status);
        }

        if (severity != null && !"ALL".equalsIgnoreCase(severity)) {
            sql.append(" AND il.severity = ? ");
            params.add(severity);
        }

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (il.title LIKE ? OR il.description LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
        }

        sql.append(" ORDER BY il.createdAt DESC, il.occurred DESC, il.incidentId DESC ");

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, String.valueOf(p));
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IncidentLogBean(
                            rs.getInt("incidentId"),
                            rs.getInt("staffId"),
                            rs.getInt("clinicId"),
                            (Integer) rs.getObject("serviceId"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("severity"),
                            rs.getString("status"),
                            rs.getString("occurred"),
                            rs.getString("createdAt")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
