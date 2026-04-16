/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import ict.bean.AppointmentBean;
import ict.bean.ClinicBean;
import ict.bean.PatientProfileBean;
import ict.bean.ServiceBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.PatientDB;
import ict.db.ServiceDB;
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
@WebServlet(name = "AppointmentRecordDetailsController", urlPatterns = {"/AppointmentRecordDetails"})
public class AppointmentRecordDetailsController extends HttpServlet {

    private AppointmentDB apptDb;
    private PatientDB patientDb;
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
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserInfoBean user = UserCheckUtil.requireRole(request, response, "PATIENT");
        if (user == null) {
            return;
        }
        
        String idStr = request.getParameter("appointmentId");
        if(idStr == null || idStr.isEmpty()){
            request.setAttribute("error", "Missing appointment ID.");
            request.getRequestDispatcher("/patient/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }
        
        int appointmentId;
        try{
            appointmentId = Integer.parseInt(idStr);
        }catch(NumberFormatException e){
            request.setAttribute("error", "Invalid appointment ID.");
            request.getRequestDispatcher("/patient/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }
        
        AppointmentBean appt = apptDb.getAppointmentByAppointmentId(appointmentId);
        if (appt == null) {
            request.setAttribute("error", "Appointment not found.");
            request.getRequestDispatcher("/patient/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }
        
        PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
        if (patient == null || appt.getPatientId() != patient.getPatientId()) {
            request.setAttribute("error", "You are not allowed to view this appointment.");
            request.getRequestDispatcher("/patient/appointmentRecordDetails.jsp").forward(request, response);
            return;
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
