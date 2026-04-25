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
@WebServlet(name = "AdminClinicCreateController", urlPatterns = {"/AdminClinicCreate"})
public class AdminClinicCreateController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private QueueSettingDB queueSettingDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        queueSettingDb = new QueueSettingDB(dbUrl, dbUser, dbPassword);

    }

    private String normOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isBlank() ? null : t;
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

        request.getRequestDispatcher("/admin/adminClinicCreate.jsp").forward(request, response);
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
        String district = normOrNull(request.getParameter("district"));
        String address = normOrNull(request.getParameter("address"));
        String openTime = normOrNull(request.getParameter("openTime"));
        String closeTime = normOrNull(request.getParameter("closeTime"));
        String closeDay = normOrNull(request.getParameter("closeDay"));

        if (name == null) {
            request.getSession().setAttribute("error", "Clinic name is required.");
            response.sendRedirect(request.getContextPath() + "/AdminClinicCreate");
            return;
        }

        int newId = clinicDb.createClinic(name, district, address, openTime, closeTime, closeDay);

        if (newId > 0) {
            queueSettingDb.ensureSettingsForNewClinic(newId);
            request.getSession().setAttribute("success",
                    "Clinic created (ID: " + newId + "). Queue settings initialized.");
        } else {
            request.getSession().setAttribute("error", "Failed to create clinic.");
        }

        response.sendRedirect(request.getContextPath() + "/AdminClinicList");
        return;

    }
}
