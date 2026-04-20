/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;

import ict.bean.PatientProfileBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.PatientDB;
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

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        db = new UserDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
    }

    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (db.isValidUser(username, password)) {
            UserInfoBean bean = db.getUserByUsername(username);

            HttpSession session = request.getSession();
            session.setAttribute("userInfo", bean);

            try {
                if ("PATIENT".equalsIgnoreCase(bean.getRole())) {
                    PatientProfileBean p = patientDb.getPatientByUserId(bean.getUserId());
                    if (p != null) {
                        session.setAttribute("displayFirstName", p.getFirstName());
                        session.setAttribute("displayLastName", p.getLastName());
                    }
                } else if ("STAFF".equalsIgnoreCase(bean.getRole())) {
                    StaffProfileBean s = staffDb.getStaffByUserId(bean.getUserId());
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
                response.sendRedirect(ctx + "/PublicHome");
            }
        } else {
            request.setAttribute("loginError", "Invalid username or password.");
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
            response.sendRedirect(ctx + "/PublicHome");
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
            response.sendRedirect(ctx + "/PublicHome");
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
