/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
import ict.bean.ServiceCapacityBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.ServiceCapacityDB;
import ict.db.ServiceDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AdminClinicDetailController", urlPatterns = {"/AdminClinicDetail"})
public class AdminClinicDetailController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private ServiceCapacityDB capDb;
    private AppointmentDB apptDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        capDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
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

    private String normOrNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private boolean isValidTimeSlot(String t) {
        if (t == null) {
            return false;
        }
        t = t.trim();
        return t.matches("^(24:00|([01]\\d|2[0-3]):[0-5]\\d)$");
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

        List<ServiceBean> services = serviceDb.getAllServices();

        Integer serviceId = parseIntOrNull(request.getParameter("serviceId"));
        if ((serviceId == null) && services != null && !services.isEmpty()) {
            serviceId = services.get(0).getServiceId();
        }

        List<ServiceCapacityBean> capList = null;
        ServiceBean selectedService = null;

        if (serviceId != null) {
            selectedService = serviceDb.getServiceById(serviceId);
            capList = capDb.getCapacityByClinicService(clinicId, serviceId);
        }

        request.setAttribute("clinic", clinic);
        request.setAttribute("services", services);
        request.setAttribute("selectedServiceId", serviceId);
        request.setAttribute("selectedService", selectedService);
        request.setAttribute("capacityList", capList);

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
        Integer serviceId = parseIntOrNull(request.getParameter("serviceId"));

        if (clinicId == null) {
            request.getSession().setAttribute("error", "Invalid clinicId.");
            response.sendRedirect(request.getContextPath() + "/AdminClinicList");
            return;
        }

        if ("updateClinic".equalsIgnoreCase(action)) {

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
            String address = normOrNull(request.getParameter("address"));
            String openTime = normOrNull(request.getParameter("openTime"));
            String closeTime = normOrNull(request.getParameter("closeTime"));
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

            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId
                    + (serviceId != null ? "&serviceId=" + serviceId : ""));
            return;
        }

        if ("updateCapacity".equalsIgnoreCase(action)) {
            Integer capacityId = parseIntOrNull(request.getParameter("capacityId"));
            Integer newQuota = parseIntOrNull(request.getParameter("quota"));

            if (serviceId == null || capacityId == null || newQuota == null || newQuota < 0) {
                request.getSession().setAttribute("error", "Invalid capacity update input.");
                response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId
                        + (serviceId != null ? "&serviceId=" + serviceId : ""));
                return;
            }

            boolean ok = capDb.updateCapacity(capacityId, newQuota);
            request.getSession().setAttribute(ok ? "success" : "error",
                    ok ? "Quota updated." : "Failed to update quota.");

            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
            return;
        }

        if ("createCapacity".equalsIgnoreCase(action)) {
            if (serviceId == null) {
                request.getSession().setAttribute("error", "Please select a service first.");
                response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId);
                return;
            }

            String timeSlot = normOrNull(request.getParameter("timeSlot"));
            Integer quota = parseIntOrNull(request.getParameter("quota"));

            if (timeSlot == null || !isValidTimeSlot(timeSlot) || quota == null || quota < 0) {
                request.getSession().setAttribute("error", "Invalid timeSlot. Use HH:mm (00:00-23:59) or 24:00.");
                response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
                return;
            }

            int newId = capDb.createCapacityRule(clinicId, serviceId, timeSlot, quota);
            request.getSession().setAttribute(newId > 0 ? "success" : "error",
                    newId > 0 ? "Capacity rule created." : "Failed to create capacity rule (maybe duplicate timeSlot).");

            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
            return;
        }

        if ("deleteCapacity".equalsIgnoreCase(action)) {
            Integer capacityId = parseIntOrNull(request.getParameter("capacityId"));
            String timeSlot = normOrNull(request.getParameter("timeSlot"));

            if (serviceId == null || capacityId == null || timeSlot == null) {
                request.getSession().setAttribute("error", "Invalid delete request.");
                response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
                return;
            }

            int used = apptDb.countActiveFutureAppointmentsByClinicServiceTimeSlot(clinicId, serviceId, timeSlot);
            if (used > 0) {
                request.getSession().setAttribute("error", "Cannot delete: there are existing appointments for this time slot.");
                response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
                return;
            }

            boolean ok = capDb.deleteCapacity(capacityId);
            request.getSession().setAttribute(ok ? "success" : "error",
                    ok ? "Capacity rule deleted." : "Failed to delete capacity rule.");

            response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId + "&serviceId=" + serviceId);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/AdminClinicDetail?clinicId=" + clinicId
                + (serviceId != null ? "&serviceId=" + serviceId : ""));
    }
}
