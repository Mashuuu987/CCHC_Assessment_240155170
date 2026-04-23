<%-- 
    Document   : incident
    Created on : 2026/04/22, 18:34:21
    Author     : amzte
--%>



<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="ict.bean.IncidentLogBean"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page import="ict.bean.StaffProfileBean"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Incident Log</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/incident.css">
    </head>

    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();

            Boolean isStaff = (Boolean) request.getAttribute("isStaff");
            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");

            List<Map<String, Object>> viewRows = (List<Map<String, Object>>) request.getAttribute("viewRows");
            Boolean showClinicCol = (Boolean) request.getAttribute("showClinicCol");
            Integer colCount = (Integer) request.getAttribute("colCount");
            if (colCount == null) {
                colCount = 10;
            }

            Integer filterClinicId = (Integer) request.getAttribute("filterClinicId");
            String filterStaffName = (String) request.getAttribute("filterStaffName");
            Integer filterServiceId = (Integer) request.getAttribute("filterServiceId");

            Integer filterOccurredYear = (Integer) request.getAttribute("filterOccurredYear");
            Integer filterOccurredMonth = (Integer) request.getAttribute("filterOccurredMonth");
            Integer filterCreatedYear = (Integer) request.getAttribute("filterCreatedYear");
            Integer filterCreatedMonth = (Integer) request.getAttribute("filterCreatedMonth");

            String filterStatus = (String) request.getAttribute("filterStatus");
            String filterSeverity = (String) request.getAttribute("filterSeverity");
            String filterKeyword = (String) request.getAttribute("filterKeyword");

            if (filterStaffName == null) {
                filterStaffName = "";
            }
            if (filterStatus == null) {
                filterStatus = "ALL";
            }
            if (filterSeverity == null) {
                filterSeverity = "ALL";
            }
            if (filterKeyword == null)
                filterKeyword = "";
        %>

        <div class="inc-wrap">
            <div class="inc-hero">
                <div>
                    <h1 class="inc-title">Incident Log</h1>
                </div>

                <div>
                    <% if (isStaff != null && isStaff) {%>
                    <a class="create-incident-btn" href="<%= ctx%>/CreateIncident">Create Incident</a>
                    <% } %>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% } %>
            <% if (success != null) {%>
            <div class="inc-alert inc-alert-success"><%= success%></div>
            <% }%>

            <div class="records-search-row">
                <form method="get" action="<%= ctx%>/Incident" class="records-search-form">
                    <% if (isAdmin != null && isAdmin) { %>
                    <select class="records-filter-select" name="clinicId">
                        <option value="">All clinics</option>
                        <% if (clinics != null) {
                                for (ClinicBean c : clinics) {
                                    boolean sel = (filterClinicId != null && filterClinicId == c.getClinicId());
                        %>
                        <option value="<%= c.getClinicId()%>" <%= sel ? "selected" : ""%>><%= c.getName()%></option>
                        <%     }
                            }%>
                    </select>

                    <input class="records-filter-input" type="text" name="staffName"
                           placeholder="Search staff name..." value="<%= filterStaffName%>">
                    <% }%>


                    <select class="records-filter-select" name="serviceId">
                        <option value="">All services</option>

                        <option value="CLINIC" <%= "CLINIC".equals(request.getAttribute("filterServiceToken")) ? "selected" : ""%>>
                            Clinic incident
                        </option>

                        <% if (services != null) {
                                for (ServiceBean s : services) {
                                    boolean sel = (filterServiceId != null && filterServiceId == s.getServiceId());
                        %>
                        <option value="<%= s.getServiceId()%>" <%= sel ? "selected" : ""%>><%= s.getName()%></option>
                        <%  }
                            }%>
                    </select>


                    <input class="records-filter-input" type="number" name="occurredYear" placeholder="Occurred Year"
                           value="<%= filterOccurredYear != null ? filterOccurredYear : ""%>">
                    <input class="records-filter-input" type="number" name="occurredMonth" placeholder="Occurred Month"
                           value="<%= filterOccurredMonth != null ? filterOccurredMonth : ""%>">

                    <input class="records-filter-input" type="number" name="createdYear" placeholder="Created Year"
                           value="<%= filterCreatedYear != null ? filterCreatedYear : ""%>">
                    <input class="records-filter-input" type="number" name="createdMonth" placeholder="Created Month"
                           value="<%= filterCreatedMonth != null ? filterCreatedMonth : ""%>">

                    <select class="records-filter-select" name="status">
                        <option value="ALL" <%= "ALL".equalsIgnoreCase(filterStatus) ? "selected" : ""%>>All Status</option>
                        <option value="OPEN" <%= "OPEN".equalsIgnoreCase(filterStatus) ? "selected" : ""%>>OPEN</option>
                        <option value="CLOSED" <%= "CLOSED".equalsIgnoreCase(filterStatus) ? "selected" : ""%>>CLOSED</option>
                    </select>

                    <select class="records-filter-select" name="severity">
                        <option value="ALL" <%= "ALL".equalsIgnoreCase(filterSeverity) ? "selected" : ""%>>All Severity</option>
                        <option value="LOW" <%= "LOW".equalsIgnoreCase(filterSeverity) ? "selected" : ""%>>LOW</option>
                        <option value="MEDIUM" <%= "MEDIUM".equalsIgnoreCase(filterSeverity) ? "selected" : ""%>>MEDIUM</option>
                        <option value="HIGH" <%= "HIGH".equalsIgnoreCase(filterSeverity) ? "selected" : ""%>>HIGH</option>
                        <option value="CRITICAL" <%= "CRITICAL".equalsIgnoreCase(filterSeverity) ? "selected" : ""%>>CRITICAL</option>
                    </select>

                    <input class="records-filter-input" type="text" name="keyword" placeholder="Search keyword..."
                           value="<%= filterKeyword%>">

                    <button type="submit" class="records-filter-reset">Search</button>
                    <a class="records-filter-clear" href="<%= ctx%>/Incident">Clear</a>
                </form>
            </div>

            <table class="records-table">
                <thead>
                    <tr>
                        <% if (showClinicCol != null && showClinicCol) { %>
                        <th>Clinic</th>
                            <% } %>
                        <th>Incident ID</th>
                        <th>Staff ID</th>
                        <th>Staff Name</th>
                        <th>Service</th>
                        <th>Title</th>
                        <th>Occurred</th>
                        <th>Created</th>
                        <th>Severity</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <% if (viewRows == null || viewRows.isEmpty()) {%>
                    <tr>
                        <td colspan="<%= colCount%>" class="empty">No incidents found.</td>
                    </tr>
                    <% } else {
                        for (Map<String, Object> r : viewRows) {
                            String sev = String.valueOf(r.get("severity"));
                            String st = String.valueOf(r.get("status"));
                    %>
                    <tr>
                        <% if (showClinicCol != null && showClinicCol) {%>
                        <td><%= r.get("clinicName")%></td>
                        <% }%>

                        <td><%= r.get("incidentId")%></td>
                        <td><%= r.get("staffId")%></td>
                        <td><%= r.get("staffName")%></td>

                        <td><%= r.get("serviceName")%></td>
                        <td><%= r.get("title")%></td>
                        <td><%= r.get("occurred")%></td>
                        <td><%= r.get("createdAt")%></td>

                        <td><span class="inc-pill sev-<%= sev.toLowerCase()%>"><%= sev%></span></td>
                        <td><span class="inc-pill st-<%= st.toLowerCase()%>"><%= st%></span></td>

                        <td>
                            <div class="action-cell">
                                <a class="btn-details"
                                   href="<%= ctx%>/IncidentDetail?incidentId=<%= r.get("incidentId")%>">Detail</a>

                                <% if (isAdmin != null && isAdmin && "OPEN".equalsIgnoreCase(st)) {%>
                                <form method="post" action="<%= ctx%>/Incident" style="display:inline;"
                                      onsubmit="return confirm('Close this incident?');">
                                    <input type="hidden" name="action" value="close">
                                    <input type="hidden" name="incidentId" value="<%= r.get("incidentId")%>">
                                    <button class="btn-danger" type="submit">Close</button>
                                </form>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                    <%     }
                        }%>
                </tbody>
            </table>
        </div>

    </body>
</html>