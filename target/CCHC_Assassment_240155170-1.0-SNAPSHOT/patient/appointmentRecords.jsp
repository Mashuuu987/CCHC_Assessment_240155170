<%-- 
    Document   : appointmentRecords
    Created on : 2026/04/16, 16:58:19
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="ict.bean.AppointmentBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Appointment Records</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/appointmentRecords.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>
        <%
            List<AppointmentBean> appointments = (List<AppointmentBean>) request.getAttribute("appointments");
            Map<Integer, String> clinicNameMap = (Map<Integer, String>) request.getAttribute("clinicNameMap");
            Map<Integer, String> serviceNameMap = (Map<Integer, String>) request.getAttribute("serviceNameMap");
            String error = (String) request.getAttribute("error");
        %>

        <div class="records-wrap">
            <h2 class="records-title">My Appointment Records</h2>

            <% if (error != null) {%>
            <p class="empty"><%= error%></p>
            <% } else if (appointments == null || appointments.isEmpty()) { %>
            <p class="empty">No appointment records yet.</p>
            <% } else { %>
            <table class="records-table">
                <tr>
                    <th>ID</th>
                    <th>Clinic</th>
                    <th>Service</th>
                    <th>Date</th>
                    <th>Timeslot</th>
                    <th>Status</th>
                    <th>Created At</th>
                </tr>

                <% for (AppointmentBean a : appointments) {
                        String status = a.getStatus();
                        String statusClass = "s-requested";
                        if ("CONFIRMED".equalsIgnoreCase(status)) {
                            statusClass = "s-confirmed";
                        } else if ("COMPLETED".equalsIgnoreCase(status)) {
                            statusClass = "s-completed";
                        } else if ("NO_SHOW".equalsIgnoreCase(status)) {
                            statusClass = "s-noshow";
                        } else if ("CANCELLED_BY_PATIENT".equalsIgnoreCase(status) || "CANCELLED_BY_CLINIC".equalsIgnoreCase(status)) {
                            statusClass = "s-cancelled";
                        }

                        String clinicName = clinicNameMap != null ? clinicNameMap.get(a.getClinicId()) : ("Clinic #" + a.getClinicId());
                        String serviceName = serviceNameMap != null ? serviceNameMap.get(a.getServiceId()) : ("Service #" + a.getServiceId());
                %>
                <tr>
                    <td><%= a.getAppointmentId()%></td>
                    <td><%= clinicName%></td>
                    <td><%= serviceName%></td>
                    <td><%= a.getAppointmentDate()%></td>
                    <td><%= a.getTimeSlot()%></td>
                    <td><span class="status-pill <%= statusClass%>"><%= status%></span></td>
                    <td><%= a.getCreatedAt()%></td>
                </tr>
                <% } %>
            </table>
            <% }%>
        </div>
    </body>
</html>
