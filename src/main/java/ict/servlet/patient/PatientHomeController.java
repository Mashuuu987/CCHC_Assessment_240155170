/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.patient;

import ict.bean.ClinicBean;
import ict.bean.NotificationBean;
import ict.bean.ServiceBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.ServiceDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author amzte
 */
@WebServlet(name = "PatientHomeController", urlPatterns = {"/PatientHome"})
public class PatientHomeController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private NotificationDB notifDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;

        if (user == null || user.getRole() == null || !"PATIENT".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        List<ClinicBean> clinics = clinicDb.getAllClinics();
        List<ServiceBean> services = serviceDb.getAllServices();

        request.setAttribute("clinics", clinics);
        request.setAttribute("services", services);

        int notifUnreadCount = 0;
        String notifBadgeClass = null;

        try {
            List<NotificationBean> notifList = notifDb.getNotificationsByUserId(user.getUserId());

            boolean hasUrgent = false;
            boolean hasImportant = false;

            for (NotificationBean n : notifList) {
                if (n != null && !n.isRead()) {
                    notifUnreadCount++;
                    String t = n.getType();
                    if (t != null) {
                        String upper = t.toUpperCase();
                        if ("URGENT".equals(upper)) {
                            hasUrgent = true;
                        } else if ("IMPORTANT".equals(upper)) {
                            hasImportant = true;
                        }
                    }
                }
            }

            if (notifUnreadCount > 0) {
                notifBadgeClass = "notification-badge-normal";
                if (hasImportant) {
                    notifBadgeClass = "notification-badge-important";
                }
                if (hasUrgent) {
                    notifBadgeClass = "notification-badge-urgent";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.setAttribute("notifUnreadCount", notifUnreadCount);
        request.setAttribute("notifBadgeClass", notifBadgeClass);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
