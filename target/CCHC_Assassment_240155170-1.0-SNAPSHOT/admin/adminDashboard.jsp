<%-- 
    Document   : adminDashboard
    Created on : 2026/04/25, 17:06:41
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Dashboard</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminDashboard.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
        %>

        <div class="dash-wrap">
            <div class="dash-head">
                <div>
                    <h1 class="dash-title">Dashboard</h1>
                </div>
                <div class="dash-actions">
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminHome">Back</a>
                </div>
            </div>

            <div class="dash-grid">
                <a class="dash-card" href="<%= ctx%>/AdminPolicySettings">
                    <div class="dash-card-title">Policy Settings</div>
                </a>

                <a class="dash-card" href="<%= ctx%>/AdminAnalytics">
                    <div class="dash-card-title">Analytics & Reports</div>
                </a>
            </div>
        </div>

    </body>
</html>
