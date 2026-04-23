<%-- 
    Document   : createIncident
    Created on : 2026/04/23, 3:12:27
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page import="ict.bean.StaffProfileBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create Incident</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/incident.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/createIncident.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            StaffProfileBean staff = (StaffProfileBean) request.getAttribute("staff");

            String error = (String) request.getAttribute("error");
            Object sErr = request.getSession().getAttribute("error");
            if (sErr != null) {
                error = sErr.toString();
                request.getSession().removeAttribute("error");
            }
        %>

        <div class="cr-wrap">
            <div class="cr-head">
                <div>
                    <h1 class="cr-title">Create Incident</h1>
                </div>

                <div class="cr-actions">
                    <a class="btn-details" href="<%= ctx%>/Incident">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% }%>

            <div class="cr-card">
                <form method="post" action="<%= ctx%>/CreateIncident" class="cr-form"
                      onsubmit="return confirm('Submit this incident report?');">

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Clinic</label>
                            <div class="cr-readonly">
                                <% if (staff != null && staff.getClinicId() != null) {%>
                                Clinic ID: <%= staff.getClinicId()%>
                                <% } else { %>
                                -
                                <% } %>
                            </div>
                        </div>

                        <div class="cr-field">
                            <label class="cr-label">Service</label>
                            <select name="serviceId" class="records-filter-select" required>
                                <option value="CLINIC">Clinic incident</option>
                                <% if (services != null) {
                                for (ServiceBean s : services) {%>
                                <option value="<%= s.getServiceId()%>"><%= s.getName()%></option>
                                <%     }
                            }%>
                            </select>
                        </div>
                    </div>

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Severity</label>
                            <select name="severity" class="records-filter-select" required>
                                <option value="LOW">LOW</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HIGH">HIGH</option>
                                <option value="CRITICAL">CRITICAL</option>
                            </select>
                        </div>

                        <div class="cr-field">
                            <label class="cr-label">Occurred (date & time)</label>
                            <input type="datetime-local" name="occurred" class="records-filter-input" required>
                        </div>
                    </div>

                    <div class="cr-field">
                        <label class="cr-label">Title</label>
                        <input type="text" name="title" maxlength="50" class="records-filter-input" required>
                    </div>

                    <div class="cr-field">
                        <label class="cr-label">Description</label>
                        <textarea name="description" class="cr-textarea" rows="5" required></textarea>
                    </div>

                    <div class="cr-actions-bottom">
                        <button type="submit" class="records-filter-reset">Submit</button>
                        <a class="records-filter-clear" href="<%= ctx%>/Incident">Cancel</a>
                    </div>
                </form>
            </div>
        </div>

    </body>
</html>
