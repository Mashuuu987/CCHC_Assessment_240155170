package ict.util;

import ict.bean.UserInfoBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author amzte
 */
public class UserCheckUtil {

    public UserCheckUtil() {
    }

    public static UserInfoBean getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserInfoBean) session.getAttribute("userInfo");
    }

    public static boolean hasRole(UserInfoBean user, String role) {
        if (user == null || user.getRole() == null || role == null) {
            return false;
        }
        return user.getRole().equalsIgnoreCase(role);
    }

    public static UserInfoBean requireRole(HttpServletRequest request, HttpServletResponse response, String role) 
            throws IOException {
        UserInfoBean user = getLoginUser(request);
        if (!hasRole(user, role)) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return null;
        }
        return user;
    }
}
