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

import ict.bean.PatientProfileBean;

/**
 *
 * @author amzte
 */
public class PatientDB {

    private String url;
    private String username;
    private String password;

    public PatientDB(String url, String username, String password) {
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

    public void createPatientProfileTable() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS patient_profile ("
                    + "patientId INT AUTO_INCREMENT, "
                    + "userId INT NOT NULL, "
                    + "hkid_id VARCHAR(15) NOT NULL UNIQUE, "
                    + "firstName VARCHAR(50) NOT NULL, "
                    + "lastName VARCHAR(50) NOT NULL, "
                    + "gender ENUM('M','F') NOT NULL, "
                    + "dob DATE NOT NULL, "
                    + "phone VARCHAR(20) NOT NULL, "
                    + "email VARCHAR(80), "
                    + "address VARCHAR(100), "
                    + "emergencyContactFullName VARCHAR(100), "
                    + "emergencyContact VARCHAR(20), "
                    + "PRIMARY KEY (patientId), "
                    + "UNIQUE (userId), "
                    + "CONSTRAINT fk_patient_user "
                    + "FOREIGN KEY (userId) REFERENCES userInfo(userId)"
                    + ")";
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int createPatientProfile(int userId,
            String HKIDorID,
            String firstName,
            String lastName,
            String gender,
            String DOB,
            String phone,
            String email,
            String address,
            String emergencyContactFullName,
            String emergencyContact) {
        int patientId = -1;
        String sql = "INSERT INTO patient_profile "
            + "(userId, hkid_id, firstName, lastName, gender, dob, phone, email, address, "
                + " emergencyContactFullName, emergencyContact) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, HKIDorID);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setString(5, gender);
            java.sql.Date dob = java.sql.Date.valueOf(DOB);
            ps.setDate(6, dob);
            ps.setString(7, phone);
            ps.setString(8, email);
            ps.setString(9, address);
            ps.setString(10, emergencyContactFullName);
            ps.setString(11, emergencyContact);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        patientId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patientId;
    }
    
       public void insertDefaultPatientIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM patient_profile";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                createPatientProfile(1, "B1234567", "Banana", "Skin", "M", "2000-12-25", "12345678", "Banana@banana.com", "Banana house", "NULL", "NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PatientProfileBean> getAllPatients() {
        List<PatientProfileBean> list = new ArrayList<>();
        String sql = "SELECT * FROM patient_profile ORDER BY patientId";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int patientId = rs.getInt("patientId");
                int userId = rs.getInt("userId");
                String HKIDorID = rs.getString("hkid_id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String gender = rs.getString("gender");
                String DOB = rs.getString("dob");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String ecName = rs.getString("emergencyContactFullName");
                String ecPhone = rs.getString("emergencyContact");

                PatientProfileBean bean = new PatientProfileBean(
                        patientId, userId, HKIDorID, firstName, lastName, gender, DOB,
                        phone, email, address, ecName, ecPhone);
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public PatientProfileBean getPatientById(int patientId) {
        PatientProfileBean bean = null;
        String sql = "SELECT * FROM patient_profile WHERE patientId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String HKIDorID = rs.getString("hkid_id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String ecName = rs.getString("emergencyContactFullName");
                    String ecPhone = rs.getString("emergencyContact");

                    bean = new PatientProfileBean(
                            patientId, userId, HKIDorID, firstName, lastName, gender, DOB,
                            phone, email, address, ecName, ecPhone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public PatientProfileBean getPatientByUserId(int userId) {
        PatientProfileBean bean = null;
        String sql = "SELECT * FROM patient_profile WHERE userId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int patientId = rs.getInt("patientId");
                    String HKIDorID = rs.getString("hkid_id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String ecName = rs.getString("emergencyContactFullName");
                    String ecPhone = rs.getString("emergencyContact");

                    bean = new PatientProfileBean(
                            patientId, userId, HKIDorID, firstName, lastName, gender, DOB,
                            phone, email, address, ecName, ecPhone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public List<PatientProfileBean> getPatientsByName(String name) {
        List<PatientProfileBean> list = new ArrayList<>();
        String sql = "SELECT * FROM patient_profile WHERE firstName LIKE ? OR lastName LIKE ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int patientId = rs.getInt("patientId");
                    int userId = rs.getInt("userId");
                    String HKIDorID = rs.getString("hkid_id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");;
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String ecName = rs.getString("emergencyContactFullName");
                    String ecPhone = rs.getString("emergencyContact");

                    PatientProfileBean bean = new PatientProfileBean(
                            patientId, userId, HKIDorID, firstName, lastName, gender, DOB,
                            phone, email, address, ecName, ecPhone);
                    list.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
       public PatientProfileBean getPatientByHKIdOrID(String HKIDorID) {
        PatientProfileBean bean = null;
        String sql = "SELECT * FROM patient_profile WHERE hkid_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, HKIDorID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int patientId = rs.getInt("patientId");
                    int userId = rs.getInt("userId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");;
                    String gender = rs.getString("gender");
                    String DOB = rs.getString("dob");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String ecName = rs.getString("emergencyContactFullName");
                    String ecPhone = rs.getString("emergencyContact");

                    bean = new PatientProfileBean(
                            patientId, userId, HKIDorID, firstName, lastName, gender, DOB,
                            phone, email, address, ecName, ecPhone);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean delPatient(int patientId) {
        String sql = "DELETE FROM patient_profile WHERE patientId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean editPatient(PatientProfileBean patient) {
        String sql = "UPDATE patient_profile SET firstName = ?, lastName = ?, gender = ?, dob = ? , phone = ? , email = ? , address = ?, "
                + "emergencyContactFullName = ? ,emergencyContact = ? WHERE patientId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            ps.setString(3, patient.getGender());
            java.sql.Date dob = java.sql.Date.valueOf(patient.getDOB());
            ps.setDate(4, dob);
            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getEmail());
            ps.setString(7, patient.getAddress());
            ps.setString(8, patient.getEmergencyContactFullName());
            ps.setString(9, patient.getEmergencyContact());
            ps.setInt(10, patient.getPatientId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
