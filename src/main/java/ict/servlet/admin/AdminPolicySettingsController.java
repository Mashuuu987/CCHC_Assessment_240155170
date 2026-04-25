/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.UserInfoBean;
import ict.db.PolicyDB;
import ict.util.UserCheckUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminPolicySettingsController", urlPatterns = {"/AdminPolicySettings"})
public class AdminPolicySettingsController extends HttpServlet {

    private PolicyDB policyDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        policyDb = new PolicyDB(dbUrl, dbUser, dbPassword);
        policyDb.ensureDefaults();

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

        UserInfoBean u = UserCheckUtil.getLoginUser(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (!UserCheckUtil.hasRole(u, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        int maxActive = policyDb.getMaxActiveAppointments();

        request.setAttribute("maxActiveAppointments", maxActive);

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

        request.getRequestDispatcher("/admin/adminPolicySettings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean u = UserCheckUtil.getLoginUser(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (!UserCheckUtil.hasRole(u, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        Integer maxActive = parseIntOrNull(request.getParameter("maxActiveAppointments"));

        if (maxActive == null || maxActive < 1 || maxActive > 20) {
            request.getSession().setAttribute("error", "Max active appointments must be between 1 and 20.");
            response.sendRedirect(request.getContextPath() + "/AdminPolicySettings");
            return;
        }

        boolean ok = policyDb.setMaxActiveAppointments(maxActive);
        request.getSession().setAttribute(ok ? "success" : "error",
                ok ? "Policy updated successfully." : "Failed to update policy.");

        response.sendRedirect(request.getContextPath() + "/AdminPolicySettings");
    }
}
