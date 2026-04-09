/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.db;

import ict.bean.ClinicBean;
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
public class ClinicDB {

    private String url;
    private String username;
    private String password;

    public ClinicDB(String url, String username, String password) {
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

    public void createClinicTable() {
        String sql = "CREATE TABLE IF NOT EXISTS clinic ("
                + "clinicId INT AUTO_INCREMENT, "
                + "name VARCHAR(100) NOT NULL, "
                + "district VARCHAR(50), "
                + "address VARCHAR(200), "
                + "openTime TIME,"
                + "closeTime TIME,"
                + "closeDay VARCHAR(20),"
                + "PRIMARY KEY (clinicId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultClinicsIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM clinic";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                String insertSql = "INSERT INTO clinic (name, district, address, openTime, closeTime, closeDay) VALUES "
                        + "('Chai Wan Clinic', 'Chai Wan', 'Chai Wan Address', '09:00', '18:00', 'Sunday'),"
                        + "('Tseung Kwan O Clinic', 'Tseung Kwan O', 'TKO Address', '08:00', '17:00', 'Monday'),"
                        + "('Sha Tin Clinic', 'Sha Tin', 'Sha Tin Address', '07:00', '17:00', 'Sunday'),"
                        + "('Tuen Mun Clinic', 'Tuen Mun', 'Tuen Mun Address', '09:00', '19:00', NULL),"
                        + "('Tsing Yi Clinic', 'Tsing Yi', 'Tsing Yi Address', '08:00', '20:00', NULL)";
                try (PreparedStatement psInsert = c.prepareStatement(insertSql)) {
                    psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int createClinic(String name,String district,String address, String openTime, String closeTime, String closeDay) {
        int clinicId = -1;
        String sql = "INSERT INTO clinic (name, district, address, openTime, closeTime, closeDay) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, district);
            ps.setString(3, address);
            ps.setString(4, openTime);
            ps.setString(5, closeTime);
            ps.setString(6, closeDay);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        clinicId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clinicId;
    }

    public List<ClinicBean> getAllClinics() {
        List<ClinicBean> list = new ArrayList<>();
        String sql = "SELECT * FROM clinic ORDER BY clinicId";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int clinicId = rs.getInt("clinicId");
                String name = rs.getString("name");
                String district = rs.getString("district");
                String address = rs.getString("address");
                String openTime = rs.getString("openTime");
                String closeTime = rs.getString("closeTime");
                String closeDay = rs.getString("closeDay");
                ClinicBean bean = new ClinicBean(clinicId, name, district, address, openTime, closeTime, closeDay);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ClinicBean getClinicByID(int id) {
        ClinicBean bean = null;
        String sql = "SELECT * FROM clinic WHERE clinicId = ?";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int clinicId = rs.getInt("clinicId");
                    String name = rs.getString("name");
                    String district = rs.getString("district");
                    String address = rs.getString("address");
                    String openTime = rs.getString("openTime");
                    String closeTime = rs.getString("closeTime");
                    String closeDay = rs.getString("closeDay");
                    bean = new ClinicBean(clinicId, name, district, address, openTime, closeTime, closeDay);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public List<ClinicBean> getClinicsByName(String name) {
        List<ClinicBean> list = new ArrayList<>();
        String sql = "SELECT * FROM clinic WHERE name LIKE ? ORDER BY clinicId";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int clinicId = rs.getInt("clinicId");
                    String cName = rs.getString("name");
                    String district = rs.getString("district");
                    String address = rs.getString("address");
                    String openTime = rs.getString("openTime");
                    String closeTime = rs.getString("closeTime");
                    String closeDay = rs.getString("closeDay");
                    ClinicBean bean = new ClinicBean(clinicId, cName, district, address, openTime, closeTime, closeDay);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean delClinic(int clinicId) {
        String sql = "DELETE FROM clinic WHERE clinicId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean editClinic(ClinicBean clinic) {
        String sql = "UPDATE clinic SET name = ?, district = ?, address = ? , openTime = ? , closeTime = ? , closeDay = ? WHERE clinicId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, clinic.getName());
            ps.setString(2, clinic.getDistrict());
            ps.setString(3, clinic.getAddress());
            ps.setString(4, clinic.getOpenTime());
            ps.setString(5, clinic.getCloseTime());
            ps.setString(6, clinic.getCloseDay());
            ps.setInt(7, clinic.getClinicId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
