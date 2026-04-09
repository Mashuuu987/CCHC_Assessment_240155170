/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.QueueTicketBean;
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
