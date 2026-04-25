/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import java.sql.*;
import java.util.Optional;

/**
 *
 * @author amzte
 */
public class PolicyDB {

    private final String url;
    private final String username;
    private final String password;

    private static final String KEY_MAX_ACTIVE = "MAX_ACTIVE_APPOINTMENTS";
    private static final String DEFAULT_MAX_ACTIVE = "3";

    public PolicyDB(String url, String username, String password) {
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

    public void createPolicyTable() {
        String sql = "CREATE TABLE IF NOT EXISTS system_policy ("
                + "policyId INT AUTO_INCREMENT PRIMARY KEY, "
                + "policyKey VARCHAR(60) NOT NULL, "
                + "policyValue VARCHAR(120) NOT NULL, "
                + "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                + "ON UPDATE CURRENT_TIMESTAMP, "
                + "UNIQUE KEY uq_policy_key (policyKey)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultIfMissing(String key, String value) {
        String sql = "INSERT INTO system_policy(policyKey, policyValue) "
                + "SELECT ?, ? WHERE NOT EXISTS "
                + "(SELECT 1 FROM system_policy WHERE policyKey = ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setString(3, key);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<String> getPolicyValue(String key) {
        String sql = "SELECT policyValue FROM system_policy WHERE policyKey = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Integer getPolicyInt(String key) {
        return getPolicyValue(key).map(v -> {
            try {
                return Integer.parseInt(v.trim());
            } catch (Exception e) {
                return null;
            }
        }).orElse(null);
    }

    public boolean upsertPolicyValue(String key, String value) {
        String sql = "INSERT INTO system_policy(policyKey, policyValue) VALUES(?, ?) "
                + "ON DUPLICATE KEY UPDATE policyValue = VALUES(policyValue)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void ensureDefaults() {
        createPolicyTable();
        insertDefaultIfMissing(KEY_MAX_ACTIVE, DEFAULT_MAX_ACTIVE);
    }

    public int getMaxActiveAppointments() {
        Integer v = getPolicyInt(KEY_MAX_ACTIVE);
        if (v == null) {
            return Integer.parseInt(DEFAULT_MAX_ACTIVE);
        }
        return v;
    }

    public boolean setMaxActiveAppointments(int value) {
        return upsertPolicyValue(KEY_MAX_ACTIVE, String.valueOf(value));
    }

}
