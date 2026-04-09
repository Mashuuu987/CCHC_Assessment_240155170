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

import ict.bean.NotificationBean;

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
                + "type ENUM('NORMAL','URGENT','IMPORTANT') NOT NULL, "
                + "title VARCHAR(100), "
                + "message VARCHAR(255) NOT NULL,"
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "isRead BOOLEAN NOT NULL DEFAULT FALSE,"
                + "PRIMARY KEY (notificationId),"
                + "CONSTRAINT fk_notif_user FOREIGN KEY (userId) REFERENCES userInfo(userId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int createNotification(int userId,
            String type,
            String title,
            String message) {

        int notificationId = -1;
        String sql = "INSERT INTO notification (userId, type, title, message) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, title);
            ps.setString(4, message);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        notificationId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationId;
    }
    
    public void insertDefaultNotificationIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM notification";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                createNotification(1, "NORMAL", "Hello Banana", "Yo Banana ! YO ! YO !");
                createNotification(1, "URGENT", "Bye Banana", "Ya Banana ! YA ! YA !");
                createNotification(1, "IMPORTANT", "GG Banana", "Ho Banana ! HO ! HO !");
                createNotification(1, "NORMAL", "Joe Banana", "Wa Banana ! WA ! WA !");
                createNotification(1, "NORMAL", "HAHA Banana", "HA Banana ! HA ! HA !");
            }
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
                    Boolean read = rs.getBoolean("isRead");

                    NotificationBean bean = new NotificationBean(
                            notificationId, userId, type, title, message, createdAt,read);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<NotificationBean> getNotificationsByNotificationsId(int notificationId) {
        List<NotificationBean> list = new ArrayList<>();
        String sql = "SELECT * FROM notification "
                + "WHERE notificationId = ? ";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, notificationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("userId");
                    String type = rs.getString("type");
                    String title = rs.getString("title");
                    String message = rs.getString("message");
                    String createdAt = rs.getString("createdAt");
                    Boolean read = rs.getBoolean("isRead");

                    NotificationBean bean = new NotificationBean(
                            notificationId, userId, type, title, message, createdAt,read);
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
    
    public boolean isRead(int notificationId){
        boolean isRead = false;
        String sql = "SELECT * FROM notification "
                + "WHERE notificationId = ? ";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, notificationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    isRead = rs.getBoolean("isRead");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRead;
    }

    public void markAsRead(int notificationId) {
        String sql = "UPDATE notification SET isRead = TRUE WHERE notificationId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
