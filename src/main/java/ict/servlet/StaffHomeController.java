/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet;

import java.io.IOException;
import java.util.List;

import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.ServiceDB;
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
@WebServlet(name = "StaffHomeController", urlPatterns = {"/StaffHome"})
public class StaffHomeController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        if (user == null || user.getRole() == null || !"STAFF".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        List<ServiceBean> services = serviceDb.getAllServices();

        request.setAttribute("clinics", clinics);
        request.setAttribute("services", services);

        request.getRequestDispatcher("/staff/staffIndex.jsp").forward(request, response);
    }
}
