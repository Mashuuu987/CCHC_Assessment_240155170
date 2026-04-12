/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet;

import java.io.IOException;
import java.util.List;

import ict.bean.NotificationBean;
import ict.bean.UserInfoBean;
import ict.db.NotificationDB;
import ict.db.UserDB;
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
@WebServlet(name = "NotificationController", urlPatterns = {"/Notification"})
public class NotificationController extends HttpServlet{
    
    private UserDB db;
    private NotificationDB notificationDb;
    
    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        db = new UserDB(dbUrl, dbUser, dbPassword);
        notificationDb = new NotificationDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null)
                ? (UserInfoBean) session.getAttribute("userInfo")
                : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String idParam = request.getParameter("notificationId");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int notificationId = Integer.parseInt(idParam);
                request.setAttribute("selectedId", notificationId);

                notificationDb.markAsRead(notificationId);

                List<NotificationBean> selectedList = notificationDb.getNotificationsByNotificationsId(notificationId);
                if (selectedList != null && !selectedList.isEmpty()) {
                    request.setAttribute("selectedNotification", selectedList.get(0));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        int userId = user.getUserId();
        List<NotificationBean> notifications = notificationDb.getNotificationsByUserId(userId);
        request.setAttribute("notifications", notifications);

        request.getRequestDispatcher("/common/notificationCenter.jsp")
               .forward(request, response);
    }
}
