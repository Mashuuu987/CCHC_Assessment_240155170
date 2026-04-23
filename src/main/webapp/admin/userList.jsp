<%-- 
    Document   : userList
    Created on : 2026/04/23, 23:14:14
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>User List</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/userList.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();

            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");

            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<Map<String, Object>> viewRows = (List<Map<String, Object>>) request.getAttribute("viewRows");
            Integer colCount = (Integer) request.getAttribute("colCount");
            if (colCount == null) {
                colCount = 9;
            }

            Integer fUserId = (Integer) request.getAttribute("filterUserId");
            String fUsername = (String) request.getAttribute("filterUsername");
            String fRole = (String) request.getAttribute("filterRole");
            Integer fStaffId = (Integer) request.getAttribute("filterStaffId");
            Integer fPatientId = (Integer) request.getAttribute("filterPatientId");
            Integer fClinicId = (Integer) request.getAttribute("filterClinicId");
            String fName = (String) request.getAttribute("filterName");

            if (fUsername == null) {
                fUsername = "";
            }
            if (fRole == null) {
                fRole = "ALL";
            }
            if (fName == null)
                fName = "";
        %>

        <div class="inc-wrap">
            <div class="inc-hero">
                <div>
                    <h1 class="inc-title">User List</h1>
                </div>
            </div>

            <div class="records-search-row">
                <form method="get" action="<%= ctx%>/AdminUserList" class="records-search-form">
                    <input class="records-filter-input" type="number" name="userId" placeholder="User ID"
                           value="<%= fUserId != null ? fUserId : ""%>">

                    <input class="records-filter-input" type="text" name="username" placeholder="Username"
                           value="<%= fUsername%>">

                    <input class="records-filter-input" type="number" name="staffId" placeholder="Staff ID"
                           value="<%= fStaffId != null ? fStaffId : ""%>">

                    <input class="records-filter-input" type="number" name="patientId" placeholder="Patient ID"
                           value="<%= fPatientId != null ? fPatientId : ""%>">

                    <div>
                        <input class="records-filter-input" type="text" name="name" placeholder="Name (First/Last)"
                               value="<%= fName%>">

                        <select class="records-filter-select" name="role">
                            <option value="ALL" <%= "ALL".equalsIgnoreCase(fRole) ? "selected" : ""%>>All Roles</option>
                            <option value="PATIENT" <%= "PATIENT".equalsIgnoreCase(fRole) ? "selected" : ""%>>PATIENT</option>
                            <option value="STAFF" <%= "STAFF".equalsIgnoreCase(fRole) ? "selected" : ""%>>STAFF</option>
                            <option value="ADMIN" <%= "ADMIN".equalsIgnoreCase(fRole) ? "selected" : ""%>>ADMIN</option>
                        </select>

                        <select class="records-filter-select" name="clinicId">
                            <option value="">All Clinics</option>
                            <% if (clinics != null) {
                                    for (ClinicBean c : clinics) {
                                        boolean sel = (fClinicId != null && fClinicId == c.getClinicId());
                            %>
                            <option value="<%= c.getClinicId()%>" <%= sel ? "selected" : ""%>><%= c.getName()%></option>
                            <%     }
                                }%>
                        </select>

                        <button type="submit" class="records-filter-reset">Search</button>
                        <a class="records-filter-clear" href="<%= ctx%>/AdminUserList">Clear</a>
                    </div>
                </form>
            </div>

            <table class="records-table">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Clinic</th>
                        <th>Staff ID</th>
                        <th>Patient ID</th>
                        <th>Full Name</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <% if (viewRows == null || viewRows.isEmpty()) {%>
                    <tr>
                        <td colspan="<%= colCount%>" class="empty">No users found.</td>
                    </tr>
                    <% } else {
                        for (Map<String, Object> r : viewRows) {
                    %>
                    <tr>
                        <td><%= r.get("userId")%></td>
                        <td><%= r.get("username")%></td>
                        <td><%= r.get("role")%></td>
                        <td><%= r.get("clinicName")%></td>
                        <td><%= r.get("staffId")%></td>
                        <td><%= r.get("patientId")%></td>
                        <td><%= r.get("fullName")%></td>
                        <td>
                            <div class="action-cell">
                                <a class="btn-details" href="<%= ctx%>/AdminUserDetail?userId=<%= r.get("userId")%>">Detail</a>
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
