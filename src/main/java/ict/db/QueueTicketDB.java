/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ict.bean.QueueTicketBean;

/**
 *
 * @author amzte
 */
public class QueueTicketDB {
    private String url;
    private String username;
    private String password;

    public QueueTicketDB(String url, String username, String password) {
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

    public void createQueueTicketTable() {
        String sql = "CREATE TABLE IF NOT EXISTS queueTicket ("
                + "ticketId INT AUTO_INCREMENT, "
                + "patientId INT NOT NULL, "
                + "clinicId INT NOT NULL, "
                + "serviceId INT NOT NULL, "
                + "queueDate DATE NOT NULL,"
                + "queueNumber INT NOT NULL,"
                + "status ENUM('WAITING','CALLED','SKIPPED','SERVED','EXPIRED') NOT NULL, "
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (ticketId),"
                + "CONSTRAINT fk_queue_patient FOREIGN KEY (patientId) REFERENCES patient_profile(patientId),"
                + "CONSTRAINT fk_queue_clinic FOREIGN KEY (clinicId) REFERENCES clinic(clinicId),"
                + "CONSTRAINT fk_queue_service FOREIGN KEY (serviceId) REFERENCES service(serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<QueueTicketBean> getTicketsByPatientId(int patientId) {
        List<QueueTicketBean> list = new ArrayList<>();
        String sql = "SELECT * FROM queueTicket WHERE patientId = ? ORDER BY queueDate DESC, ticketId DESC";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ticketId = rs.getInt("ticketId");
                    int clinicId = rs.getInt("clinicId");
                    int serviceId = rs.getInt("serviceId");
                    String queueDate = rs.getString("queueDate");
                    int queueNumber = rs.getInt("queueNumber");
                    String status = rs.getString("status");
                    String createdAt = rs.getString("createdAt");

                    QueueTicketBean bean = new QueueTicketBean(
                            ticketId, patientId, clinicId, serviceId,
                            queueDate, queueNumber, status, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<QueueTicketBean> getTicketsByClinicServiceDate(int clinicId, int serviceId, String queueDate) {
        List<QueueTicketBean> list = new ArrayList<>();
        String sql = "SELECT * FROM queueTicket WHERE clinicId = ? AND serviceId = ? AND queueDate = ? ORDER BY queueNumber ASC";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, queueDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public QueueTicketBean getCurrentCalledTicket(int clinicId, int serviceId, String queueDate) {
        QueueTicketBean bean = null;
        String sql = "SELECT * FROM queueTicket WHERE clinicId = ? AND serviceId = ? AND queueDate = ? AND status = 'CALLED' ORDER BY queueNumber ASC LIMIT 1";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public QueueTicketBean getNextWaitingTicket(int clinicId, int serviceId, String queueDate) {
        QueueTicketBean bean = null;
        String sql = "SELECT * FROM queueTicket WHERE clinicId = ? AND serviceId = ? AND queueDate = ? AND status = 'WAITING' ORDER BY queueNumber ASC LIMIT 1";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public QueueTicketBean getTicketByPatientClinicServiceDate(int patientId, int clinicId, int serviceId, String queueDate) {
        QueueTicketBean bean = null;
        String sql = "SELECT * FROM queueTicket WHERE patientId = ? AND clinicId = ? AND serviceId = ? AND queueDate = ?";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            ps.setString(4, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean hasTicketByPatientClinicServiceDate(int patientId, int clinicId, int serviceId, String queueDate) {
        return getTicketByPatientClinicServiceDate(patientId, clinicId, serviceId, queueDate) != null;
    }

    public QueueTicketBean getLatestTicketByPatientDate(int patientId, String queueDate) {
        QueueTicketBean bean = null;
        String sql = "SELECT * FROM queueTicket WHERE patientId = ? AND queueDate = ? ORDER BY ticketId DESC LIMIT 1";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public QueueTicketBean getLatestNonCalledTicketByPatientDate(int patientId, String queueDate) {
        QueueTicketBean bean = null;
        String sql = "SELECT * FROM queueTicket WHERE patientId = ? AND queueDate = ? AND status = 'WAITING' ORDER BY ticketId DESC LIMIT 1";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setString(2, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = new QueueTicketBean(
                            rs.getInt("ticketId"),
                            rs.getInt("patientId"),
                            rs.getInt("clinicId"),
                            rs.getInt("serviceId"),
                            rs.getString("queueDate"),
                            rs.getInt("queueNumber"),
                            rs.getString("status"),
                            rs.getString("createdAt"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean hasNonCalledTicketByPatientDate(int patientId, String queueDate) {
        return getLatestNonCalledTicketByPatientDate(patientId, queueDate) != null;
    }

    public int getNextQueueNumber(int clinicId, int serviceId, String queueDate) {
        int next = 1;
        String sql = "SELECT COALESCE(MAX(queueNumber), 0) + 1 AS nextQueueNumber FROM queueTicket WHERE clinicId = ? AND serviceId = ? AND queueDate = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    next = rs.getInt("nextQueueNumber");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return next;
    }

    public int countTicketsByClinicServiceDate(int clinicId, int serviceId, String queueDate) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM queueTicket WHERE clinicId = ? AND serviceId = ? AND queueDate = ? AND status IN ('WAITING','CALLED')";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setString(3, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int createQueueTicket(int patientId, int clinicId, int serviceId, String queueDate, int queueNumber, String status) {
        int ticketId = -1;
        String sql = "INSERT INTO queueTicket (patientId, clinicId, serviceId, queueDate, queueNumber, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            ps.setString(4, queueDate);
            ps.setInt(5, queueNumber);
            ps.setString(6, status);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticketId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticketId;
    }

    public boolean delTicket(int ticketId) {
        String sql = "DELETE FROM queueTicket WHERE ticketId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTicketStatus(int ticketId, String newStatus) {
        String sql = "UPDATE queueTicket SET status = ? WHERE ticketId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, ticketId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
