<%-- 
    Document   : Announcements
    Created on : 2026/04/16, 3:37:18
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="ict.bean.AnnouncementsBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Announcements</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/announcement.css">
    </head>
    <body>
        <%
             List<AnnouncementsBean> list = (List<AnnouncementsBean>) request.getAttribute("announcements");
        %>
        <%@ include file="/heading.jsp" %>
        <div class="main-container">
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
        </div>
    </body>
</html>
