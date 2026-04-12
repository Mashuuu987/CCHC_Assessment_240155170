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
            throws ServletException, IOException{
        
        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;
         
        if (user == null){
            response.sendRedirect(request.getContextPath() + "/Login");
        }
        
        String role = user.getRole();
        if (role != null && role.equalsIgnoreCase("PATIENT")){
            PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
            request.setAttribute("patientProfile", patient);
        } else {
            //staff and admin
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            request.setAttribute("staffProfile", staff);
        }
        request.getRequestDispatcher("/common/profile.jsp").forward(request,response);
    }
}
