/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.staff;

/**
 *
 * @author amzte
 */
import ict.bean.ServiceBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.IncidentLogDB;
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

@WebServlet(name = "CreateIncidentController", urlPatterns = {"/CreateIncident"})
public class CreateIncidentController extends HttpServlet {

    private IncidentLogDB incidentDb;
    private StaffDB staffDb;
    private ServiceDB serviceDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        incidentDb = new IncidentLogDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
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

        if (!UserCheckUtil.hasRole(user, "STAFF")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
        if (staff == null || staff.getClinicId() == null) {
            request.setAttribute("error", "No clinic is linked to this staff account.");
            request.getRequestDispatcher("/staff/createIncident.jsp").forward(request, response);
            return;
        }

        List<ServiceBean> services = serviceDb.getAllServices();

        request.setAttribute("isStaff", true);
        request.setAttribute("staff", staff);
        request.setAttribute("services", services);

        request.getRequestDispatcher("/staff/createIncident.jsp").forward(request, response);
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

        if (!UserCheckUtil.hasRole(user, "STAFF")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
        if (staff == null || staff.getClinicId() == null) {
            request.getSession().setAttribute("error", "No clinic is linked to this staff account.");
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        String serviceParam = request.getParameter("serviceId");
        Integer serviceId = null;

        if (serviceParam != null && !serviceParam.isBlank() && !"CLINIC".equalsIgnoreCase(serviceParam)) {
            serviceId = parseIntOrNull(serviceParam);
            if (serviceId != null && serviceId <= 0) {
                serviceId = null;
            }
        }

        String severity = request.getParameter("severity");
        String title = request.getParameter("title");
        String occurred = request.getParameter("occurred");
        String description = request.getParameter("description");

        if (severity == null || severity.isBlank()) {
            severity = "LOW";
        }

        if (title == null || title.isBlank()) {
            request.getSession().setAttribute("error", "Title is required.");
            response.sendRedirect(request.getContextPath() + "/CreateIncident");
            return;
        }
        if (title.length() > 50) {
            request.getSession().setAttribute("error", "Title must be <= 50 characters.");
            response.sendRedirect(request.getContextPath() + "/CreateIncident");
            return;
        }

        if (occurred == null || occurred.isBlank()) {
            request.getSession().setAttribute("error", "Occurred time is required.");
            response.sendRedirect(request.getContextPath() + "/CreateIncident");
            return;
        }

        if (description == null || description.isBlank()) {
            request.getSession().setAttribute("error", "Description is required.");
            response.sendRedirect(request.getContextPath() + "/CreateIncident");
            return;
        }

        int newId = incidentDb.createIncident(
                staff.getStaffId(),
                staff.getClinicId(),
                serviceId,
                title.trim(),
                description.trim(),
                severity,
                occurred
        );

        request.getSession().setAttribute(newId > 0 ? "success" : "error",
                newId > 0 ? "Incident created successfully." : "Failed to create incident.");

        response.sendRedirect(request.getContextPath() + "/Incident");
    }
}
