/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet;

import ict.bean.PatientProfileBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.PatientDB;
import ict.db.StaffDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author amzte
 */
@WebServlet(name = "ProfileController", urlPatterns = {"/Profile"})
public class ProfileController extends HttpServlet {

    private PatientDB patientDb;
    private StaffDB staffDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String role = user.getRole();
        if (role != null && role.equalsIgnoreCase("PATIENT")) {
            PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
            request.setAttribute("patientProfile", patient);
        } else {
            //staff and admin
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            request.setAttribute("staffProfile", staff);
        }
        request.getRequestDispatcher("/common/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        String role = user.getRole();
        boolean success = false;
        
        if (role != null && role.equalsIgnoreCase("PATIENT")) {
            PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
            if (patient != null) {
                patient.setFirstName(request.getParameter("firstName"));
                patient.setLastName(request.getParameter("lastName"));
                patient.setGender(request.getParameter("gender"));
                patient.setDOB(request.getParameter("dob"));
                patient.setPhone(request.getParameter("phone"));
                patient.setEmail(request.getParameter("email"));
                patient.setAddress(request.getParameter("address"));
                patient.setEmergencyContactFullName(request.getParameter("emergencyContactFullName"));
                patient.setEmergencyContact(request.getParameter("emergencyContact"));

                success = patientDb.editPatient(patient);
                request.setAttribute("patientProfile",
                        patientDb.getPatientByUserId(user.getUserId()));
            }
        } else {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff != null) {
                staff.setFirstName(request.getParameter("firstName"));
                staff.setLastName(request.getParameter("lastName"));
                staff.setGender(request.getParameter("gender"));
                staff.setDOB(request.getParameter("dob"));
                staff.setPosition(request.getParameter("position"));

                String clinicIdStr = request.getParameter("clinicId");
                if (clinicIdStr != null && clinicIdStr.trim().length() > 0) {
                    try {
                        staff.setClinicId(Integer.parseInt(clinicIdStr.trim()));
                    } catch (NumberFormatException e) {
                        staff.setClinicId(null);
                    }
                } else {
                    staff.setClinicId(null);
                }

                success = staffDb.editStaff(staff);
                request.setAttribute("staffProfile",
                        staffDb.getStaffByUserId(user.getUserId()));
            }
        }

        if (success) {
            request.setAttribute("message", "Profile updated successfully.");
        } else {
            request.setAttribute("message", "Failed to update profile. Please check your input.");
        }

        request.getRequestDispatcher("/common/profile.jsp").forward(request, response);
    }
}
