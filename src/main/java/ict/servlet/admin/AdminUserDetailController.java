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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminUserDetailController", urlPatterns = {"/AdminUserDetail"})
public class AdminUserDetailController extends HttpServlet {

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

        Integer userId = parseIntOrNull(request.getParameter("userId"));
        if (userId == null) {
            request.setAttribute("error", "Invalid userId.");
            request.getRequestDispatcher("/admin/adminUserDetail.jsp").forward(request, response);
            return;
        }

        UserInfoBean user = userDb.getUserById(userId);
        if (user == null) {
            request.setAttribute("error", "User not found.");
            request.getRequestDispatcher("/admin/adminUserDetail.jsp").forward(request, response);
            return;
        }

        StaffProfileBean staff = staffDb.getStaffByUserId(userId);
        PatientProfileBean patient = patientDb.getPatientByUserId(userId);

        String clinicName = " - ";
        if (staff != null && staff.getClinicId() != null) {
            ClinicBean c = clinicDb.getClinicByID(staff.getClinicId());
            clinicName = (c != null) ? c.getName() : ("Clinic #" + staff.getClinicId());
        }

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        List<java.util.Map<String, Object>> staffClinicOptions = new java.util.ArrayList<>();
        Integer selectedClinicId = (staff != null) ? staff.getClinicId() : null;

        if (clinics != null) {
            for (ClinicBean c : clinics) {
                Map<String, Object> opt = new HashMap<>();
                opt.put("value", String.valueOf(c.getClinicId()));
                opt.put("label", c.getClinicId() + " - " + c.getName());
                opt.put("selected", selectedClinicId != null && selectedClinicId == c.getClinicId());
                staffClinicOptions.add(opt);
            }
        }

        request.setAttribute("staffClinicOptions", staffClinicOptions);
        request.setAttribute("user", user);
        request.setAttribute("staff", staff);
        request.setAttribute("patient", patient);
        request.setAttribute("clinicName", clinicName);

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object suc = session.getAttribute("success");
            Object err = session.getAttribute("error");
            if (suc != null) {
                request.setAttribute("success", suc.toString());
                session.removeAttribute("success");
            }
            if (err != null) {
                request.setAttribute("error", err.toString());
                session.removeAttribute("error");
            }
        }

        request.getRequestDispatcher("/admin/adminUserDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

        String action = request.getParameter("action");
        Integer userId = parseIntOrNull(request.getParameter("userId"));
        if (userId == null) {
            request.getSession().setAttribute("error", "Invalid userId.");
            response.sendRedirect(request.getContextPath() + "/AdminUserList");
            return;
        }

        if ("update".equalsIgnoreCase(action)) {
            UserInfoBean existing = userDb.getUserById(userId);
            if (existing == null) {
                request.getSession().setAttribute("error", "User not found.");
                response.sendRedirect(request.getContextPath() + "/AdminUserList");
                return;
            }

            String newUsername = request.getParameter("username");

            if (newUsername == null || newUsername.isBlank()) {
                request.getSession().setAttribute("error", "Username is required.");
                response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
                return;
            }

            boolean okUsername = true;
            if (!newUsername.equals(existing.getUsername())) {
                okUsername = userDb.changeUsername(userId, newUsername.trim());
                if (!okUsername) {
                    request.getSession().setAttribute("error", "Username already exists or update failed.");
                    response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
                    return;
                }
            }

            StaffProfileBean staff = staffDb.getStaffByUserId(userId);
            if (staff != null) {
                staff.setFirstName(request.getParameter("staffFirstName"));
                staff.setLastName(request.getParameter("staffLastName"));
                staff.setGender(request.getParameter("staffGender"));
                staff.setDOB(request.getParameter("staffDOB"));
                staff.setPosition(request.getParameter("staffPosition"));

                Integer clinicId = parseIntOrNull(request.getParameter("staffClinicId"));
                staff.setClinicId(clinicId);

                boolean okStaff = staffDb.editStaff(staff);
                if (!okStaff) {
                    request.getSession().setAttribute("error", "Failed to update staff profile.");
                    response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
                    return;
                }
            }

            PatientProfileBean patient = patientDb.getPatientByUserId(userId);
            if (patient != null) {
                patient.setFirstName(request.getParameter("patientFirstName"));
                patient.setLastName(request.getParameter("patientLastName"));
                patient.setGender(request.getParameter("patientGender"));
                patient.setDOB(request.getParameter("patientDOB"));
                patient.setPhone(request.getParameter("patientPhone"));
                patient.setEmail(request.getParameter("patientEmail"));
                patient.setAddress(request.getParameter("patientAddress"));
                patient.setEmergencyContactFullName(request.getParameter("patientEcName"));
                patient.setEmergencyContact(request.getParameter("patientEcPhone"));

                boolean okPatient = patientDb.editPatient(patient);
                if (!okPatient) {
                    request.getSession().setAttribute("error", "Failed to update patient profile.");
                    response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
                    return;
                }
            }

            request.getSession().setAttribute("success", "User updated successfully.");
            response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/AdminUserDetail?userId=" + userId);
    }
}
