<%-- 
    Document   : incidentDetail
    Created on : 2026/04/23, 2:52:09
    Author     : amzte
--%>


<%@page import="ict.bean.IncidentLogBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Incident Detail</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/incident.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/incidentDetail.css">
    </head>

    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
            IncidentLogBean incident = (IncidentLogBean) request.getAttribute("incident");

            String clinicName = (String) request.getAttribute("clinicName");
            String staffName = (String) request.getAttribute("staffName");
            String serviceName = (String) request.getAttribute("serviceName");

            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
            Boolean canClose = (Boolean) request.getAttribute("canClose");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
        %>

        <div class="incd-wrap">
            <div class="incd-header">
                <div>
                    <h1 class="incd-title">Incident Detail</h1>
                </div>

                <div class="incd-actions">
                    <% if (canClose != null && canClose) {%>
                    <form method="post" action="<%= ctx%>/IncidentDetail" style="display:inline;"
                          onsubmit="return confirm('Close this incident?');">
                        <input type="hidden" name="action" value="close">
                        <input type="hidden" name="incidentId" value="<%= incident != null ? incident.getIncidentId() : ""%>">
                        <button type="submit" class="btn-action btn-reschedule">Close</button>
                    </form>
                    <% } %>
                    <a class="btn-action btn-back" href="<%= ctx%>/Incident">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% } %>
            <% if (success != null) {%>
            <div class="inc-alert inc-alert-success"><%= success%></div>
            <% } %>

            <% if (incident == null && error == null) { %>
            <div class="empty">No incident data.</div>
            <% } else if (incident != null) {
                String sev = incident.getSeverity() == null ? "LOW" : incident.getSeverity();
                String st = incident.getStatus() == null ? "OPEN" : incident.getStatus();
            %>

            <div class="incd-card">
                <div class="incd-grid">
                    <div class="incd-label">Clinic</div>
                    <div class="incd-value"><%= clinicName%></div>

                    <div class="incd-label">Incident ID</div>
                    <div class="incd-value"><%= incident.getIncidentId()%></div>

                    <div class="incd-label">Staff ID</div>
                    <div class="incd-value"><%= incident.getStaffId()%></div>

                    <div class="incd-label">Staff Name</div>
                    <div class="incd-value"><%= staffName%></div>

                    <div class="incd-label">Service</div>
                    <div class="incd-value"><%= serviceName%></div>

                    <div class="incd-label">Title</div>
                    <div class="incd-value"><%= incident.getTitle()%></div>

                    <div class="incd-label">Occurred</div>
                    <div class="incd-value"><%= incident.getOccurred()%></div>

                    <div class="incd-label">Created</div>
                    <div class="incd-value"><%= incident.getCreatedAt()%></div>

                    <div class="incd-label">Severity</div>
                    <div class="incd-value">
                        <span class="inc-pill sev-<%= sev.toLowerCase()%>"><%= sev%></span>
                    </div>

                    <div class="incd-label">Status</div>
                    <div class="incd-value">
                        <span class="inc-pill st-<%= st.toLowerCase()%>"><%= st%></span>
                    </div>
                </div>

                <div class="incd-desc">
                    <div class="incd-desc-title">Description</div>
                    <div class="incd-desc-body"><%= incident.getDescription()%></div>
                </div>
            </div>

            <% }%>
        </div>

    </body>
</html>
