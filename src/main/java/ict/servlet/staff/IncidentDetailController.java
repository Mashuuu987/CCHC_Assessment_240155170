/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.staff;

import ict.bean.ClinicBean;
import ict.bean.IncidentLogBean;
import ict.bean.ServiceBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.IncidentLogDB;
import ict.db.NotificationDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.IncidentNotificationUtil;
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
@WebServlet(name = "IncidentDetailController", urlPatterns = {"/IncidentDetail"})
public class IncidentDetailController extends HttpServlet {

    private IncidentLogDB incidentDb;
    private StaffDB staffDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private NotificationDB notifDb;
    private IncidentNotificationUtil incidentNotificationUtil;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        incidentDb = new IncidentLogDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        incidentNotificationUtil = new IncidentNotificationUtil(notifDb);
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.isBlank()) {
                return null;
            }
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
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

        request.setAttribute("isStaff", isStaff);
        request.setAttribute("isAdmin", isAdmin);

        Integer incidentId = parseIntOrNull(request.getParameter("incidentId"));
        if (incidentId == null) {
            request.setAttribute("error", "Invalid incident ID.");
            request.getRequestDispatcher("/staff/incidentDetail.jsp").forward(request, response);
            return;
        }

        IncidentLogBean incident = incidentDb.getIncidentById(incidentId);
        if (incident == null) {
            request.setAttribute("error", "Incident not found.");
            request.getRequestDispatcher("/staff/incidentDetail.jsp").forward(request, response);
            return;
        }

        if (isStaff) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null || incident.getClinicId() != staff.getClinicId()) {
                request.setAttribute("error", "You are not allowed to view this incident.");
                request.getRequestDispatcher("/staff/incidentDetail.jsp").forward(request, response);
                return;
            }
        }

        ClinicBean clinic = clinicDb.getClinicByID(incident.getClinicId());
        String clinicName = clinic != null ? clinic.getName() : ("Clinic #" + incident.getClinicId());

        StaffProfileBean staff = staffDb.getStaffById(incident.getStaffId());
        String staffName = (staff != null) ? (staff.getFirstName() + " " + staff.getLastName()).trim() : ("Staff #" + incident.getStaffId());

        String serviceName;
        if (incident.getServiceId() == null) {
            serviceName = "Clinic incident";
        } else {
            ServiceBean s = serviceDb.getServiceById(incident.getServiceId());
            serviceName = (s != null) ? s.getName() : ("Service #" + incident.getServiceId());
        }

        boolean canClose = isAdmin && "OPEN".equalsIgnoreCase(incident.getStatus());

        request.setAttribute("incident", incident);
        request.setAttribute("clinicName", clinicName);
        request.setAttribute("staffName", staffName);
        request.setAttribute("serviceName", serviceName);
        request.setAttribute("canClose", canClose);

        request.getRequestDispatcher("/staff/incidentDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        boolean isAdmin = UserCheckUtil.hasRole(user, "ADMIN");
        if (!isAdmin) {
            request.getSession().setAttribute("error", "Only admin can close incidents.");
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        String action = request.getParameter("action");
        if (!"close".equalsIgnoreCase(action)) {
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        Integer incidentId = parseIntOrNull(request.getParameter("incidentId"));
        if (incidentId == null) {
            request.getSession().setAttribute("error", "Invalid incident ID.");
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }
        IncidentLogBean incident = incidentDb.getIncidentById(incidentId);

        if (incident == null) {
            request.getSession().setAttribute("error", "Incident not found.");
            response.sendRedirect(request.getContextPath() + "/IncidentList");
            return;
        }
        int staffId = incident.getStaffId();
        boolean ok = incidentDb.closeIncident(incidentId);

        request.getSession().setAttribute(ok ? "success" : "error", ok ? "Incident closed." : "Failed to close incident.");
        if (ok) {
            StaffProfileBean staff = staffDb.getStaffById(staffId);
            if (staff != null) {
                int staffUserId = staff.getUserId();
                String remark = request.getParameter("remark");
                incidentNotificationUtil.notifyIncidentClosedByAdmin(staffUserId, incidentId, remark);
            }
        }
        response.sendRedirect(request.getContextPath() + "/IncidentDetail?incidentId=" + incidentId);
    }
}
