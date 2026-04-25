package ict.db;

import ict.bean.ClinicBean;
import ict.bean.QueueSettingBean;
import ict.bean.ServiceBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QueueSettingDB {

    private String url;
    private String username;
    private String password;

    public QueueSettingDB(String url, String username, String password) {
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

    public void createQueueSettingTable() {
        String sql = "CREATE TABLE IF NOT EXISTS queue_setting ("
                + "settingId INT AUTO_INCREMENT, "
                + "clinicId INT NOT NULL, "
                + "serviceId INT NOT NULL, "
                + "allowIssueTicket BOOLEAN NOT NULL DEFAULT TRUE, "
                + "enabled BOOLEAN NOT NULL DEFAULT TRUE, "
                + "maxTicketsPerDay INT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY (settingId), "
                + "UNIQUE KEY uq_queue_setting (clinicId, serviceId), "
                + "CONSTRAINT fk_queue_setting_clinic FOREIGN KEY (clinicId) REFERENCES clinic(clinicId), "
                + "CONSTRAINT fk_queue_setting_service FOREIGN KEY (serviceId) REFERENCES service(serviceId)"
                + ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultSettingsIfEmpty(ClinicDB clinicDb, ServiceDB serviceDb) {
        String countSql = "SELECT COUNT(*) FROM queue_setting";
        try (Connection c = getConnection(); PreparedStatement psCount = c.prepareStatement(countSql); ResultSet rs = psCount.executeQuery()) {

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            if (count == 0) {
                List<ClinicBean> clinics = clinicDb.getAllClinics();
                List<ServiceBean> services = serviceDb.getAllServices();
                String insertSql = "INSERT INTO queue_setting (clinicId, serviceId, allowIssueTicket, enabled, maxTicketsPerDay) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                    for (ClinicBean clinic : clinics) {
                        for (ServiceBean service : services) {
                            ps.setInt(1, clinic.getClinicId());
                            ps.setInt(2, service.getServiceId());
                            ps.setBoolean(3, true);
                            ps.setBoolean(4, true);
                            ps.setInt(5, 0);
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QueueSettingBean mapRow(ResultSet rs) throws SQLException {
        QueueSettingBean bean = new QueueSettingBean();
        bean.setSettingId(rs.getInt("settingId"));
        bean.setClinicId(rs.getInt("clinicId"));
        bean.setClinicName(rs.getString("clinicName"));
        bean.setClinicOpenTime(rs.getString("openTime"));
        bean.setClinicCloseTime(rs.getString("closeTime"));
        bean.setClinicCloseDay(rs.getString("closeDay"));
        bean.setServiceId(rs.getInt("serviceId"));
        bean.setServiceName(rs.getString("serviceName"));
        bean.setServiceType(rs.getString("serviceType"));
        bean.setDurationMins(rs.getInt("durationMins"));
        bean.setAllowIssueTicket(rs.getBoolean("allowIssueTicket"));
        bean.setEnabled(rs.getBoolean("enabled"));
        bean.setMaxTicketsPerDay(rs.getInt("maxTicketsPerDay"));
        return bean;
    }

    public List<QueueSettingBean> getAllQueueSettings() {
        List<QueueSettingBean> list = new ArrayList<>();
        String sql = "SELECT qs.settingId, qs.clinicId, qs.serviceId, qs.allowIssueTicket, qs.enabled, qs.maxTicketsPerDay, "
                + "c.name AS clinicName, c.openTime, c.closeTime, c.closeDay, "
                + "s.name AS serviceName, s.serviceType, s.durationMins "
                + "FROM queue_setting qs "
                + "JOIN clinic c ON qs.clinicId = c.clinicId "
                + "JOIN service s ON qs.serviceId = s.serviceId "
                + "ORDER BY c.name, s.name";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<QueueSettingBean> getQueueSettingsByClinicId(int clinicId) {
        List<QueueSettingBean> list = new ArrayList<>();
        String sql = "SELECT qs.settingId, qs.clinicId, qs.serviceId, qs.allowIssueTicket, qs.enabled, qs.maxTicketsPerDay, "
                + "c.name AS clinicName, c.openTime, c.closeTime, c.closeDay, "
                + "s.name AS serviceName, s.serviceType, s.durationMins "
                + "FROM queue_setting qs "
                + "JOIN clinic c ON qs.clinicId = c.clinicId "
                + "JOIN service s ON qs.serviceId = s.serviceId "
                + "WHERE qs.clinicId = ? "
                + "ORDER BY s.name";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public QueueSettingBean getQueueSetting(int clinicId, int serviceId) {
        QueueSettingBean bean = null;
        String sql = "SELECT qs.settingId, qs.clinicId, qs.serviceId, qs.allowIssueTicket, qs.enabled, qs.maxTicketsPerDay, "
                + "c.name AS clinicName, c.openTime, c.closeTime, c.closeDay, "
                + "s.name AS serviceName, s.serviceType, s.durationMins "
                + "FROM queue_setting qs "
                + "JOIN clinic c ON qs.clinicId = c.clinicId "
                + "JOIN service s ON qs.serviceId = s.serviceId "
                + "WHERE qs.clinicId = ? AND qs.serviceId = ?";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bean = mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    public boolean updateAllowIssueTicket(int clinicId, int serviceId, boolean allowIssueTicket) {
        String sql = "UPDATE queue_setting SET allowIssueTicket = ? WHERE clinicId = ? AND serviceId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, allowIssueTicket);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEnabled(int clinicId, int serviceId, boolean enabled) {
        String sql = "UPDATE queue_setting SET enabled = ? WHERE clinicId = ? AND serviceId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, enabled);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMaxTicketsPerDay(int clinicId, int serviceId, int maxTicketsPerDay) {
        String sql = "UPDATE queue_setting SET maxTicketsPerDay = ? WHERE clinicId = ? AND serviceId = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maxTicketsPerDay);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void ensureSettingsForNewClinic(int clinicId) {
        String sql
                = "INSERT INTO queue_setting (clinicId, serviceId, allowIssueTicket, enabled, maxTicketsPerDay) "
                + "SELECT ?, s.serviceId, TRUE, TRUE, 0 FROM service s "
                + "ON DUPLICATE KEY UPDATE settingId = settingId";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ensureSettingsForNewService(int serviceId) {
        String sql
                = "INSERT INTO queue_setting (clinicId, serviceId, allowIssueTicket, enabled, maxTicketsPerDay) "
                + "SELECT c.clinicId, ?, TRUE, TRUE, 0 FROM clinic c "
                + "ON DUPLICATE KEY UPDATE settingId = settingId";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
