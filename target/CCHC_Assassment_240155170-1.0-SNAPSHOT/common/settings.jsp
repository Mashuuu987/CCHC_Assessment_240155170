<%-- 
    Document   : settings
    Created on : 2026/04/13, 13:37:55
    Author     : amzte
--%>
<%@page import="ict.bean.UserInfoBean" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Settings</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/settings.css">
    </head>
    <body>
        <%
            UserInfoBean user = (UserInfoBean) session.getAttribute("userInfo");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }
            String ctx = request.getContextPath();
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container">
            <h1 class="page-title">Settings</h1>
            <div class="feature-grid">
                <a class="feature-card" href="<%= ctx%>/Profile">
                    <h2 class="feature-card-title">Profile</h2>
                    <p class="feature-card-text">View and update your personal information.</p>
                </a>

                <a class="feature-card" href="<%= ctx%>/ChangePassword">
                    <h2 class="feature-card-title">Change password</h2>
                    <p class="feature-card-text">Update your account password securely.</p>
                </a>
            </div>
        </div>
    </body>
</html>
