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
import java.util.*;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminClinicListController", urlPatterns = {"/AdminClinicList"})
public class AdminClinicListController extends HttpServlet {

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
            if (s == null || s.isBlank()) {
                return null;
            }
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String ns(String s) {
        return s == null ? "" : s;
    }

    private boolean containsIgnoreCase(String text, String kw) {
        if (kw == null || kw.isBlank()) {
            return true;
        }
        if (text == null) {
            return false;
        }
        return text.toLowerCase().contains(kw.trim().toLowerCase());
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

        Integer fClinicId = parseIntOrNull(request.getParameter("clinicId"));
        String fName = ns(request.getParameter("name")).trim();
        String fDistrict = ns(request.getParameter("district")).trim();
        String fCloseDay = ns(request.getParameter("closeDay")).trim();
        String fKeyword = ns(request.getParameter("keyword")).trim();

        request.setAttribute("filterClinicId", fClinicId);
        request.setAttribute("filterName", fName);
        request.setAttribute("filterDistrict", fDistrict);
        request.setAttribute("filterCloseDay", fCloseDay);
        request.setAttribute("filterKeyword", fKeyword);

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        List<Map<String, Object>> viewRows = new ArrayList<>();

        if (clinics != null) {
            for (ClinicBean c : clinics) {

                if (fClinicId != null && c.getClinicId() != fClinicId) {
                    continue;
                }
                if (!containsIgnoreCase(c.getName(), fName)) {
                    continue;
                }
                if (!containsIgnoreCase(c.getDistrict(), fDistrict)) {
                    continue;
                }
                if (!fCloseDay.isBlank() && !containsIgnoreCase(c.getCloseDay(), fCloseDay)) {
                    continue;
                }

                String combined = ns(c.getName()) + " " + ns(c.getDistrict()) + " " + ns(c.getAddress());
                if (!containsIgnoreCase(combined, fKeyword)) {
                    continue;
                }

                Map<String, Object> row = new HashMap<>();
                row.put("clinicId", c.getClinicId());
                row.put("name", ns(c.getName()));
                row.put("district", ns(c.getDistrict()));
                row.put("address", ns(c.getAddress()));
                row.put("openTime", ns(c.getOpenTime()));
                row.put("closeTime", ns(c.getCloseTime()));
                row.put("closeDay", ns(c.getCloseDay()));
                viewRows.add(row);
            }
        }

        request.setAttribute("viewRows", viewRows);
        request.setAttribute("colCount", 8);

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

        request.getRequestDispatcher("/admin/clinicList.jsp").forward(request, response);
    }
}
