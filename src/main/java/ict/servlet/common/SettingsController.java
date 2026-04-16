/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;

import ict.bean.UserInfoBean;
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
@WebServlet(name = "SettingsController", urlPatterns = {"/Settings"})
public class SettingsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        if (UserCheckUtil.hasRole(user, "PATIENT")) {
            // 
        } else if (UserCheckUtil.hasRole(user, "STAFF")) {
            //
        } else if (UserCheckUtil.hasRole(user, "ADMIN")) {
            //
        }

        request.getRequestDispatcher("/common/settings.jsp").forward(request, response);
    }
}
