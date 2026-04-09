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

import ict.bean.StaffProfileBean;

/**
 *
 * @author amzte
 */
public class StaffDB {

    private String url;
    private String username;
    private String password;

    public StaffDB(String url, String username, String password) {
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

    public void createStaffProfileTable() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS staff_profile ("
                    + "staffId INT AUTO_INCREMENT, "
                    + "userId INT NOT NULL, "
                    + "hkid VARCHAR(15) NOT NULL UNIQUE, "
                    + "firstName VARCHAR(50) NOT NULL, "
                    + "lastName VARCHAR(50) NOT NULL, "
                    + "gender ENUM('M','F') NOT NULL, "
                    + "dob DATE NOT NULL,"
                    + "clinicId INT, "
                    + "position VARCHAR(50), "
                    + "PRIMARY KEY (staffId), "
                    + "UNIQUE (userId), "
                    + "CONSTRAINT fk_staff_user "
                    + "FOREIGN KEY (userId) REFERENCES userInfo(userId)"
                    + ")";
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createStaffProfile(int userId,
            String HKID,
            String firstName,
            String lastName,
            String gender,
            String DOB,
            Integer clinicId,
            String position) {

        int staffId = -1;
        String sql = "INSERT INTO staff_profile (userId, hkid, firstName, lastName, gender, dob, clinicId, position) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, HKID);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setString(5, gender);
            java.sql.Date dob = java.sql.Date.valueOf(DOB);
            ps.setDate(6, dob);
            if (clinicId == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, clinicId);
            }
            ps.setString(8, position);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        staffId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staffId;
    }
    
    public void insertDefaultStaffIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM staff_profile";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                String insertSql = "INSERT INTO staff_profile (userId, hkid, firstName, lastName, gender, dob, clinicId, position) VALUES "
                        + "('2', 'A1234567', 'Apple','Wong','F','1993-12-10',1,'Nurse'),"
                        + "('3', 'O1234567', 'Orange','Hui','M','1996-06-03',NULL ,'Administrator')";
                try (PreparedStatement psInsert = c.prepareStatement(insertSql)) {
                    psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StaffProfileBean> getAllStaff() {
        List<StaffProfileBean> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_profile ORDER BY staffId";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int staffId = rs.getInt("staffId");
                int userId = rs.getInt("userId");
                String HKID = rs.getString("hkid");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String gender = rs.getString("gender");
                String DOB = rs.getString("dob");
                Integer clinicId = (Integer) rs.getObject("clinicId");
                String position = rs.getString("position");

                StaffProfileBean bean = new StaffProfileBean(
                        staffId, userId, HKID, firstName, lastName, gender, DOB, clinicId, position);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public StaffProfileBean getStaffById(int staffId) {
        StaffProfileBean bean = null;
        String sql = "SELECT * FROM staff_profile WHERE staffId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String HKID = rs.getString("hkid");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    Integer clinicId = (Integer) rs.getObject("clinicId");
                    String position = rs.getString("position");

                    bean = new StaffProfileBean(
                            staffId, userId, HKID, firstName, lastName, gender, DOB, clinicId, position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public StaffProfileBean getStaffByUserId(int userId) {
        StaffProfileBean bean = null;
        String sql = "SELECT * FROM staff_profile WHERE userId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int staffId = rs.getInt("staffId");
                    String HKID = rs.getString("hkid");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    Integer clinicId = (Integer) rs.getObject("clinicId");
                    String position = rs.getString("position");

                    bean = new StaffProfileBean(
                            staffId, userId, HKID, firstName, lastName, gender, DOB, clinicId, position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public List<StaffProfileBean> getStaffByName(String name) {
        List<StaffProfileBean> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_profile WHERE firstName LIKE ? OR lastName LIKE ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int staffId = rs.getInt("staffId");
                    int userId = rs.getInt("userId");
                    String HKID = rs.getString("hkid");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    Integer clinicId = (Integer) rs.getObject("clinicId");
                    String position = rs.getString("position");

                    StaffProfileBean bean = new StaffProfileBean(
                            staffId, userId, HKID, firstName, lastName, gender, DOB, clinicId, position);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delStaff(int staffId) {
        String sql = "DELETE FROM staff_profile WHERE staffId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean editStaff(StaffProfileBean staff) {
        String sql = "UPDATE staff_profile SET firstName = ?, lastName = ?, gender = ?, dob = ?, clinicId = ? , position = ? WHERE staffId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setString(3, staff.getGender());
            java.sql.Date dob = java.sql.Date.valueOf(staff.getDOB());
            ps.setDate(4, dob);
            if (staff.getClinicId() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, staff.getClinicId());
            }
            ps.setString(6, staff.getPosition());
            ps.setInt(7, staff.getStaffId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
