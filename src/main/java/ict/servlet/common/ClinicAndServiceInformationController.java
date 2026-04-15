/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
import ict.bean.ServiceCapacityBean;
import ict.db.ClinicDB;
import ict.db.ServiceCapacityDB;
import ict.db.ServiceDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amzte
 */
@WebServlet(name = "ClinicAndServiceInformationController", urlPatterns = {"/ClinicAndServiceInformation"})
public class ClinicAndServiceInformationController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private ServiceCapacityDB scDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        scDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ClinicBean> clinicList = clinicDb.getAllClinics();
        List<ServiceBean> serviceList = serviceDb.getAllServices();
        List<ServiceCapacityBean> scList = scDb.getAllServiceCapacity();

        request.setAttribute("clinics", clinicList);
        request.setAttribute("services", serviceList);
        request.setAttribute("capacities", scList);
        
        request.getRequestDispatcher("/common/ClinicAndServiceInfromation.jsp").forward(request,response);
    }
}
