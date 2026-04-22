/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;

import ict.bean.AppointmentBean;
import ict.bean.ClinicBean;
import ict.bean.PatientProfileBean;
import ict.bean.ServiceBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.PatientDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AppointmentRecordDetailsController", urlPatterns = {"/AppointmentRecordDetails"})
public class AppointmentRecordDetailsController extends HttpServlet {

    private AppointmentDB apptDb;
    private PatientDB patientDb;
    private StaffDB staffDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        boolean isPatient = UserCheckUtil.hasRole(user, "PATIENT");
        boolean isStaff = UserCheckUtil.hasRole(user, "STAFF");
        PatientProfileBean patient = null;

        if (!isPatient && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        request.setAttribute("isPatient", isPatient);
        request.setAttribute("isStaff", isStaff);

        String idStr = request.getParameter("appointmentId");
        if (idStr == null || idStr.isEmpty()) {
            request.setAttribute("error", "Missing appointment ID.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        AppointmentBean appt = apptDb.getAppointmentByAppointmentId(appointmentId);
        if (appt == null) {
            request.setAttribute("error", "Appointment not found.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        if (isPatient) {
            patient = patientDb.getPatientByUserId(user.getUserId());
            if (isPatient && (patient == null || appt.getPatientId() != patient.getPatientId())) {
                request.setAttribute("error", "You are not allowed to view this appointment.");
                request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
                return;
            }
        }
        request.setAttribute("patient", patient);

        if (isStaff) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null || appt.getClinicId() != staff.getClinicId()) {
                request.setAttribute("error", "You can only view appointments for your clinic.");
                request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
                return;
            }
            patient = patientDb.getPatientById(appt.getPatientId());
        }

        ClinicBean clinic = clinicDb.getClinicByID(appt.getClinicId());
        ServiceBean service = serviceDb.getServiceById(appt.getServiceId());

        request.setAttribute("appointment", appt);
        request.setAttribute("clinic", clinic);
        request.setAttribute("service", service);
        request.setAttribute("patient", patient);

        request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
    }
}
