/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.patient;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AppointmentRecordsPatientController", urlPatterns = {"/AppointmentRecordsPatient"})
public class AppointmentRecordsPatientController extends HttpServlet {

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

        PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
        if (patient == null) {
            request.setAttribute("error", "Patient profile not found.");
            request.getRequestDispatcher("/patient/appointmentRecords.jsp").forward(request, response);
            return;
        }

        List<AppointmentBean> list = apptDb.getAppointmentsByPatientId(patient.getPatientId());
        Map<Integer, String> clinicNameMap = new LinkedHashMap<>();
        Map<Integer, String> serviceNameMap = new LinkedHashMap<>();

        if (list != null) {
            for (AppointmentBean a : list) {
                int clinicId = a.getClinicId();
                int serviceId = a.getServiceId();

                if (!clinicNameMap.containsKey(clinicId)) {
                    ClinicBean c = clinicDb.getClinicByID(clinicId);
                    clinicNameMap.put(clinicId, c != null ? c.getName() : ("Clinic #" + clinicId));
                }

                if (!serviceNameMap.containsKey(serviceId)) {
                    ServiceBean s = serviceDb.getServiceById(serviceId);
                    serviceNameMap.put(serviceId, s != null ? s.getName() : ("Service #" + serviceId));
                }
            }
        }

        request.setAttribute("appointments", list);
        request.setAttribute("clinicNameMap", clinicNameMap);
        request.setAttribute("serviceNameMap", serviceNameMap);

        request.getRequestDispatcher("/patient/appointmentRecords.jsp").forward(request, response);
    }
}
