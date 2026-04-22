/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet;

import java.io.IOException;

import ict.bean.UserInfoBean;
import ict.db.AnnouncementsDB;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.IncidentLogDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.QueueTicketDB;
import ict.db.QueueSettingDB;
import ict.db.ServiceCapacityDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.db.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author amzte
 */
@WebServlet(name = "PublicHomeController", urlPatterns = {"/PublicHome"})
public class PublicHomeController extends HttpServlet {

    private UserDB db;
    private PatientDB patientDb;
    private StaffDB staffDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private AppointmentDB apptDb;
    private QueueTicketDB queueDb;
    private QueueSettingDB queueSettingDb;
    private NotificationDB notifDb;
    private ServiceCapacityDB capDb;
    private AnnouncementsDB annDb;
    private IncidentLogDB incDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        db = new UserDB(dbUrl, dbUser, dbPassword);
        db.createUserInfoTable();
        db.insertDefaultUserIfEmpty();

        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        patientDb.createPatientProfileTable();
        patientDb.insertDefaultPatientIfEmpty();

        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        staffDb.createStaffProfileTable();
        staffDb.insertDefaultStaffIfEmpty();

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        clinicDb.createClinicTable();
        clinicDb.insertDefaultClinicsIfEmpty();

        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        serviceDb.createServiceTable();
        serviceDb.insertDefaultServicesIfEmpty();

        annDb = new AnnouncementsDB(dbUrl, dbUser, dbPassword);
        annDb.createAnnouncementTable();
        annDb.insertDefaultAnnouncementsIfEmpty();

        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        notifDb.createNotificationTable();
        notifDb.insertDefaultNotificationIfEmpty();

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        apptDb.createAppointmentTable();

        queueDb = new QueueTicketDB(dbUrl, dbUser, dbPassword);
        queueDb.createQueueTicketTable();

        queueSettingDb = new QueueSettingDB(dbUrl, dbUser, dbPassword);
        queueSettingDb.createQueueSettingTable();
        queueSettingDb.insertDefaultSettingsIfEmpty(clinicDb, serviceDb);

        capDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
        capDb.createServiceCapacityTable();
        capDb.insertDefaultCapacitiesIfEmpty();
        
        incDb = new IncidentLogDB(dbUrl, dbUser, dbPassword);
        incDb.createIncidentLogTable();
        incDb.insertDefaultIncidentIfEmpty();
    }

    @Override
    protected void doGet(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        String ctx = request.getContextPath();

        if (user == null || user.getRole() == null) {
            request.setAttribute("announcements", annDb.getLatestVisibleAnnouncements(3));
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        String role = user.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/AdminHome");
        } else if ("STAFF".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/StaffHome");
        } else if ("PATIENT".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/PatientHome");
        } else {
            request.setAttribute("announcements", annDb.getLatestVisibleAnnouncements(3));
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
