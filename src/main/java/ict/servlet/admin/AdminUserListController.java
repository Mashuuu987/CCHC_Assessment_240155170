/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.ClinicBean;
import ict.bean.PatientProfileBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.PatientDB;
import ict.db.StaffDB;
import ict.db.UserDB;
import ict.util.UserCheckUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "AdminUserListController", urlPatterns = {"/AdminUserList"})
public class AdminUserListController extends HttpServlet {

    private UserDB userDb;
    private StaffDB staffDb;
    private PatientDB patientDb;
    private ClinicDB clinicDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        userDb = new UserDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
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

    private boolean containsIgnoreCase(String text, String kw) {
        if (kw == null || kw.isBlank()) {
            return true;
        }
        if (text == null) {
            return false;
        }
        return text.toLowerCase().contains(kw.trim().toLowerCase());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean login = UserCheckUtil.getLoginUser(request);
        if (login == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        boolean isAdmin = UserCheckUtil.hasRole(login, "ADMIN");
        if (!isAdmin) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }
        request.setAttribute("isAdmin", true);

        Integer fUserId = parseIntOrNull(request.getParameter("userId"));
        String fUsername = request.getParameter("username");
        String fRole = request.getParameter("role"); // ALL / PATIENT / STAFF / ADMIN
        Integer fStaffId = parseIntOrNull(request.getParameter("staffId"));
        Integer fPatientId = parseIntOrNull(request.getParameter("patientId"));
        Integer fClinicId = parseIntOrNull(request.getParameter("clinicId"));
        String fName = request.getParameter("name");

        if (fRole == null || fRole.isBlank()) {
            fRole = "ALL";
        }
        if (fUsername == null) {
            fUsername = "";
        }
        if (fName == null) {
            fName = "";
        }

        request.setAttribute("filterUserId", fUserId);
        request.setAttribute("filterUsername", fUsername);
        request.setAttribute("filterRole", fRole);
        request.setAttribute("filterStaffId", fStaffId);
        request.setAttribute("filterPatientId", fPatientId);
        request.setAttribute("filterClinicId", fClinicId);
        request.setAttribute("filterName", fName);

        List<UserInfoBean> users = userDb.getAllUsers();
        List<StaffProfileBean> staffList = staffDb.getAllStaff();
        List<PatientProfileBean> patientList = patientDb.getAllPatients();
        List<ClinicBean> clinics = clinicDb.getAllClinics();

        Map<Integer, String> clinicNameMap = new HashMap<>();
        for (ClinicBean c : clinics) {
            clinicNameMap.put(c.getClinicId(), c.getName());
        }

        Map<Integer, StaffProfileBean> staffByUserId = new HashMap<>();
        for (StaffProfileBean s : staffList) {
            staffByUserId.put(s.getUserId(), s);
        }

        Map<Integer, PatientProfileBean> patientByUserId = new HashMap<>();
        for (PatientProfileBean p : patientList) {
            patientByUserId.put(p.getUserId(), p);
        }

        List<Map<String, Object>> viewRows = new ArrayList<>();

        for (UserInfoBean u : users) {

            String role = u.getRole();
            StaffProfileBean sp = staffByUserId.get(u.getUserId());
            PatientProfileBean pp = patientByUserId.get(u.getUserId());

            Integer staffId = (sp != null) ? sp.getStaffId() : null;
            Integer patientId = (pp != null) ? pp.getPatientId() : null;

            String fullName = " - ";
            if (sp != null) {
                fullName = (sp.getFirstName() + " " + sp.getLastName()).trim();
            } else if (pp != null) {
                fullName = (pp.getFirstName() + " " + pp.getLastName()).trim();
            }

            String clinicName = " - ";
            Integer clinicId = null;
            if (sp != null && sp.getClinicId() != null) {
                clinicId = sp.getClinicId();
                clinicName = clinicNameMap.getOrDefault(clinicId, "Clinic #" + clinicId);
            }

            if (fUserId != null && u.getUserId() != fUserId) {
                continue;
            }
            if (!containsIgnoreCase(u.getUsername(), fUsername)) {
                continue;
            }
            if (!"ALL".equalsIgnoreCase(fRole) && (role == null || !role.equalsIgnoreCase(fRole))) {
                continue;
            }

            if (fStaffId != null) {
                if (staffId == null || !staffId.equals(fStaffId)) {
                    continue;
                }
            }
            if (fPatientId != null) {
                if (patientId == null || !patientId.equals(fPatientId)) {
                    continue;
                }
            }
            if (fClinicId != null) {
                if (clinicId == null || !clinicId.equals(fClinicId)) {
                    continue;
                }
            }
            if (!containsIgnoreCase(fullName, fName)) {
                continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("userId", u.getUserId());
            row.put("username", u.getUsername());
            row.put("role", role);

            row.put("staffId", staffId == null ? " - " : String.valueOf(staffId));
            row.put("patientId", patientId == null ? " - " : String.valueOf(patientId));
            row.put("fullName", (fullName == null || fullName.isBlank()) ? " - " : fullName);

            row.put("clinicName", clinicName);
            row.put("clinicId", clinicId == null ? "" : String.valueOf(clinicId));

            viewRows.add(row);
        }

        request.setAttribute("clinics", clinics);
        request.setAttribute("viewRows", viewRows);
        request.setAttribute("colCount", 8);

        request.getRequestDispatcher("/admin/userList.jsp").forward(request, response);
    }
}
