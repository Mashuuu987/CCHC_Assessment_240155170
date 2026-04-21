package ict.servlet.staff;

import java.io.IOException;
import java.util.List;

import ict.bean.QueueSettingBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.QueueSettingDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "QueueSettingController", urlPatterns = {"/QueueSetting"})
public class QueueSettingController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private QueueSettingDB queueSettingDb;
    private StaffDB staffDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        queueSettingDb = new QueueSettingDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
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
        if (!UserCheckUtil.hasRole(user, "STAFF") && !UserCheckUtil.hasRole(user, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        boolean isAdmin = UserCheckUtil.hasRole(user, "ADMIN");
        Integer staffClinicId = null;
        List<QueueSettingBean> settings;

        if (isAdmin) {
            settings = queueSettingDb.getAllQueueSettings();
        } else {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                settings = java.util.Collections.emptyList();
            } else {
                staffClinicId = staff.getClinicId();
                settings = queueSettingDb.getQueueSettingsByClinicId(staffClinicId);
            }
        }

        request.setAttribute("queueSettings", settings);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("staffClinicId", staffClinicId);
        request.setAttribute("clinics", clinicDb.getAllClinics());
        request.setAttribute("services", serviceDb.getAllServices());
        request.getRequestDispatcher("/staff/queueSetting.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        if (!UserCheckUtil.hasRole(user, "STAFF") && !UserCheckUtil.hasRole(user, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        boolean isAdmin = UserCheckUtil.hasRole(user, "ADMIN");
        Integer staffClinicId = null;
        if (!isAdmin) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                doGet(request, response);
                return;
            }
            staffClinicId = staff.getClinicId();
        }

        String action = request.getParameter("action");
        if ("toggleAllow".equals(action)) {
            try {
                int clinicId = Integer.parseInt(request.getParameter("clinicId"));
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));

                if (!isAdmin && (staffClinicId == null || staffClinicId != clinicId)) {
                    request.setAttribute("error", "You can only modify queue settings for your own clinic.");
                    doGet(request, response);
                    return;
                }

                boolean allow = Boolean.parseBoolean(request.getParameter("allowIssueTicket"));
                boolean ok = queueSettingDb.updateAllowIssueTicket(clinicId, serviceId, allow);
                request.setAttribute(ok ? "success" : "error", ok ? "Queue permission updated." : "Queue permission update failed.");
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid queue setting input.");
            }
        }
        doGet(request, response);
    }
}
