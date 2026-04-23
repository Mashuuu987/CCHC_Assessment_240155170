/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.staff;

import ict.bean.AppointmentBean;
import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AppointmentRecordsStaffAdminController", urlPatterns = {"/AppointmentRecordsStaffAdmin"})
public class AppointmentRecordsStaffAdminController extends HttpServlet {

    private AppointmentDB apptDb;
    private StaffDB staffDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        boolean isStaff = UserCheckUtil.hasRole(user, "STAFF");
        boolean isAdmin = UserCheckUtil.hasRole(user, "ADMIN");

        if (!isStaff && !isAdmin) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        List<AppointmentBean> appointments;
        Map<Integer, String> clinicNameMap = new java.util.HashMap<>();

        if (isStaff) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                request.getRequestDispatcher("/staff/appointmentAllRecords.jsp").forward(request, response);
                return;
            }

            int clinicId = staff.getClinicId();
            appointments = apptDb.getAppointmentsByClinicId(clinicId);

            ClinicBean clinic = clinicDb.getClinicByID(clinicId);
            clinicNameMap.put(clinicId, clinic != null ? clinic.getName() : ("Clinic #" + clinicId));

            request.setAttribute("isStaff", true);
            request.setAttribute("staff", staff);
            request.setAttribute("staffClinicId", clinicId);
        } else {
            appointments = apptDb.getAllAppointments();

            for (ClinicBean c : clinicDb.getAllClinics()) {
                clinicNameMap.put(c.getClinicId(), c.getName());
            }

            request.setAttribute("isAdmin", true);
        }

        Map<Integer, String> serviceNameMap = new java.util.HashMap<>();
        for (ServiceBean s : serviceDb.getAllServices()) {
            serviceNameMap.put(s.getServiceId(), s.getName());
        }

        request.setAttribute("appointments", appointments);
        request.setAttribute("clinicNameMap", clinicNameMap);
        request.setAttribute("serviceNameMap", serviceNameMap);

        request.getRequestDispatcher("/staff/appointmentAllRecords.jsp").forward(request, response);

    }
}
