package ict.servlet.staff;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author amzte
 */

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
        queueSettingDb.createQueueSettingTable();
    }

    private String ns(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean containsIgnoreCase(String text, String kw) {
        if (kw == null || kw.isBlank()) return true;
        if (text == null) return false;
        return text.toLowerCase().contains(kw.trim().toLowerCase());
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String buildReturnQuery(HttpServletRequest request) {
        String fClinicId = ns(request.getParameter("fClinicId"));
        String fClinicKw = ns(request.getParameter("fClinic"));
        String fServiceKw = ns(request.getParameter("fService"));
        String fStatus = ns(request.getParameter("fStatus"));

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        if (!fClinicId.isBlank()) {
            sb.append(first ? "?" : "&").append("fClinicId=").append(URLEncoder.encode(fClinicId, StandardCharsets.UTF_8));
            first = false;
        }
        if (!fClinicKw.isBlank()) {
            sb.append(first ? "?" : "&").append("fClinic=").append(URLEncoder.encode(fClinicKw, StandardCharsets.UTF_8));
            first = false;
        }
        if (!fServiceKw.isBlank()) {
            sb.append(first ? "?" : "&").append("fService=").append(URLEncoder.encode(fServiceKw, StandardCharsets.UTF_8));
            first = false;
        }
        if (!fStatus.isBlank() && !"ALL".equalsIgnoreCase(fStatus)) {
            sb.append(first ? "?" : "&").append("fStatus=").append(URLEncoder.encode(fStatus, StandardCharsets.UTF_8));
        }
        return sb.toString();
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

        if (!isAdmin) {
            StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                request.setAttribute("queueSettings", java.util.Collections.emptyList());
                request.setAttribute("isAdmin", false);
                request.setAttribute("isStaff", true);
                request.setAttribute("staffClinicId", null);
                request.getRequestDispatcher("/staff/queueSetting.jsp").forward(request, response);
                return;
            }
            staffClinicId = staff.getClinicId();
        }

        Integer fClinicId = parseIntOrNull(request.getParameter("fClinicId"));
        String fClinic = ns(request.getParameter("fClinic"));
        String fService = ns(request.getParameter("fService"));
        String fStatus = ns(request.getParameter("fStatus"));
        if (fStatus.isBlank()) fStatus = "ALL";

        request.setAttribute("fClinicId", fClinicId);
        request.setAttribute("fClinic", fClinic);
        request.setAttribute("fService", fService);
        request.setAttribute("fStatus", fStatus);

        List<QueueSettingBean> settings = isAdmin ? queueSettingDb.getAllQueueSettings() : queueSettingDb.getQueueSettingsByClinicId(staffClinicId);

        List<QueueSettingBean> filtered = new ArrayList<>();
        if (settings != null) {
            for (QueueSettingBean s : settings) {

                if (!isAdmin && staffClinicId != null && s.getClinicId() != staffClinicId) continue;

                if (fClinicId != null && s.getClinicId() != fClinicId) continue;

                if (!containsIgnoreCase(s.getClinicName(), fClinic)) continue;

                String serviceCombined = ns(s.getServiceName()) + " " + ns(s.getServiceType());
                if (!containsIgnoreCase(serviceCombined, fService)) continue;

                boolean accept = s.isEnabled() && s.isAllowIssueTicket();
                if ("OPEN".equalsIgnoreCase(fStatus) && !accept) continue;
                if ("CLOSED".equalsIgnoreCase(fStatus) && accept) continue;

                filtered.add(s);
            }
        }

        request.setAttribute("queueSettings", filtered);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("isStaff", false);
        request.setAttribute("staffClinicId", staffClinicId);

        request.setAttribute("clinics", clinicDb.getAllClinics());
        request.setAttribute("services", serviceDb.getAllServices());

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object suc = session.getAttribute("success");
            Object err = session.getAttribute("error");
            if (suc != null) { request.setAttribute("success", suc.toString()); session.removeAttribute("success"); }
            if (err != null) { request.setAttribute("error", err.toString()); session.removeAttribute("error"); }
        }

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
                request.getSession().setAttribute("error", "No clinic is linked to this staff account.");
                response.sendRedirect(request.getContextPath() + "/QueueSetting");
                return;
            }
            staffClinicId = staff.getClinicId();
        }

        String action = request.getParameter("action");

        if ("toggleAllow".equalsIgnoreCase(action)) {
            Integer clinicId = parseIntOrNull(request.getParameter("clinicId"));
            Integer serviceId = parseIntOrNull(request.getParameter("serviceId"));

            if (clinicId == null || serviceId == null) {
                request.getSession().setAttribute("error", "Invalid queue setting input.");
                response.sendRedirect(request.getContextPath() + "/QueueSetting" + buildReturnQuery(request));
                return;
            }

            if (!isAdmin && (staffClinicId == null || staffClinicId.intValue() != clinicId.intValue())) {
                request.getSession().setAttribute("error", "You can only modify queue settings for your own clinic.");
                response.sendRedirect(request.getContextPath() + "/QueueSetting" + buildReturnQuery(request));
                return;
            }

            QueueSettingBean current = queueSettingDb.getQueueSetting(clinicId, serviceId);
            if (current == null) {
                request.getSession().setAttribute("error", "Queue setting record not found.");
                response.sendRedirect(request.getContextPath() + "/QueueSetting" + buildReturnQuery(request));
                return;
            }

            boolean newAllow = !(current.isAllowIssueTicket() && current.isEnabled());
            boolean ok1 = queueSettingDb.updateAllowIssueTicket(clinicId, serviceId, newAllow);
            boolean ok2 = queueSettingDb.updateEnabled(clinicId, serviceId, newAllow);

            boolean ok = ok1 && ok2;
            request.getSession().setAttribute(ok ? "success" : "error",
                    ok ? "Queue setting updated." : "Queue setting update failed.");
        }

        response.sendRedirect(request.getContextPath() + "/QueueSetting" + buildReturnQuery(request));
    }
}
