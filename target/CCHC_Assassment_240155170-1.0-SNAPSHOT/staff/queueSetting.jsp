<%-- 
    Document   : queueSetting
    Created on : 2026/04/21
--%>
<%@page import="java.util.List"%>
<%@page import="ict.bean.QueueSettingBean"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Queue Settings</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/queue.css">
    </head>
    <body>
        <%
            String ctx = request.getContextPath();
            List<QueueSettingBean> queueSettings = (List<QueueSettingBean>) request.getAttribute("queueSettings");
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
        %>
        <%@ include file="/heading.jsp" %>

        <div class="main-container queue-page">
            <div class="queue-hero">
                <div>
                    <p class="queue-kicker">Queue Administration</p>
                    <h1 class="page-title">Enable or disable queue ticket issuing</h1>
                </div>
            </div>

            <% if (error != null) { %>
            <div class="queue-alert queue-alert-error"><%= error %></div>
            <% } %>
            <% if (success != null) { %>
            <div class="queue-alert queue-alert-success"><%= success %></div>
            <% } %>

            <div class="queue-panel">
                <h2 class="section-title">Queue settings</h2>
                <div class="queue-table-wrap">
                    <table class="queue-table">
                        <thead>
                            <tr>
                                <th>Clinic</th>
                                <th>Service</th>
                                <th>Hours</th>
                                <th>Closed On</th>
                                <th>Duration</th>
                                <th>Accept Queue</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (queueSettings == null || queueSettings.isEmpty()) { %>
                            <tr><td colspan="7" class="queue-empty-row">No queue setting found.</td></tr>
                            <% } else {
                                   for (QueueSettingBean setting : queueSettings) {
                                       boolean allow = setting.isEnabled() && setting.isAllowIssueTicket();
                            %>
                            <tr>
                                <td><%= setting.getClinicName() %></td>
                                <td><%= setting.getServiceName() %> (<%= setting.getServiceType() %>)</td>
                                <td><%= setting.getClinicOpenTime() %> - <%= setting.getClinicCloseTime() %></td>
                                <td><%= setting.getClinicCloseDay() %></td>
                                <td><%= setting.getDurationMins() %> mins</td>
                                <td><span class="queue-status <%= allow ? "status-waiting" : "status-skipped" %>"><%= allow ? "TRUE" : "FALSE" %></span></td>
                                <td>
                                    <form method="post" action="<%= ctx %>/QueueSetting" class="queue-inline-form">
                                        <input type="hidden" name="action" value="toggleAllow" />
                                        <input type="hidden" name="clinicId" value="<%= setting.getClinicId() %>" />
                                        <input type="hidden" name="serviceId" value="<%= setting.getServiceId() %>" />
                                        <input type="hidden" name="allowIssueTicket" value="<%= !allow %>" />
                                        <button type="submit" class="queue-btn <%= allow ? "queue-btn-danger" : "queue-btn-primary" %>">
                                            <%= allow ? "Disable" : "Enable" %>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                            <%     }
                               } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
