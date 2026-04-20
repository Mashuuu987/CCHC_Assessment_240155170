/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

/**
 *
 * @author amzte
 */
@WebServlet(name = "ClinicAndServiceInfomationController", urlPatterns = {"/ClinicAndServiceInfomation"})
public class ClinicAndServiceInfomationController extends HttpServlet {

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ClinicBean> clinicList = clinicDb.getAllClinics();
        List<ServiceBean> serviceList = serviceDb.getAllServices();
        List<ServiceCapacityBean> scList = scDb.getAllServiceCapacity();

        Set<String> districtsSet = new HashSet<>();
        Set<String> serviceTypesSet = new HashSet<>();

        if (clinicList != null) {
            for (ClinicBean clinic : clinicList) {
                if (clinic.getDistrict() != null && !clinic.getDistrict().isEmpty()) {
                    districtsSet.add(clinic.getDistrict());
                }
            }
        }

        if (serviceList != null) {
            for (ServiceBean service : serviceList) {
                if (service.getServiceType() != null && !service.getServiceType().isEmpty()) {
                    serviceTypesSet.add(service.getServiceType());
                }
            }
        }

        List<String> districtsList = new ArrayList<>(districtsSet);
        districtsList.sort(null);

        List<String> serviceTypesList = new ArrayList<>(serviceTypesSet);
        serviceTypesList.sort(null);

        Map<Integer, ServiceBean> serviceById = new HashMap<>();
        if (serviceList != null) {
            for (ServiceBean service : serviceList) {
                serviceById.put(service.getServiceId(), service);
            }
        }

        Map<Integer, List<ServiceCapacityBean>> capacitiesByClinic = new HashMap<>();
        Map<Integer, Set<String>> clinicServiceTypesMap = new HashMap<>();

        if (scList != null) {
            for (ServiceCapacityBean cap : scList) {
                int clinicId = cap.getClinicId();

                List<ServiceCapacityBean> clinicCapList = capacitiesByClinic.get(clinicId);
                if (clinicCapList == null) {
                    clinicCapList = new ArrayList<>();
                    capacitiesByClinic.put(clinicId, clinicCapList);
                }
                clinicCapList.add(cap);

                ServiceBean service = serviceById.get(cap.getServiceId());
                if (service != null && service.getServiceType() != null && !service.getServiceType().isEmpty()) {
                    Set<String> clinicServiceTypes = clinicServiceTypesMap.get(clinicId);
                    if (clinicServiceTypes == null) {
                        clinicServiceTypes = new HashSet<>();
                        clinicServiceTypesMap.put(clinicId, clinicServiceTypes);
                    }
                    clinicServiceTypes.add(service.getServiceType());
                }
            }
        }

        Map<Integer, String> clinicServiceTypesStrMap = new HashMap<>();
        for (Map.Entry<Integer, Set<String>> e : clinicServiceTypesMap.entrySet()) {
            clinicServiceTypesStrMap.put(e.getKey(), String.join(",", e.getValue()));
        }

        request.setAttribute("clinics", clinicList);
        request.setAttribute("services", serviceList);
        request.setAttribute("capacities", scList);
        request.setAttribute("districtsList", districtsList);
        request.setAttribute("serviceTypesList", serviceTypesList);
        request.setAttribute("serviceById", serviceById);
        request.setAttribute("capacitiesByClinic", capacitiesByClinic);
        request.setAttribute("clinicServiceTypesStrMap", clinicServiceTypesStrMap);
        
        request.getRequestDispatcher("/common/clinicAndServiceInfomation.jsp").forward(request,response);
    }
}
