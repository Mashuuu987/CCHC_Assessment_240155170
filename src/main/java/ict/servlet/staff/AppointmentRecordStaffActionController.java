/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.staff;

import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.AppointmentNotificationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AppointmentRecordStaffActionController", urlPatterns = {"/AppointmentRecordStaffAction"})
public class AppointmentRecordStaffActionController extends HttpServlet {

    private AppointmentDB apptDb;
    private StaffDB staffDb;
    private PatientDB patientDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private NotificationDB notifDb;
    private AppointmentNotificationUtil apptNotifUtil;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        apptNotifUtil = new AppointmentNotificationUtil(notifDb, clinicDb, serviceDb);
    }

}
