/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.patient;

import java.io.IOException;

import ict.bean.PatientProfileBean;
import ict.bean.UserInfoBean;
import ict.db.PatientDB;
import ict.db.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author amzte
 */
@WebServlet(name = "PatientRegisterController", urlPatterns = {"/PatientRegister"})
public class PatientRegisterController extends HttpServlet {

    private UserDB userDb;
    private PatientDB patientDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        userDb = new UserDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login-Process/patientRegister.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");
        String hkid = request.getParameter("hkid");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("dob");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String ecName = request.getParameter("emergencyContactFullName");
        String ecPhone = request.getParameter("emergencyContact");

        String error = null;
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()
                || confirm == null || confirm.isEmpty()
                || hkid == null || hkid.isEmpty()
                || firstName == null || firstName.isEmpty()
                || lastName == null || lastName.isEmpty()
                || gender == null || gender.isEmpty()
                || dob == null || dob.isEmpty()
                || phone == null || phone.isEmpty()) {
            error = "Please fill in all required fields!";
        } else if (!password.equals(confirm)) {
            error = "Password and confirm password do not match!";
        } else if (password.length() < 6) {
            error = "New password must be at least 6 characters.";
        }

        if (error != null) {
            request.setAttribute("registerError", error);
            request.setAttribute("username", username);
            request.setAttribute("hkid", hkid);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("gender", gender);
            request.setAttribute("dob", dob);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("emergencyContactFullName", ecName);
            request.setAttribute("emergencyContact", ecPhone);

            request.getRequestDispatcher("/login-Process/patientRegister.jsp").forward(request, response);
            return;
        }

        UserInfoBean user_name_existing = userDb.getUserByUsername(username);
        if (user_name_existing != null) {
            error = "Username already exists.";
        }

        if (error != null) {
            request.setAttribute("registerError", error);
            request.setAttribute("username", username);
            request.setAttribute("hkid", hkid);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("gender", gender);
            request.setAttribute("dob", dob);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("emergencyContactFullName", ecName);
            request.setAttribute("emergencyContact", ecPhone);

            request.getRequestDispatcher("/login-Process/patientRegister.jsp").forward(request, response);
            return;
        }
        
        PatientProfileBean HKID_OR_ID_existing = patientDb.getPatientByHKIdOrID(hkid);
        if (HKID_OR_ID_existing != null) {
            error = "HKID/ID already exists.";
        }

        if (error != null) {
            request.setAttribute("registerError", error);
            request.setAttribute("username", username);
            request.setAttribute("hkid", hkid);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("gender", gender);
            request.setAttribute("dob", dob);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("emergencyContactFullName", ecName);
            request.setAttribute("emergencyContact", ecPhone);

            request.getRequestDispatcher("/login-Process/patientRegister.jsp").forward(request, response);
            return;
        }

        int userId = userDb.createUser(username, password, "PATIENT");
        if (userId == -1) {
            error = "Failed to create user account. Please try again.";
        }

        if (error == null) {
            int patientId = patientDb.createPatientProfile(
                    userId,
                    hkid,
                    firstName,
                    lastName,
                    gender,
                    dob,
                    phone,
                    email,
                    address,
                    ecName,
                    ecPhone
            );
            if (patientId == -1) {
                try {
                    userDb.delUser(userId);
                } catch (Exception e) {
        
                }
                error = "Failed to create patient profile. Please try again.";
            }
        }

        if (error != null) {
            request.setAttribute("registerError", error);
            request.setAttribute("username", username);
            request.setAttribute("hkid", hkid);
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("gender", gender);
            request.setAttribute("dob", dob);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("emergencyContactFullName", ecName);
            request.setAttribute("emergencyContact", ecPhone);

            request.getRequestDispatcher("/login-Process/patientRegister.jsp").forward(request, response);
        } else {
            request.setAttribute("selectedRole", "PATIENT");
            request.setAttribute("enteredUsername", username);
            request.setAttribute("registerSuccess", "Registration successful. Please login.");
            request.getRequestDispatcher("/login-Process/login.jsp").forward(request, response);
        }
    }
}
