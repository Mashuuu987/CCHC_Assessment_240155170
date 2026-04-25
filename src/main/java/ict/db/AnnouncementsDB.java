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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ict.bean.AnnouncementsBean;

/**
 *
 * @author amzte
 */
public class AnnouncementsDB {

    private String url;
    private String username;
    private String password;

    public AnnouncementsDB(String url, String username, String password) {
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

    public void createAnnouncementTable() {
        String sql = "CREATE TABLE IF NOT EXISTS announcement ("
                + "announcementId INT AUTO_INCREMENT, "
                + "title VARCHAR(200) NOT NULL, "
                + "content TEXT NOT NULL, "
                + "type ENUM('NORMAL','URGENT','IMPORTANT') NOT NULL, "
                + "status ENUM('PUBLISHED','DRAFT') NOT NULL DEFAULT 'DRAFT',"
                + "publishTime DATETIME NULL,"
                + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (announcementId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createAnnouncement(String title, String content, String type, String status, String publishTime) {

        int announcementId = -1;
        String sql = "INSERT INTO announcement (title, content, type, status, publishTime) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, type);
            ps.setString(4, status);

            if (publishTime == null || publishTime.isEmpty()) {
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setString(5, publishTime);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        announcementId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return announcementId;
    }

    public void insertDefaultAnnouncementsIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM announcement";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                createAnnouncement("Do yo have a chicken?", "Chicken is so kawaii.", "NORMAL", "PUBLISHED", "");
                createAnnouncement("Do yo have a penguin?", "Penguin is so kawaii.", "NORMAL", "PUBLISHED", "");
                createAnnouncement("Do yo have a owl?", "Owl is so kawaii.", "NORMAL", "PUBLISHED", "");
                createAnnouncement("Do yo have a seal?", "Seal is so kawaii.", "NORMAL", "PUBLISHED", "");
                createAnnouncement("Do yo have a duck?", "Duck is so kawaii.", "NORMAL", "DRAFT", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AnnouncementsBean> getLatestVisibleAnnouncements(int limit) {
        List<AnnouncementsBean> list = new ArrayList<>();
        String sql = "SELECT * FROM announcement "
                + "WHERE status = 'PUBLISHED' "
                + "AND (publishTime IS NULL OR publishTime <= NOW()) "
                + "ORDER BY publishTime DESC, announcementId DESC "
                + "LIMIT ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBean(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AnnouncementsBean> getAllAnnouncementsForAdmin() {
        List<AnnouncementsBean> list = new ArrayList<>();
        String sql = "SELECT * FROM announcement ORDER BY createdAt DESC, announcementId DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBean(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private AnnouncementsBean mapBean(ResultSet rs) throws SQLException {
        return new AnnouncementsBean(
                rs.getInt("announcementId"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("type"),
                rs.getString("status"),
                rs.getString("publishTime"),
                rs.getString("createdAt"),
                rs.getString("updatedAt")
        );
    }

    public AnnouncementsBean getAnnouncementById(int announcementId) {
        String sql = "SELECT * FROM announcement WHERE announcementId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, announcementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBean(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAnnouncement(AnnouncementsBean a) {
        String sql = "UPDATE announcement SET title = ?, content = ?, type = ?, status = ?, publishTime = ? "
                + "WHERE announcementId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getContent());
            ps.setString(3, a.getType());
            ps.setString(4, a.getStatus());

            if (a.getPublishTime() == null || a.getPublishTime().isBlank()) {
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setString(5, a.getPublishTime());
            }

            ps.setInt(6, a.getAnnouncementId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAnnouncement(int announcementId) {
        String sql = "DELETE FROM announcement WHERE announcementId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, announcementId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
