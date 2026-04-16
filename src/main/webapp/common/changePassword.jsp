<%-- 
    Document   : changePassword
    Created on : 2026/04/13, 14:16:11
    Author     : amzte
--%>
<%@page import="ict.bean.UserInfoBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Change Password</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/changePassword.css" />
    </head>
    <body>
        <%
            UserInfoBean user = (UserInfoBean) session.getAttribute("userInfo");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }
            String ctx = request.getContextPath();
            String message = (String) request.getAttribute("message");
            String messageType = (String) request.getAttribute("messageType");
        %>

        <%@ include file="/heading.jsp" %>
        <div class="main-container">
            <h1 class="page-title">Change Password</h1>

            <% if (message != null) {
                String messageClass = "changePassword-message-success";
                if ("error".equals(messageType)) {
                    messageClass = "changePassword-message-error";
                }
            %>
            <div class="changePassword-message <%= messageClass %>">
                <%= message%>
            </div>
            <% }%>

            <form method="post" action="<%= ctx%>/ChangePassword" class="profile-form">
                <div class="changePassword-row">
                    <label class="changePassword-label">Current password</label>
                    <input class="changePassword-input" type="password" name="currentPassword" required />
                </div>

                <div class="changePassword-row">
                    <label class="changePassword-label">New password</label>
                    <input class="changePassword-input" type="password" name="newPassword" required />
                </div>

                <div class="changePassword-row">
                    <label class="changePassword-label">Confirm new password</label>
                    <input class="changePassword-input" type="password" name="confirmPassword" required />
                </div>

                <div class="changePassword-actions">
                    <button type="submit" class="btn-primary">Save</button>
                    <button type="button" class="btn-back"
                            onclick="window.location.href = '<%= ctx%>/Settings';">
                        Back to settings
                    </button>
                </div>
            </form>
        </div>
    </body>
</html>
