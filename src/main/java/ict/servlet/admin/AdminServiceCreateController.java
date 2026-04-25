/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.QueueSettingDB;
import ict.db.ServiceDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminServiceCreateController", urlPatterns = {"/AdminServiceCreate"})
public class AdminServiceCreateController extends HttpServlet {

    private ServiceDB serviceDb;
    private ClinicDB clinicDb;
    private QueueSettingDB queueSettingDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        queueSettingDb = new QueueSettingDB(dbUrl, dbUser, dbPassword);
    }

    private String normOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isBlank() ? null : t;
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
        if (!UserCheckUtil.hasRole(login, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object err = session.getAttribute("error");
            if (err != null) {
                request.setAttribute("error", err.toString());
                session.removeAttribute("error");
            }
        }

        request.getRequestDispatcher("/admin/adminServiceCreate.jsp").forward(request, response);
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

        String name = normOrNull(request.getParameter("name"));
        String description = normOrNull(request.getParameter("description"));
        String serviceType = normOrNull(request.getParameter("serviceType"));
        Integer duration = parseIntOrNull(request.getParameter("durationMins"));

        if (name == null || duration == null || duration <= 0) {
            request.getSession().setAttribute("error", "Service name and duration (mins) are required (duration > 0).");
            response.sendRedirect(request.getContextPath() + "/AdminServiceCreate");
            return;
        }

        if (serviceType == null) {
            serviceType = "CONSULTATION";
        }
        serviceType = serviceType.toUpperCase();

        int newId = serviceDb.createService(name, description, serviceType, duration);

        if (newId > 0) {
            queueSettingDb.ensureSettingsForNewService(newId);

            request.getSession().setAttribute("success",
                    "Service created (ID: " + newId + "). Queue settings initialized.");
        } else {
            request.getSession().setAttribute("error", "Failed to create service.");
        }

        response.sendRedirect(request.getContextPath() + "/AdminClinicList");
        return;
    }
}
