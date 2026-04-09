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

import ict.bean.UserInfoBean;
import ict.util.PasswordUtil;

/**
 *
 * @author amzte
 */
public class UserDB {

    private String url;
    private String username;
    private String password;

    public UserDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
        return DriverManager.getConnection(url, username, password);
    }

    public void createUserInfoTable() {
        Connection c;
        Statement s;
        try {
            c = getConnection();
            s = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS userInfo("
                    + "userId INT AUTO_INCREMENT, "
                    + "username VARCHAR(50) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "role ENUM('PATIENT','STAFF','ADMIN') NOT NULL, "
                    + "PRIMARY KEY (userId), "
                    + "UNIQUE (username) "
                    + ")";
            s.execute(sql);
            s.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createUser(String user, String pwd, String role) {
        int generatedId = -1;
        String sql = "INSERT INTO userInfo (username, password, role) VALUES (?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashed = PasswordUtil.hashPassword(pwd);

            ps.setString(1, user);
            ps.setString(2, hashed);
            ps.setString(3, role);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public void insertDefaultUserIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM userInfo";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                createUser("Banana", "banana", "PATIENT");
                createUser("Apple", "apple", "STAFF");
                createUser("Orange", "orange", "ADMIN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserInfoBean> getAllUsers() {
        List<UserInfoBean> list = new ArrayList<>();
        String sql = "SELECT * FROM userInfo ORDER BY userId";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("userId");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                UserInfoBean bean = new UserInfoBean(userId, username, password, role);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public UserInfoBean getUserById(int id) {
        UserInfoBean bean = null;
        String sql = "SELECT * FROM userInfo WHERE userId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    bean = new UserInfoBean(userId, username, password, role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public UserInfoBean getUserByUsername(String user) {
        UserInfoBean bean = null;
        String sql = "SELECT * FROM userInfo WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    bean = new UserInfoBean(userId, username, password, role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean delUser(int userId) {
        String sql = "DELETE FROM userInfo WHERE userId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeUsername(int userId, String newUsername) {
        String checkSql = "SELECT userId FROM userInfo WHERE username = ? AND userId <> ?";
        String updateSql = "UPDATE userInfo SET username = ? WHERE userId = ?";

        try (Connection c = getConnection()) {
            try (PreparedStatement psCheck = c.prepareStatement(checkSql)) {
                psCheck.setString(1, newUsername);
                psCheck.setInt(2, userId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }

            try (PreparedStatement psUpdate = c.prepareStatement(updateSql)) {
                psUpdate.setString(1, newUsername);
                psUpdate.setInt(2, userId);
                int rows = psUpdate.executeUpdate();
                return rows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPwd, String newPwd) {
        String selectSql = "SELECT password FROM userInfo WHERE userId = ?";
        String updateSql = "UPDATE userInfo SET password = ? WHERE userId = ?";

        try (Connection c = getConnection()) {
            String stored = null;
            try (PreparedStatement psSelect = c.prepareStatement(selectSql)) {
                psSelect.setInt(1, userId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        stored = rs.getString("password");
                    } else {
                        return false;
                    }
                }
            }

            if (!PasswordUtil.verifyPassword(oldPwd, stored)) {
                return false;
            }

            String newHashed = PasswordUtil.hashPassword(newPwd);

            try (PreparedStatement psUpdate = c.prepareStatement(updateSql)) {
                psUpdate.setString(1, newHashed);
                psUpdate.setInt(2, userId);
                int rows = psUpdate.executeUpdate();
                return rows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidUser(String user, String pwd) {
        String sql = "SELECT password FROM userInfo WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    return PasswordUtil.verifyPassword(pwd, stored);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserInfoBean getUserByPatientId(int patientId) {
        UserInfoBean bean = null;
        String sql = "SELECT u.* FROM userInfo u "
                   + "JOIN patient_profile p ON u.userId = p.userId "
                   + "WHERE p.patientId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    bean = new UserInfoBean(userId, username, password, role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public UserInfoBean getUserByStaffId(int staffId) {
        UserInfoBean bean = null;
        String sql = "SELECT u.* FROM userInfo u "
                   + "JOIN staff_profile s ON u.userId = s.userId "
                   + "WHERE s.staffId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    bean = new UserInfoBean(userId, username, password, role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
