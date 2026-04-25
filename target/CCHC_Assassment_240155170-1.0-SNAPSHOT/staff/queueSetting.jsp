<%-- 
    Document   : queueSetting
    Created on : 2026/04/21
--%>
<%@page import="java.util.List"%>
<%@page import="ict.bean.QueueSettingBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Queue Settings</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/queue.css">
    </head>
    <body>
        <%
            String ctx = request.getContextPath();
            List<QueueSettingBean> queueSettings = (List<QueueSettingBean>) request.getAttribute("queueSettings");
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            Integer fClinicId = (Integer) request.getAttribute("fClinicId");
            String fClinic = (String) request.getAttribute("fClinic");
            String fService = (String) request.getAttribute("fService");
            String fStatus = (String) request.getAttribute("fStatus");
            if (fClinic == null) {
                fClinic = "";
            }
            if (fService == null) {
                fService = "";
            }
            if (fStatus == null || fStatus.isBlank())
                fStatus = "ALL";
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container queue-page">
            <% if (error != null) {%>
            <div class="queue-alert queue-alert-error"><%= error%></div>
            <% } %>
            <% if (success != null) {%>
            <div class="queue-alert queue-alert-success"><%= success%></div>
            <% }%>

            <div class="queue-panel">
                <h2 class="section-title">Queue settings</h2>
                <form class="queue-filter-bar" method="get" action="<%= ctx%>/QueueSetting">
                    <div class="queue-field field-small">
                        <label class="queue-label">Clinic ID</label>
                        <input class="queue-input" type="number" name="fClinicId"
                               value="<%= (fClinicId != null) ? fClinicId : ""%>"
                               placeholder="e.g. 1">
                    </div>

                    <div class="queue-field">
                        <label class="queue-label">Clinic keyword</label>
                        <input class="queue-input" type="text" name="fClinic"
                               value="<%= fClinic%>"
                               placeholder="e.g. Sha Tin">
                    </div>

                    <div class="queue-field">
                        <label class="queue-label">Service keyword</label>
                        <input class="queue-input" type="text" name="fService"
                               value="<%= fService%>"
                               placeholder="e.g. Vaccination">
                    </div>

                    <div class="queue-field field-status">
                        <label class="queue-label">Status</label>
                        <select class="queue-input" name="fStatus">
                            <option value="ALL" <%= "ALL".equalsIgnoreCase(fStatus) ? "selected" : ""%>>ALL</option>
                            <option value="OPEN" <%= "OPEN".equalsIgnoreCase(fStatus) ? "selected" : ""%>>OPEN</option>
                            <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(fStatus) ? "selected" : ""%>>CLOSED</option>
                        </select>
                    </div>

                    <div class="queue-actions">
                        <button type="submit" class="queue-btn queue-btn-primary">Search</button>
                        <button type="button" class="queue-btn"
                                onclick="window.location = '<%= ctx%>/QueueSetting';">Clear</button>
                    </div>
                </form>

                <div class="queue-table-wrap" style="margin-top:12px;">
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
                                    boolean accept = setting.isEnabled() && setting.isAllowIssueTicket();
                            %>
                            <tr>
                                <td><%= setting.getClinicName()%></td>
                                <td><%= setting.getServiceName()%> (<%= setting.getServiceType()%>)</td>
                                <td><%= setting.getClinicOpenTime()%> - <%= setting.getClinicCloseTime()%></td>
                                <td><%= setting.getClinicCloseDay()%></td>
                                <td><%= setting.getDurationMins()%> mins</td>
                                <td>
                                    <span class="queue-status <%= accept ? "status-waiting" : "status-skipped"%>">
                                        <%= accept ? "TRUE" : "FALSE"%>
                                    </span>
                                </td>
                                <td>
                                    <form method="post" action="<%= ctx%>/QueueSetting" class="queue-inline-form">
                                        <input type="hidden" name="action" value="toggleAllow" />
                                        <input type="hidden" name="clinicId" value="<%= setting.getClinicId()%>" />
                                        <input type="hidden" name="serviceId" value="<%= setting.getServiceId()%>" />

                                        <input type="hidden" name="fClinicId" value="<%= (fClinicId != null) ? fClinicId : ""%>">
                                        <input type="hidden" name="fClinic" value="<%= fClinic%>">
                                        <input type="hidden" name="fService" value="<%= fService%>">
                                        <input type="hidden" name="fStatus" value="<%= fStatus%>">

                                        <button type="submit" class="queue-btn <%= accept ? "queue-btn-danger" : "queue-btn-primary"%>">
                                            <%= accept ? "Disable" : "Enable"%>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                            <%     }
                    }%>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </body>
</html>

