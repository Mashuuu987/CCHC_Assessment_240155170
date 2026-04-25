<%-- 
    Document   : clinicList
    Created on : 2026/04/25, 15:51:03
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Clinic List</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/incident-userList-clinicList.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            List<Map<String, Object>> viewRows = (List<Map<String, Object>>) request.getAttribute("viewRows");
            Integer colCount = (Integer) request.getAttribute("colCount");
            if (colCount == null) {
                colCount = 8;
            }

            Integer fClinicId = (Integer) request.getAttribute("filterClinicId");
            String fName = (String) request.getAttribute("filterName");
            String fDistrict = (String) request.getAttribute("filterDistrict");
            String fCloseDay = (String) request.getAttribute("filterCloseDay");
            String fKeyword = (String) request.getAttribute("filterKeyword");

            if (fName == null) {
                fName = "";
            }
            if (fDistrict == null) {
                fDistrict = "";
            }
            if (fCloseDay == null) {
                fCloseDay = "";
            }
            if (fKeyword == null)
                fKeyword = "";
        %>

        <div class="inc-wrap">
            <div class="inc-hero">
                <div>
                    <h1 class="inc-title">Clinic List</h1>
                    <p class="inc-lead">View and edit clinics.</p>
                </div>

                <div class="inc-actions">
                    <a class="create-incident-btn" href="<%= ctx%>/AdminClinicCreate">Create Clinic</a>
                    <a class="create-incident-btn" href="<%= ctx%>/AdminServiceCreate">Create Service</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% } %>
            <% if (success != null) {%>
            <div class="inc-alert inc-alert-success"><%= success%></div>
            <% }%>

            <div class="records-search-row">
                <form method="get" action="<%= ctx%>/AdminClinicList" class="records-search-form">
                    <input class="records-filter-input" type="number" name="clinicId" placeholder="Clinic ID"
                           value="<%= fClinicId != null ? fClinicId : ""%>">

                    <input class="records-filter-input" type="text" name="name" placeholder="Name"
                           value="<%= fName%>">

                    <input class="records-filter-input" type="text" name="district" placeholder="District"
                           value="<%= fDistrict%>">

                    <input class="records-filter-input" type="text" name="closeDay" placeholder="Close Day (e.g. Sunday)"
                           value="<%= fCloseDay%>">

                    <input class="records-filter-input" type="text" name="keyword" placeholder="Keyword (name/district/address)"
                           value="<%= fKeyword%>">

                    <div class="filter-actions">
                        <button type="submit" class="records-filter-reset">Search</button>
                        <a class="records-filter-clear" href="<%= ctx%>/AdminClinicList">Clear</a>
                    </div>
                </form>
            </div>

            <table class="records-table">
                <thead>
                    <tr>
                        <th>Clinic ID</th>
                        <th>Name</th>
                        <th>District</th>
                        <th>Address</th>
                        <th>Open Time</th>
                        <th>Close Time</th>
                        <th>Close Day</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <% if (viewRows == null || viewRows.isEmpty()) {%>
                    <tr>
                        <td colspan="<%= colCount%>" class="empty">No clinics found.</td>
                    </tr>
                    <% } else {
                        for (Map<String, Object> r : viewRows) {%>
                    <tr>
                        <td><%= r.get("clinicId")%></td>
                        <td><%= r.get("name")%></td>
                        <td><%= r.get("district")%></td>
                        <td><%= r.get("address")%></td>
                        <td><%= r.get("openTime")%></td>
                        <td><%= r.get("closeTime")%></td>
                        <td><%= r.get("closeDay")%></td>
                        <td>
                            <div class="action-cell">
                                <a class="btn-details" href="<%= ctx%>/AdminClinicDetail?clinicId=<%= r.get("clinicId")%>">Detail</a>
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