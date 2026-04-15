<%-- 
    Document   : index
    Created on : 2026/04/07, 19:25:16
    Author     : amzte
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>CCHC</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/index.css">
    </head>
    <body>
        <%
            String ctx = request.getContextPath();
            Integer notifUnreadCount = (Integer) request.getAttribute("notifUnreadCount");
            String notifBadgeClass = (String) request.getAttribute("notifBadgeClass");
        %>
        <%@ include file="/heading.jsp" %>
        <div class="main-container">
            <div class="hero">
                <h1 class="hero-title">Community Clinic Health Center</h1>
                <p class="hero-subtitle">A super online service platform for community clinics 
                    that helps you more easily book outpatient appointments, track waiting/queue status, 
                    and follow up on health records.</p>
                <div class="hero-badges">
                    <span class="hero-badge">Quick online appointments</span>
                    <span class="hero-badge">Real-time waiting queue</span>
                    <span class="hero-badge">Appointment and visit records</span>
                </div>
            </div>

            <h2 class="page-title">Patient feature overview</h2>
            <p class="page-subtitle">Please choose commonly used patient features. (Some function need to login first)</p>

            <div class="feature-grid">
                <a class="feature-card" href="<%= ctx + "/AdminHome"%>">
                    <h2 class="feature-card-title">Appointment Service</h2>
                    <p class="feature-card-text">Schedule appointments for outpatient visits or examinations, and manage future appointments.</p>
                </a>

                <a class="feature-card" href="<%= ctx + "/Login"%>">
                    <h2 class="feature-card-title">Waiting Queue</h2>
                    <p class="feature-card-text">View the current waiting list and your own queue number.</p>
                </a>

                <a class="feature-card" href="<%= ctx + "/Login"%>">
                    <h2 class="feature-card-title">Clinic and Service Information</h2>
                    <p class="feature-card-text">Browse the various community clinics, their opening hours, and the services they offer.</p>
                </a>

                <a class="feature-card notification-card" href="<%= ctx + "/Notification"%>">
                    <% if (notifUnreadCount != null && notifUnreadCount > 0 && notifBadgeClass != null) { %>
                    <div class="notification-badge <%= notifBadgeClass %>"><%= notifUnreadCount %></div>
                    <% }%>
                    <h2 class="feature-card-title">Notification Center</h2>
                    <p class="feature-card-text">View appointment reminders and general notifications sent by the system.</p>
                </a>

                <a class="feature-card" href="<%= ctx + "/Login"%>">
                    <h2 class="feature-card-title">Appointment and medical records</h2>
                    <p class="feature-card-text">Viewing past appointments and medical records makes it easier to follow up.</p>
                </a>

                <a class="feature-card" href="<%= ctx + "/Settings"%>">
                    <h2 class="feature-card-title">Settings</h2>
                    <p class="feature-card-text">Manage profile and change your password.</p>
                </a>
            </div>

            <div class="section">
                <h2 class="section-title">Announcements and Latest News</h2>
                <p class="section-subtitle">Click to view details.</p>
                <ul class="announcement-list">
                    <li class="announcement-item">
                        <div class="announcement-meta">2026-04-01 - System Announcement</div>
                        <p class="announcement-title">System Announcement!</p>
                        <p class="announcement-text">Hello ! System Announcement !</p>
                    </li>
                    <li class="announcement-item">
                        <div class="announcement-meta">2026-03-20 - Special Announcement</div>
                        <p class="announcement-title">Special Announcement !</p>
                        <p class="announcement-text">Hello ! Special Announcement !</p>
                    </li>
                    <li class="announcement-item">
                        <div class="announcement-meta">2026-03-05 - System Maintenance</div>
                        <p class="announcement-title">System Maintenance!</p>
                        <p class="announcement-text">Hello ! System Maintenance !</p>
                    </li>
                </ul>
            </div>

            <div class="footer">
                Community Clinic Health Center © 2026 · 240155170 Hui Ho Fung Matthew.
            </div>
        </div>
    </body>
</html>
