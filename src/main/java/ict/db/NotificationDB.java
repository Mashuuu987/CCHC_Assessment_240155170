/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.NotificationBean;
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
public class NotificationDB {

    private String url;
    private String username;
    private String password;

    public NotificationDB(String url, String username, String password) {
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

    public void createNotificationTable() {
        String sql = "CREATE TABLE IF NOT EXISTS notification ("
                + "notificationId INT AUTO_INCREMENT, "
                + "userId INT NOT NULL, "
                + "type VARCHAR(50) NOT NULL, "
                + "title VARCHAR(100), "
                + "message VARCHAR(255) NOT NULL,"
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (notificationId),"
                + "CONSTRAINT fk_notif_user FOREIGN KEY (userId) REFERENCES userInfo(userId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<NotificationBean> getNotificationsByUserId(int userId) {
        List<NotificationBean> list = new ArrayList<>();
        String sql = "SELECT * FROM notification "
                + "WHERE userId = ? "
                + "ORDER BY createdAt DESC";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int notificationId = rs.getInt("notificationId");
                    String type = rs.getString("type");
                    String title = rs.getString("title");
                    String message = rs.getString("message");
                    String createdAt = rs.getString("createdAt");

                    NotificationBean bean = new NotificationBean(
                            notificationId, userId, type, title, message, createdAt);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delNotification(int notificationId) {
        String sql = "DELETE FROM notification WHERE notificationId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
