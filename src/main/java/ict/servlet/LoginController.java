/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package ict.servlet;

import java.io.IOException;

import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.QueueTicketDB;
import ict.db.ServiceCapacityDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.db.UserDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author amzte
 */
@WebServlet(name = "LoginController", urlPatterns = {"/Login"})
public class LoginController extends HttpServlet {

    private UserDB db;
    private PatientDB patientDb;
    private StaffDB staffDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private AppointmentDB apptDb;
    private QueueTicketDB queueDb;
    private NotificationDB notifDb;
    private ServiceCapacityDB capDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        
        db = new UserDB(dbUrl, dbUser, dbPassword);
        db.createUserInfoTable();
        db.insertDefaultUserIfEmpty();

        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        patientDb.createPatientProfileTable();
        patientDb.insertDefaultPatientIfEmpty();

        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        staffDb.createStaffProfileTable();
        staffDb.insertDefaultStaffIfEmpty();

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        clinicDb.createClinicTable();
        clinicDb.insertDefaultClinicsIfEmpty();

        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        serviceDb.createServiceTable();
        serviceDb.insertDefaultServicesIfEmpty();

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        apptDb.createAppointmentTable();

        queueDb = new QueueTicketDB(dbUrl, dbUser, dbPassword);
        queueDb.createQueueTicketTable();

        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        notifDb.createNotificationTable();
        notifDb.insertDefaultNotificationIfEmpty();
        
        capDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
        capDb.createServiceCapacityTable();
        capDb.insertDefaultCapacitiesIfEmpty();
    }

    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("loginRole");

        if (db.isValidUser(username, password)) {
            UserInfoBean bean = db.getUserByUsername(username);

            if (selectedRole != null && !selectedRole.isEmpty()) {
                String Role = bean.getRole();
                boolean success = false;

                if ("PATIENT".equalsIgnoreCase(selectedRole)) {
                    success = "PATIENT".equalsIgnoreCase(Role);
                } else if ("STAFF".equalsIgnoreCase(selectedRole)) {
                    success = "STAFF".equalsIgnoreCase(Role)
                              || "ADMIN".equalsIgnoreCase(Role);
                }

                if (!success) {
                    request.setAttribute("loginError", "Invalid username or password.");
                    request.setAttribute("selectedRole", selectedRole);
                    request.setAttribute("enteredUsername", username);
                    request.getRequestDispatcher("/login-Process/login.jsp").forward(request, response);
                    return;
                }
            }

            HttpSession session = request.getSession();
            session.setAttribute("userInfo", bean);

            try {
                if ("PATIENT".equalsIgnoreCase(bean.getRole())) {
                    ict.bean.PatientProfileBean p = patientDb.getPatientByUserId(bean.getUserId());
                    if (p != null) {
                        session.setAttribute("displayFirstName", p.getFirstName());
                        session.setAttribute("displayLastName", p.getLastName());
                    }
                } else if ("STAFF".equalsIgnoreCase(bean.getRole())) {
                    ict.bean.StaffProfileBean s = staffDb.getStaffByUserId(bean.getUserId());
                    if (s != null) {
                        session.setAttribute("displayFirstName", s.getFirstName());
                        session.setAttribute("displayLastName", s.getLastName());
                    }
                } else {
                    session.removeAttribute("displayFirstName");
                    session.removeAttribute("displayLastName");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String ctx = request.getContextPath();
            String role = bean.getRole();

            if ("ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(ctx + "/AdminHome");
            } else if ("STAFF".equalsIgnoreCase(role)) {
                response.sendRedirect(ctx + "/StaffHome");
            } else if ("PATIENT".equalsIgnoreCase(role)) {
                response.sendRedirect(ctx + "/PatientHome");
            } else {
                response.sendRedirect(ctx + "/index.jsp");
            }
        } else {
            request.setAttribute("loginError", "Invalid username or password.");
            request.setAttribute("selectedRole", selectedRole);
            request.setAttribute("enteredUsername", username);
            request.getRequestDispatcher("/login-Process/login.jsp").forward(request, response);
        }
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session.getAttribute("userInfo") != null;
    }

    public void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login-Process/login.jsp").forward(request, response);
    }

    public void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("userInfo");
            session.invalidate();
        }
        doLogin(request, response);
    }

    public void showWelcome(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        String ctx = request.getContextPath();

        if (user == null || user.getRole() == null) {
            response.sendRedirect(ctx + "/index.jsp");
            return;
        }

        String role = user.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/AdminHome");
        } else if ("STAFF".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/StaffHome");
        } else if ("PATIENT".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/PatientHome");
        } else {
            response.sendRedirect(ctx + "/index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (!isAuthenticated(request)) {
            doLogin(request, response);
        } else if ("logout".equals(action)) {
            doLogout(request, response);
        } else {
            showWelcome(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (!isAuthenticated(request) && !("authenticate".equals(action))) {
            doLogin(request, response);
        } else if ("authenticate".equals(action)) {
            doAuthenticate(request, response);
        } else if ("logout".equals(action)) {
            doLogout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }
}
