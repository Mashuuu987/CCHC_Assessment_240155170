<%-- 
    Document   : appointmentAllRecords
    Created on : 2026/04/22, 2:23:24
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
        <title> Clinic Appointment Records</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/appointmentRecords.css">
        <script>
            function filterAppointments() {
                const clinicValue = document.getElementById("apptClinicFilter").value;
                const serviceValue = document.getElementById("apptServiceFilter").value;
                const idValue = document.getElementById("apptIdFilter").value.trim();
                const dateValue = document.getElementById("apptDateFilter").value.trim();
                const statusValue = document.getElementById("apptStatusFilter").value;

                const rows = document.querySelectorAll(".appointment-row");
                let visibleCount = 0;

                rows.forEach(function (row) {
                    const rowClinic = (row.getAttribute("data-clinic") || "");
                    const rowService = (row.getAttribute("data-service") || "");
                    const rowId = (row.getAttribute("data-id") || "");
                    const rowDate = (row.getAttribute("data-date") || "");
                    const rowStatus = (row.getAttribute("data-status") || "");

                    const idMatched = idValue === "" || rowId.indexOf(idValue) !== -1;
                    const dateMatched = dateValue === "" || rowDate === dateValue;
                    const statusMatched = statusValue === "ALL" || rowStatus === statusValue;
                    const clinicMatched = clinicValue === "ALL" || rowClinic === clinicValue;
                    const serviceMatched = serviceValue === "ALL" || rowService === serviceValue;

                    if (idMatched && dateMatched && statusMatched && clinicMatched && serviceMatched) {
                        row.style.display = "";
                        visibleCount++;
                    } else {
                        row.style.display = "none";
                    }

                });

                const emptyHint = document.getElementById("apptSearchEmpty");
                if (emptyHint) {
                    emptyHint.style.display = visibleCount === 0 ? "block" : "none";
                }
            }

            function resetAppointmentFilter() {
                document.getElementById("apptIdFilter").value = "";
                document.getElementById("apptDateFilter").value = "";
                document.getElementById("apptStatusFilter").value = "ALL";
                const clinicSel = document.getElementById("apptClinicFilter");
                if (clinicSel && !clinicSel.disabled) {
                    clinicSel.value = "ALL";
                }

                filterAppointments();
            }

            document.addEventListener("DOMContentLoaded", function () {
                const filters = document.querySelectorAll(".appt-filter");
                filters.forEach(function (filter) {
                    if (filter.id.includes("Filter")) {
                        filter.addEventListener(filter.tagName === "SELECT" ? "change" : "input", filterAppointments);
                    }
                });
            });
        </script>
    </head>
    <body>
        <%@ include file="/heading.jsp" %>
        <%
            List<AppointmentBean> appointments = (List<AppointmentBean>) request.getAttribute("appointments");
            Map<Integer, String> clinicNameMap = (Map<Integer, String>) request.getAttribute("clinicNameMap");
            Map<Integer, String> serviceNameMap = (Map<Integer, String>) request.getAttribute("serviceNameMap");
            String error = (String) request.getAttribute("error");
            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
            Boolean isStaff = (Boolean) request.getAttribute("isStaff");
            Integer staffClinicId = (Integer) request.getAttribute("staffClinicId");

        %>

        <div class="records-wrap">
            <h2 class="records-title">Clinic Appointment Records</h2>

            <div class="records-search-row">

                <select id="apptClinicFilter" class="appt-filter records-filter-select" <%= (isStaff != null && isStaff) ? "disabled" : ""%>>
                    <option value="ALL">All Clinics</option>
                    <% if (clinicNameMap != null) {
                            for (Map.Entry<Integer, String> e : clinicNameMap.entrySet()) {
                                boolean sel = (isStaff != null && isStaff && staffClinicId != null && staffClinicId.equals(e.getKey()));
                    %>
                    <option value="<%= e.getKey()%>" <%= sel ? "selected" : ""%>><%= e.getValue()%></option>
                    <%     }
                        } %>
                </select>
                <select id="apptServiceFilter" class="appt-filter records-filter-select">
                    <option value="ALL">All Services</option>
                    <% if (serviceNameMap != null) {
                            for (Map.Entry<Integer, String> e : serviceNameMap.entrySet()) {%>
                    <option value="<%= e.getKey()%>"><%= e.getValue()%></option>
                    <%   }
                        } %>
                </select>
                <input id="apptIdFilter" class="appt-filter records-filter-input" type="text" placeholder="Search ID..." />
                <input id="apptDateFilter" class="appt-filter records-filter-input" type="date" />
                <select id="apptStatusFilter" class="appt-filter records-filter-select">
                    <option value="ALL">All Status</option>
                    <option value="REQUESTED">Pending Request</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="NO_SHOW">No Show</option>
                    <option value="CANCELLED_BY_PATIENT">Cancelled by Patient</option>
                    <option value="CANCELLED_BY_CLINIC">Cancelled by Clinic</option>
                </select>
                <button type="button" class="records-filter-reset" onclick="resetAppointmentFilter()">Reset</button>
            </div>

            <% if (error != null) {%>
            <p class="empty"><%= error%></p>
            <% } else if (appointments == null || appointments.isEmpty()) { %>
            <p class="empty">No appointment records yet.</p>
            <% } else { %>
            <p id="apptSearchEmpty" class="empty" style="display: none;">No matching appointments found.</p>
            <table class="records-table">
                <tr>
                    <th>ID</th>
                    <th>Clinic</th>
                    <th>Service</th>
                    <th>Date</th>
                    <th>Timeslot</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Action</th>
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

                <tr class="appointment-row"
                    data-id="<%= a.getAppointmentId()%>"
                    data-date="<%= a.getAppointmentDate()%>"
                    data-status="<%= status%>"
                    data-clinic="<%= a.getClinicId()%>"
                    data-service="<%= a.getServiceId()%>">
                    <td><%= a.getAppointmentId()%></td>
                    <td><%= clinicName%></td>
                    <td><%= serviceName%></td>
                    <td><%= a.getAppointmentDate()%></td>
                    <td><%= a.getTimeSlot()%></td>
                    <td><span class="status-pill <%= statusClass%>"><%= status%></span></td>
                    <td><%= a.getCreatedAt()%></td>
                    <td><a href="<%= request.getContextPath()%>/AppointmentRecordDetails?appointmentId=<%= a.getAppointmentId()%>" class="btn-details">View Details</a></td>
                </tr>
                <% } %>
            </table>
            <% }%>
        </div>
    </body>
</html>