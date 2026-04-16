/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;

import ict.bean.UserInfoBean;
import ict.db.UserDB;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author amzte
 */
@WebServlet(name = "ChangePasswordController", urlPatterns = {"/ChangePassword"})
public class ChangePasswordController extends HttpServlet {

    private UserDB userDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        userDb = new UserDB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        request.getRequestDispatcher("/common/changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        String currentPwd = request.getParameter("currentPassword");
        String newPwd = request.getParameter("newPassword");
        String confirmPwd = request.getParameter("confirmPassword");

        String message;
        String messageType;

        if (currentPwd == null || newPwd == null || confirmPwd == null
                || currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {

            message = "All fields are required.";
            messageType = "error";

        } else if (!newPwd.equals(confirmPwd)) {

            message = "New password and confirm password do not match.";
            messageType = "error";

        } else if (newPwd.length() < 6) {

            message = "New password must be at least 6 characters.";
            messageType = "error";

        } else {
            boolean ok = userDb.changePassword(user.getUserId(), currentPwd, newPwd);
            if (ok) {
                message = "Password changed successfully.";
                messageType = "success";
            } else {
                message = "Current password is incorrect, or update failed.";
                messageType = "error";
            }
        }

        request.setAttribute("message", message);
        request.setAttribute("messageType", messageType);

        request.getRequestDispatcher("/common/changePassword.jsp")
                .forward(request, response);
    }
}
