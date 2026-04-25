<%-- 
    Document   : adminPolicySettings
    Created on : 2026/04/25, 17:20:40
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Policy Settings</title>
    <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminPolicySettings.css">
</head>
<body>
<%@ include file="/heading.jsp" %>

<%
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    Integer maxActive = (Integer) request.getAttribute("maxActiveAppointments");
    if (maxActive == null) maxActive = 3;
%>

<div class="ps-wrap">
    <div class="ps-head">
        <div>
            <h1 class="ps-title">Policy Settings</h1>
        </div>
        <div class="ps-actions">
            <a class="btn-action btn-back" href="<%= ctx %>/AdminDashboard">Back</a>
        </div>
    </div>

    <% if (error != null) { %>
        <div class="ps-alert ps-alert-error"><%= error %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="ps-alert ps-alert-success"><%= success %></div>
    <% } %>

    <div class="ps-card">
        <h3 class="ps-card-title">Max Active Appointments Per Patient</h3>

        <form method="post" action="<%= ctx %>/AdminPolicySettings"
              onsubmit="return confirm('Save policy changes?');">
            <div class="ps-row">
                <div class="ps-field">
                    <label class="ps-label">Max Active Appointments (1-20)</label>
                    <input class="ps-input" type="number" name="maxActiveAppointments"
                           min="1" max="20" value="<%= maxActive %>" required>
                    <div class="ps-hint">
                        Active appointments = REQUESTED / CONFIRMED
                    </div>
                </div>

                <div class="ps-field ps-field-tight">
                    <label class="ps-label">&nbsp;</label>
                    <button type="submit" class="ps-btn-primary">Save</button>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
