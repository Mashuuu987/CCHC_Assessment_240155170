/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.ClinicBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 *
 * @author amzte
 */
@WebServlet(name="AdminClinicDetailController", urlPatterns={"/AdminClinicDetail"})
public class AdminClinicDetailController extends HttpServlet {

    private ClinicDB clinicDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String normOrNull(String s) {
        if (s == null) return null;
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

        Integer clinicId = parseIntOrNull(request.getParameter("clinicId"));
        if (clinicId == null) {
            request.setAttribute("error", "Invalid clinicId.");
            request.getRequestDispatcher("/admin/adminClinicDetail.jsp").forward(request, response);
            return;
        }

        ClinicBean clinic = clinicDb.getClinicByID(clinicId);
        if (clinic == null) {
            request.setAttribute("error", "Clinic not found.");
            request.getRequestDispatcher("/admin/adminClinicDetail.jsp").forward(request, response);
            return;
        }

        request.setAttribute("clinic", clinic);

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object suc = session.getAttribute("success");
            Object err = session.getAttribute("error");
            if (suc != null) { request.setAttribute("success", suc.toString()); session.removeAttribute("success"); }
            if (err != null) { request.setAttribute("error", err.toString()); session.removeAttribute("error"); }
        }

        request.getRequestDispatcher("/admin/adminClinicDetail.jsp").forward(request, response);
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

        String action = request.getParameter("action");
        Integer clinicId = parseIntOrNull(request.getParameter("clinicId"));
        if (clinicId == null) {
            request.getSession().setAttribute("error", "Invalid clinicId.");
            response.sendRedirect(request.getContextPath() + "/AdminClinicList");
            return;
        }

        if (!"update".equalsIgnoreCase(action)) {
            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId);
            return;
        }

        ClinicBean clinic = clinicDb.getClinicByID(clinicId);
        if (clinic == null) {
            request.getSession().setAttribute("error", "Clinic not found.");
            response.sendRedirect(request.getContextPath() + "/AdminClinicList");
            return;
        }

        String name = normOrNull(request.getParameter("name"));
        if (name == null) {
            request.getSession().setAttribute("error", "Clinic name is required.");
            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId);
            return;
        }

        String district = normOrNull(request.getParameter("district"));
        String address  = normOrNull(request.getParameter("address"));
        String openTime = normOrNull(request.getParameter("openTime"));
        String closeTime= normOrNull(request.getParameter("closeTime"));
        String closeDay = normOrNull(request.getParameter("closeDay"));

        clinic.setName(name);
        clinic.setDistrict(district);
        clinic.setAddress(address);
        clinic.setOpenTime(openTime);
        clinic.setCloseTime(closeTime);
        clinic.setCloseDay(closeDay);

        boolean ok = clinicDb.editClinic(clinic);

        request.getSession().setAttribute(ok ? "success" : "error",
                ok ? "Clinic updated successfully." : "Failed to update clinic.");

        response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId);
    }
}
