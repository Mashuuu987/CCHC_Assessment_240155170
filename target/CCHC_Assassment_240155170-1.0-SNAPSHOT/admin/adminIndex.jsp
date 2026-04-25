<%-- 
    Document   : index
    Created on : 2026/04/07, 19:25:16
    Author     : amzte
--%>
<%@page import="ict.bean.AnnouncementsBean"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>CCHC</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/index.css">
    </head>
    <body>
        <%
            String ctx = request.getContextPath();
            Integer notifUnreadCount = (Integer) request.getAttribute("notifUnreadCount");
            String notifBadgeClass = (String) request.getAttribute("notifBadgeClass");
            List<AnnouncementsBean> list = (List<AnnouncementsBean>) request.getAttribute("announcements");
        %>
        <%@ include file="/heading.jsp" %>
        <div class="main-container">
            <div class="hero">
                <h1 class="hero-title">Community Clinic Health Center Admin Page</h1>
                <p class="hero-subtitle">You a super admin!</p>
            </div>

            <h2 class="page-title">Admin feature overview</h2>

            <div class="feature-grid">

                <a class="feature-card" href="<%= ctx + "/AppointmentRecordsStaffAdmin"%>">
                    <h2 class="feature-card-title">All Clinic Appointment Records</h2>
                </a>

                <a class="feature-card" href="<%= ctx + "/QueueSetting"%>">
                    <h2 class="feature-card-title">Queue Settings</h2>
                </a>

                <a class="feature-card" href="<%= ctx + "/Incident"%>">
                    <h2 class="feature-card-title">Incident Log</h2>
                </a>

                <a class="feature-card" href="<%= ctx + "/AdminUserList"%>">
                    <h2 class="feature-card-title">User List</h2>
                </a>

                <a class="feature-card" href="<%= ctx + "/AdminClinicList"%>">
                    <h2 class="feature-card-title">Clinic List</h2>
                </a>

                <a class="feature-card notification-card" href="<%= ctx + "/Notification"%>">
                    <% if (notifUnreadCount != null && notifUnreadCount > 0 && notifBadgeClass != null) {%>
                    <div class="notification-badge <%= notifBadgeClass%>"><%= notifUnreadCount%></div>
                    <% }%>
                    <h2 class="feature-card-title">Notification Center</h2>
                </a>

                <a class="feature-card" href="<%= ctx + "/Settings"%>">
                    <h2 class="feature-card-title">Settings</h2>
                </a>
            </div>

            <div class="section">
                <h2 class="section-title">Announcements</h2>

                <%
                    if (list == null || list.isEmpty()) {
                %>
                <p>No announcements yet.</p>
                <%
                } else {
                    for (AnnouncementsBean ann : list) {
                        String badgeClass = "notification-badge-normal";
                        if ("IMPORTANT".equalsIgnoreCase(ann.getType())) {
                            badgeClass = "notification-badge-important";
                        } else if ("URGENT".equalsIgnoreCase(ann.getType())) {
                            badgeClass = "notification-badge-urgent";
                        }
                        String timeText = ann.getPublishTime();
                        if (timeText == null || timeText.isEmpty() || "null".equalsIgnoreCase(timeText)) {
                            timeText = ann.getCreatedAt();
                        }
                %>
                <details class="announcement-item announcement-card">
                    <summary class="announcement-summary">
                        <div class="announcement-summary-main">
                            <strong class="announcement-title"><%= ann.getTitle()%></strong>
                            <span class="announcement-time"><%= timeText%></span>
                        </div>
                        <span class="announcement-type-dot <%= badgeClass%>" title="<%= ann.getType()%>"><%= ann.getType()%></span>
                    </summary>
                    <div class="announcement-content">
                        <p class="announcement-text"><%= ann.getContent()%></p>
                    </div>
                </details>
                <%
                        }
                    }
                %>

                <div class="announcements-more-wrap">
                    <a href="<%= ctx + "/Announcements"%>" class="announcements-more-btn">View more</a>
                </div>
            </div>

            <div class="footer">
                Community Clinic Health Center © 2026 · 240155170 Hui Ho Fung Matthew.
            </div>
        </div>
    </body>
</html>
