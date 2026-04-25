/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.admin;

import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author amzte
 */


@WebServlet(name="AdminAnalyticsController", urlPatterns={"/AdminAnalytics"})
public class AdminAnalyticsController extends HttpServlet {

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
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private DayOfWeek parseCloseDay(String closeDay) {
        if (closeDay == null || closeDay.isBlank()) return null;
        try {
            return DayOfWeek.valueOf(closeDay.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    private int countCloseDayInMonth(YearMonth ym, DayOfWeek closeDay) {
        if (closeDay == null) return 0;
        int count = 0;
        LocalDate d = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        while (!d.isAfter(end)) {
            if (d.getDayOfWeek() == closeDay) count++;
            d = d.plusDays(1);
        }
        return count;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (!UserCheckUtil.hasRole(user, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        List<ServiceBean> services = serviceDb.getAllServices();
        request.setAttribute("clinics", clinics);
        request.setAttribute("services", services);

        String clinicIdRaw = request.getParameter("clinicId");
        String serviceIdRaw = request.getParameter("serviceId");
        String monthStr = request.getParameter("month"); // "YYYY-MM"

        String clinicSel = (clinicIdRaw == null) ? "" : clinicIdRaw.trim();
        String serviceSel = (serviceIdRaw == null) ? "" : serviceIdRaw.trim();
        request.setAttribute("filterClinicSel", clinicSel);
        request.setAttribute("filterServiceSel", serviceSel);
        request.setAttribute("filterMonth", monthStr == null ? "" : monthStr);

        if (monthStr == null || monthStr.isBlank()) {
            request.getRequestDispatcher("/admin/adminAnalytics.jsp").forward(request, response);
            return;
        }

        YearMonth ym;
        try {
            ym = YearMonth.parse(monthStr.trim());
        } catch (Exception e) {
            request.setAttribute("error", "Invalid month format. Use YYYY-MM.");
            request.getRequestDispatcher("/admin/adminAnalytics.jsp").forward(request, response);
            return;
        }

        Integer clinicId = null;
        Integer serviceId = null;

        if (clinicIdRaw != null && !clinicIdRaw.isBlank() && !"ALL".equalsIgnoreCase(clinicIdRaw.trim())) {
            clinicId = parseIntOrNull(clinicIdRaw);
            if (clinicId == null) {
                request.setAttribute("error", "Invalid clinicId.");
                request.getRequestDispatcher("/admin/adminAnalytics.jsp").forward(request, response);
                return;
            }
        }

        if (serviceIdRaw != null && !serviceIdRaw.isBlank() && !"ALL".equalsIgnoreCase(serviceIdRaw.trim())) {
            serviceId = parseIntOrNull(serviceIdRaw);
            if (serviceId == null) {
                request.setAttribute("error", "Invalid serviceId.");
                request.getRequestDispatcher("/admin/adminAnalytics.jsp").forward(request, response);
                return;
            }
        }

        String startDate = ym.atDay(1).toString();
        String endDate = ym.atEndOfMonth().toString();

        List<Map<String, Object>> detailRows = new ArrayList<>();
        long totalAvailable = 0L;

        if (clinics != null && services != null) {
            for (ClinicBean c : clinics) {
                if (clinicId != null && c.getClinicId() != clinicId) continue;

                int daysInMonth = ym.lengthOfMonth();
                DayOfWeek closeDay = parseCloseDay(c.getCloseDay());
                int closedDays = countCloseDayInMonth(ym, closeDay);
                int openDays = Math.max(0, daysInMonth - closedDays);

                for (ServiceBean s : services) {
                    if (serviceId != null && s.getServiceId() != serviceId) continue;

                    int dailyQuota = capDb.sumDailyQuota(c.getClinicId(), s.getServiceId());
                    long available = (long) dailyQuota * (long) openDays;

                    int booked = apptDb.countBookedBetween(c.getClinicId(), s.getServiceId(), startDate, endDate);
                    int noShow = apptDb.countNoShowBetween(c.getClinicId(), s.getServiceId(), startDate, endDate);

                    boolean meaningful = (available > 0) || (booked > 0) || (noShow > 0);
                    if (!meaningful) {
                        continue;
                    }

                    double util = 0.0;
                    if (available > 0) util = (double) booked / (double) available;

                    totalAvailable += available;

                    Map<String, Object> row = new HashMap<>();
                    row.put("clinicName", c.getName());
                    row.put("serviceName", s.getName());
                    row.put("available", available);
                    row.put("booked", booked);
                    row.put("noShow", noShow);
                    row.put("utilization", util);
                    detailRows.add(row);
                }
            }
        }

        int bookedCount = apptDb.countBookedBetween(clinicId, serviceId, startDate, endDate);
        int noShowCount = apptDb.countNoShowBetween(clinicId, serviceId, startDate, endDate);

        double utilization = 0.0;
        if (totalAvailable > 0) utilization = (double) bookedCount / (double) totalAvailable;

        String clinicLabel;
        if (clinicId == null) clinicLabel = "ALL Clinics";
        else {
            ClinicBean cb = clinicDb.getClinicByID(clinicId);
            clinicLabel = (cb != null) ? cb.getName() : ("Clinic #" + clinicId);
        }

        String serviceLabel;
        if (serviceId == null) serviceLabel = "ALL Services";
        else {
            ServiceBean sb = serviceDb.getServiceById(serviceId);
            serviceLabel = (sb != null) ? sb.getName() : ("Service #" + serviceId);
        }

        request.setAttribute("resultClinicLabel", clinicLabel);
        request.setAttribute("resultServiceLabel", serviceLabel);
        request.setAttribute("resultMonth", ym.toString());

        request.setAttribute("detailRows", detailRows);

        request.setAttribute("totalAvailable", totalAvailable);
        request.setAttribute("bookedCount", bookedCount);
        request.setAttribute("noShowCount", noShowCount);
        request.setAttribute("utilization", utilization);

        request.getRequestDispatcher("/admin/adminAnalytics.jsp").forward(request, response);
    }
}