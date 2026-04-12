<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="ict.bean.NotificationBean" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Notification Center</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/notification.css" />
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            List<NotificationBean> notifications = (List<NotificationBean>) request.getAttribute("notifications");
            NotificationBean selectedNotification = (NotificationBean) request.getAttribute("selectedNotification");
            Integer selectedId = (Integer) request.getAttribute("selectedId");
        %>

        <div class="notif-container">
            <div class="notif-list-panel">
                <h2 class="notif-panel-title">Notification Center</h2>
                <p class="notif-panel-subtitle">Don't forget to view the important (yellow) and urgent (red) messages!</p>

                <%
                    if (notifications == null || notifications.isEmpty()) {
                %>
                    <p class="notif-empty">No message yet.</p>
                <%
                    } else {
                %>
                    <ul class="notif-list">
                        <%
                            for (NotificationBean n : notifications) {
                                String badgeClass = "notif-badge-normal";
                                String type = n.getType();
                                if ("IMPORTANT".equalsIgnoreCase(type)) {
                                    badgeClass = "notif-badge-important";
                                } else if ("URGENT".equalsIgnoreCase(type)) {
                                    badgeClass = "notif-badge-urgent";
                                }

                                boolean read = n.isRead();
                                boolean isActive = (selectedId != null && selectedId == n.getNotificationId());

                                String itemClasses = read ? "notif-item notif-item-read" : "notif-item notif-item-unread";
                                if (isActive) {
                                    itemClasses += " notif-item-active";
                                }
                        %>
                            <li class="<%= itemClasses %>">
                                <a class="notif-item-link" href="<%= request.getContextPath() %>/Notification?notificationId=<%= n.getNotificationId() %>">
                                    <span class="notif-badge <%= badgeClass %>"></span>
                                    <div class="notif-item-main">
                                        <div class="notif-item-title"><%= n.getTitle() %></div>
                                        <div class="notif-item-meta">
                                            <span class="notif-item-type"><%= n.getType() %></span>
                                            <span class="notif-item-time"><%= n.getCreatedAt() %></span>
                                        </div>
                                    </div>
                                </a>
                            </li>
                        <%
                            }
                        %>
                    </ul>
                <%
                    }
                %>
            </div>

            <div class="notif-detail-panel">
                <%
                    if (selectedNotification != null) {
                %>
                    <h2 class="notif-detail-title"><%= selectedNotification.getTitle() %></h2>
                    <div class="notif-detail-meta">
                        <span class="notif-detail-type">Message Type : <%= selectedNotification.getType() %></span>
                        <span class="notif-detail-time">Receive Time : <%= selectedNotification.getCreatedAt() %></span>
                    </div>
                    <div class="notif-detail-body">
                        <p><%= selectedNotification.getMessage() %></p>
                    </div>
                <%
                    } else {
                %>
                    <div class="notif-placeholder">
                        Please select a message first!
                    </div>
                <%
                    }
                %>
            </div>
        </div>
    </body>
</html>
