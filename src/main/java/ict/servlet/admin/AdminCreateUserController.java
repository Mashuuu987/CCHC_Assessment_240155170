/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.ClinicBean;
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
import java.util.List;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminCreateUserController", urlPatterns = {"/AdminCreateUser"})
public class AdminCreateUserController extends HttpServlet {

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

    private String norm(String s) {
        return s == null ? "" : s.trim();
    }

    private String normHKID(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private void stashCreateForm(HttpServletRequest request, HttpSession session, String lockedRole) {
        java.util.Map<String, String> m = new java.util.HashMap<>();

        m.put("roleLocked", lockedRole);
        m.put("username", norm(request.getParameter("username")));

        m.put("patientHKID", norm(request.getParameter("patientHKID")));
        m.put("patientPhone", norm(request.getParameter("patientPhone")));
        m.put("patientFirstName", norm(request.getParameter("patientFirstName")));
        m.put("patientLastName", norm(request.getParameter("patientLastName")));
        m.put("patientGender", norm(request.getParameter("patientGender")));
        m.put("patientDOB", norm(request.getParameter("patientDOB")));
        m.put("patientEmail", norm(request.getParameter("patientEmail")));
        m.put("patientAddress", norm(request.getParameter("patientAddress")));
        m.put("patientEcName", norm(request.getParameter("patientEcName")));
        m.put("patientEcPhone", norm(request.getParameter("patientEcPhone")));

        m.put("staffHKID", norm(request.getParameter("staffHKID")));
        m.put("staffPosition", norm(request.getParameter("staffPosition")));
        m.put("staffFirstName", norm(request.getParameter("staffFirstName")));
        m.put("staffLastName", norm(request.getParameter("staffLastName")));
        m.put("staffGender", norm(request.getParameter("staffGender")));
        m.put("staffDOB", norm(request.getParameter("staffDOB")));
        m.put("staffClinicId", norm(request.getParameter("staffClinicId")));

        session.setAttribute("createUserForm", m);
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
        if (!UserCheckUtil.hasRole(login, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        HttpSession session = request.getSession();

        if ("1".equals(request.getParameter("reset"))) {
            session.removeAttribute("createUserRole");
        }

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        request.setAttribute("clinics", clinics);

        String selectedRole = (String) session.getAttribute("createUserRole");
        if (selectedRole == null) {
            selectedRole = "";
        }
        request.setAttribute("selectedRole", selectedRole);

        Object formdata = session.getAttribute("createUserForm");
        if (formdata != null) {
            request.setAttribute("formData", formdata);
            session.removeAttribute("createUserForm");
        }

        Object err = session.getAttribute("error");
        if (err != null) {
            request.setAttribute("error", err.toString());
            session.removeAttribute("error");
        }
        Object suc = session.getAttribute("success");
        if (suc != null) {
            request.setAttribute("success", suc.toString());
            session.removeAttribute("success");
        }

        request.getRequestDispatcher("/admin/adminCreateUser.jsp").forward(request, response);
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
        if (!UserCheckUtil.hasRole(login, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        HttpSession session = request.getSession();
        String action = norm(request.getParameter("action"));

        // STEP 1
        if ("chooseRole".equalsIgnoreCase(action)) {
            String role = norm(request.getParameter("role")).toUpperCase();
            if (!("PATIENT".equals(role) || "STAFF".equals(role) || "ADMIN".equals(role))) {
                session.setAttribute("error", "Invalid role.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }
            session.setAttribute("createUserRole", role);
            response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
            return;
        }

        // STEP 2
        if ("create".equalsIgnoreCase(action)) {

            String lockedRole = (String) session.getAttribute("createUserRole");
            if (lockedRole == null || lockedRole.isBlank()) {
                session.setAttribute("error", "Please choose role first.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }

            String submittedRole = norm(request.getParameter("roleLocked")).toUpperCase();
            if (!lockedRole.equalsIgnoreCase(submittedRole)) {
                session.setAttribute("error", "Role has changed. Please restart create process.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser?reset=1");
                return;
            }

            String username = norm(request.getParameter("username"));
            String password = norm(request.getParameter("password"));

            if (username.isBlank() || password.isBlank()) {
                stashCreateForm(request, session, lockedRole);
                session.setAttribute("error", "Username and password are required.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }

            if (userDb.getUserByUsername(username) != null) {
                stashCreateForm(request, session, lockedRole);
                session.setAttribute("error", "Username already exists.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }

            int newUserId = userDb.createUser(username, password, lockedRole);
            if (newUserId <= 0) {
                stashCreateForm(request, session, lockedRole);
                session.setAttribute("error", "Failed to create user account.");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }

            try {
                if ("PATIENT".equalsIgnoreCase(lockedRole)) {
                    String hkid = normHKID(request.getParameter("patientHKID"));
                    if (hkid.isBlank()
                            || patientDb.getPatientByHKIdOrID(hkid) != null
                            || staffDb.getStaffByHKIdOrID(hkid) != null) {
                        stashCreateForm(request, session, lockedRole);
                        userDb.delUser(newUserId);
                        session.setAttribute("error", "HKID/ID already exists (patient/staff).");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }

                    String firstName = norm(request.getParameter("patientFirstName"));
                    String lastName = norm(request.getParameter("patientLastName"));
                    String gender = norm(request.getParameter("patientGender"));
                    String dob = norm(request.getParameter("patientDOB"));
                    String phone = norm(request.getParameter("patientPhone"));

                    if (firstName.isBlank() || lastName.isBlank() || gender.isBlank() || dob.isBlank() || phone.isBlank()) {
                        userDb.delUser(newUserId);
                        stashCreateForm(request, session, lockedRole);
                        session.setAttribute("error", "Patient profile required fields are missing.");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }

                    int pid = patientDb.createPatientProfile(
                            newUserId,
                            hkid,
                            firstName, lastName,
                            gender, dob,
                            phone,
                            norm(request.getParameter("patientEmail")),
                            norm(request.getParameter("patientAddress")),
                            norm(request.getParameter("patientEcName")),
                            norm(request.getParameter("patientEcPhone"))
                    );

                    if (pid <= 0) {
                        userDb.delUser(newUserId);
                        stashCreateForm(request, session, lockedRole);
                        session.setAttribute("error", "Failed to create patient profile.");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }

                } else {
                    String hkid = normHKID(request.getParameter("staffHKID"));
                    if (hkid.isBlank()
                            || staffDb.getStaffByHKIdOrID(hkid) != null
                            || patientDb.getPatientByHKIdOrID(hkid) != null) {
                        userDb.delUser(newUserId);
                        stashCreateForm(request, session, lockedRole);
                        session.setAttribute("error", "HKID/ID already exists (staff/patient).");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }

                    String firstName = norm(request.getParameter("staffFirstName"));
                    String lastName = norm(request.getParameter("staffLastName"));
                    String gender = norm(request.getParameter("staffGender"));
                    String dob = norm(request.getParameter("staffDOB"));
                    String position = norm(request.getParameter("staffPosition"));

                    if (firstName.isBlank() || lastName.isBlank() || gender.isBlank() || dob.isBlank()) {
                        userDb.delUser(newUserId);
                        stashCreateForm(request, session, lockedRole);
                        session.setAttribute("error", "Staff profile required fields are missing.");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }

                    Integer clinicId = null;
                    if ("STAFF".equalsIgnoreCase(lockedRole)) {
                        clinicId = parseIntOrNull(request.getParameter("staffClinicId"));
                        if (clinicId == null) {
                            userDb.delUser(newUserId);
                            stashCreateForm(request, session, lockedRole);
                            session.setAttribute("error", "STAFF must select a clinic.");
                            response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                            return;
                        }
                    }

                    int sid = staffDb.createStaffProfile(
                            newUserId,
                            hkid,
                            firstName, lastName,
                            gender, dob,
                            clinicId,
                            position.isBlank() ? null : position
                    );

                    if (sid <= 0) {
                        userDb.delUser(newUserId);
                        stashCreateForm(request, session, lockedRole);
                        session.setAttribute("error", "Failed to create staff profile.");
                        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                        return;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                userDb.delUser(newUserId);
                stashCreateForm(request, session, lockedRole);
                session.setAttribute("error", "Create failed (exception).");
                response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
                return;
            }

            session.removeAttribute("createUserRole");
            session.setAttribute("success", "User created successfully.");
            response.sendRedirect(request.getContextPath() + "/AdminUserList");
            return;
        }

        session.setAttribute("error", "Invalid action.");
        response.sendRedirect(request.getContextPath() + "/AdminCreateUser");
    }
}
