/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.staff;

import ict.bean.AppointmentBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.AppointmentNotificationUtil;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = UserCheckUtil.requireRole(request, response, "STAFF");
        if (user == null) return;

        StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
        if (staff == null || staff.getClinicId() == null) {
            response.sendRedirect(request.getContextPath() + "/AppointmentRecordsStaff");
            return;
        }

        String action = request.getParameter("action");
        int appointmentId;
        try {
            appointmentId = Integer.parseInt(request.getParameter("appointmentId"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/AppointmentRecordsStaff");
            return;
        }

        AppointmentBean appt = apptDb.getAppointmentByAppointmentId(appointmentId);
        if (appt == null || appt.getClinicId() != staff.getClinicId()) {
            response.sendRedirect(request.getContextPath() + "/AppointmentRecordsStaff");
            return;
        }

        String newStatus = null;
        String reason = null;

        switch (action) {
            case "confirm": newStatus = "CONFIRMED"; break;
            case "complete": newStatus = "COMPLETED"; break;
            case "noShow": newStatus = "NO_SHOW"; break;
            case "cancelByClinic":
                newStatus = "CANCELLED_BY_CLINIC";
                reason = request.getParameter("reason");
                if (reason == null || reason.isBlank()) {
                    request.getSession().setAttribute("error", "Reason is required to cancel.");
                    response.sendRedirect(request.getContextPath() + "/AppointmentRecordDetails?appointmentId=" + appointmentId);
                    return;
                }
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/AppointmentRecordDetails?appointmentId=" + appointmentId);
                return;
        }

        boolean ok = apptDb.updateAppointmentStatus(appointmentId, newStatus);

        if (ok) {
            Integer patientUserId = patientDb.getUserIdByPatientId(appt.getPatientId());
            if (patientUserId != null) {
                if ("CONFIRMED".equals(newStatus)) {
                    apptNotifUtil.notifyAppointmentConfirmed(patientUserId, appt.getClinicId(), appt.getServiceId(),
                            appt.getAppointmentId(), appt.getAppointmentDate(), appt.getTimeSlot());
                } else if ("CANCELLED_BY_CLINIC".equals(newStatus)) {
                    apptNotifUtil.notifyAppointmentCancelledByStaff(patientUserId, appt.getClinicId(), appt.getServiceId(),
                            appt.getAppointmentId(), appt.getAppointmentDate(), appt.getTimeSlot(), reason);
                } else if ("NO_SHOW".equals(newStatus)) {
                    apptNotifUtil.notifyAppointmentMissed(patientUserId, appt.getClinicId(), appt.getServiceId(),
                            appt.getAppointmentId(), appt.getAppointmentDate(), appt.getTimeSlot());
                } else if ("COMPLETED".equals(newStatus)) {
                    notifDb.createNotification(patientUserId, "NORMAL", "Appointment completed",
                            "Your appointment has been marked as completed.\nAppointment ID: " + appt.getAppointmentId());
                }
            }

            request.getSession().setAttribute("success", "Status updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update status.");
        }

        response.sendRedirect(request.getContextPath() + "/AppointmentRecordDetails?appointmentId=" + appointmentId);
    }
}
