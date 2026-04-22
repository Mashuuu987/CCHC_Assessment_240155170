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
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author amzte
 */
@WebServlet(name = "IncidentController", urlPatterns = {"/Incident"})
public class IncidentController extends HttpServlet {

    private IncidentLogDB incidentDb;
    private StaffDB staffDb;
    private ServiceDB serviceDb;
    private ClinicDB clinicDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        incidentDb = new IncidentLogDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.isBlank()) {
                return null;
            }
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
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

        Integer staffClinicId = null;
        if (isStaff) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                request.getRequestDispatcher("/staff/incident.jsp").forward(request, response);
                return;
            }
            staffClinicId = staff.getClinicId();
            request.setAttribute("staffClinicId", staffClinicId);
        }

        Integer clinicId = isAdmin ? parseIntOrNull(request.getParameter("clinicId")) : staffClinicId;

        String staffName = isAdmin ? request.getParameter("staffName") : "";
        if (staffName == null) {
            staffName = "";
        }
        staffName = staffName.trim();

        String serviceParam = request.getParameter("serviceId");
        Integer serviceId = null;
        boolean clinicOnly = false;
        if ("CLINIC".equalsIgnoreCase(serviceParam)) {
            clinicOnly = true;
        } else {
            serviceId = parseIntOrNull(serviceParam);
        }

        Integer occurredYear = parseIntOrNull(request.getParameter("occurredYear"));
        Integer occurredMonth = parseIntOrNull(request.getParameter("occurredMonth"));

        Integer createdYear = parseIntOrNull(request.getParameter("createdYear"));
        Integer createdMonth = parseIntOrNull(request.getParameter("createdMonth"));

        String status = request.getParameter("status");
        String severity = request.getParameter("severity");
        String keyword = request.getParameter("keyword");

        if (status == null || status.isBlank()) {
            status = "ALL";
        }
        if (severity == null || severity.isBlank()) {
            severity = "ALL";
        }
        if (keyword == null) {
            keyword = "";
        }

        List<IncidentLogBean> incidents = incidentDb.searchIncidentsfitter(
                clinicId, staffName, serviceId, clinicOnly,
                occurredYear, occurredMonth,
                createdYear, createdMonth,
                status, severity, keyword
        );

        Map<Integer, String> serviceNameMap = new HashMap<>();
        for (ServiceBean s : serviceDb.getAllServices()) {
            serviceNameMap.put(s.getServiceId(), s.getName());
        }

        Map<Integer, String> clinicNameMap = new HashMap<>();
        for (ClinicBean c : clinicDb.getAllClinics()) {
            clinicNameMap.put(c.getClinicId(), c.getName());
        }

        Map<Integer, String> staffNameMap = new HashMap<>();
        try {
            List<StaffProfileBean> allStaff = staffDb.getAllStaff();
            if (allStaff != null) {
                for (StaffProfileBean s : allStaff) {
                    String nm = "";
                    if (s.getFirstName() != null) {
                        nm += s.getFirstName();
                    }
                    if (s.getLastName() != null) {
                        nm += " " + s.getLastName();
                    }
                    nm = nm.trim();
                    if (nm.isBlank()) {
                        nm = "Staff #" + s.getStaffId();
                    }
                    staffNameMap.put(s.getStaffId(), nm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // for jsp use to display
        List<Map<String, Object>> viewRows = new ArrayList<>();

        for (IncidentLogBean i : incidents) {
            Map<String, Object> row = new HashMap<>();

            String clinicName = clinicNameMap.getOrDefault(i.getClinicId(), "Clinic #" + i.getClinicId());
            String staffNm = staffNameMap.getOrDefault(i.getStaffId(), "Staff #" + i.getStaffId());

            String serviceName;
            if (i.getServiceId() == null) {
                serviceName = "Clinic incident";
            } else {
                serviceName = serviceNameMap.getOrDefault(i.getServiceId(), "Service #" + i.getServiceId());
            }

            String sev = (i.getSeverity() == null ? "LOW" : i.getSeverity());
            String st = (i.getStatus() == null ? "OPEN" : i.getStatus());

            row.put("incidentId", i.getIncidentId());
            row.put("clinicName", clinicName);

            row.put("staffId", i.getStaffId());
            row.put("staffName", staffNm);

            row.put("serviceName", serviceName);
            row.put("title", i.getTitle());
            row.put("occurred", i.getOccurred());
            row.put("createdAt", i.getCreatedAt());
            row.put("severity", sev);
            row.put("status", st);

            viewRows.add(row);
        }

        boolean showClinicCol = isAdmin;
        int colCount = showClinicCol ? 11 : 10;

        request.setAttribute("viewRows", viewRows);
        request.setAttribute("showClinicCol", showClinicCol);
        request.setAttribute("colCount", colCount);

        request.setAttribute("filterClinicId", clinicId);
        request.setAttribute("filterStaffName", staffName);
        request.setAttribute("filterServiceId", serviceId);
        request.setAttribute("filterServiceToken", clinicOnly ? "CLINIC" : "");

        request.setAttribute("filterOccurredYear", occurredYear);
        request.setAttribute("filterOccurredMonth", occurredMonth);
        request.setAttribute("filterCreatedYear", createdYear);
        request.setAttribute("filterCreatedMonth", createdMonth);

        request.setAttribute("filterStatus", status);
        request.setAttribute("filterSeverity", severity);
        request.setAttribute("filterKeyword", keyword);

        request.setAttribute("clinics", clinicDb.getAllClinics());
        request.setAttribute("services", serviceDb.getAllServices());

        Object suc = request.getSession().getAttribute("success");
        Object err = request.getSession().getAttribute("error");
        if (suc != null) {
            request.setAttribute("success", suc.toString());
            request.getSession().removeAttribute("success");
        }
        if (err != null) {
            request.setAttribute("error", err.toString());
            request.getSession().removeAttribute("error");
        }

        request.getRequestDispatcher("/staff/incident.jsp").forward(request, response);
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
        boolean isStaff = UserCheckUtil.hasRole(user, "STAFF");

        if (!isAdmin && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        String action = request.getParameter("action");
        if (!"close".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        if (!isAdmin) {
            request.getSession().setAttribute("error", "Only admin can close incidents.");
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        Integer incidentId = parseIntOrNull(request.getParameter("incidentId"));
        if (incidentId == null) {
            request.getSession().setAttribute("error", "Invalid incident ID.");
            response.sendRedirect(request.getContextPath() + "/Incident");
            return;
        }

        boolean ok = incidentDb.closeIncident(incidentId);

        request.getSession().setAttribute(ok ? "success" : "error",
                ok ? "Incident closed." : "Failed to close incident.");

        response.sendRedirect(request.getContextPath() + "/Incident");
    }
}
