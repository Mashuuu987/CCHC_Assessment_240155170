<%-- 
    Document   : appointmentRecordDetails
    Created on : 2026/04/16, 18:03:54
    Author     : amzte
--%>
<%@page import="ict.bean.AppointmentBean, ict.bean.PatientProfileBean, ict.bean.ClinicBean, ict.bean.ServiceBean, ict.bean.UserInfoBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Appointment Detail</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/appointmentRecordDetails.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>
        <%
            AppointmentBean appointment = (AppointmentBean) request.getAttribute("appointment");
            ClinicBean clinic = (ClinicBean) request.getAttribute("clinic");
            ServiceBean service = (ServiceBean) request.getAttribute("service");
            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patient");
            String error = (String) request.getAttribute("error");

            UserInfoBean currentUser = (UserInfoBean) session.getAttribute("userInfo");
            String userRole = currentUser != null ? currentUser.getRole() : "";
            boolean isPatient = "PATIENT".equalsIgnoreCase(userRole);
        %>
        <div class="detail-wrap">
            <h2>Appointment Detail</h2>

            <% if (error != null) {%>
            <div class="error-box"><%= error%></div>
            <% } else if (appointment == null) { %>
            <div class="error-box">No appointment data.</div>
            <% } else {
                String status = appointment.getStatus();
                String statusClass = "s-requested";
                if ("CONFIRMED".equalsIgnoreCase(status))
                    statusClass = "s-confirmed";
                else if ("COMPLETED".equalsIgnoreCase(status))
                    statusClass = "s-completed";
                else if ("NO_SHOW".equalsIgnoreCase(status))
                    statusClass = "s-noshow";
                else if ("CANCELLED_BY_PATIENT".equalsIgnoreCase(status) || "CANCELLED_BY_CLINIC".equalsIgnoreCase(status))
                    statusClass = "s-cancelled";
            %>
            <div class="detail-card">
                <div>
                    Status:
                    <span class="status-pill <%= statusClass%>"><%= appointment.getStatus()%></span>
                </div>

                <div class="detail-grid">
                    <div class="detail-label">Appointment ID</div>
                    <div class="detail-value"><%= appointment.getAppointmentId()%></div>

                    <div class="detail-label">Patient Name</div>
                    <div class="detail-value">
                        <%= patient != null ? patient.getFirstName() + " " + patient.getLastName() : ""%>
                    </div>

                    <div class="detail-label">HKID / ID</div>
                    <div class="detail-value"><%= patient != null ? patient.getHKID() : ""%></div>

                    <div class="detail-label">Clinic</div>
                    <div class="detail-value"><%= clinic != null ? clinic.getName() : ""%></div>

                    <div class="detail-label">District</div>
                    <div class="detail-value"><%= clinic != null ? clinic.getDistrict() : ""%></div>

                    <div class="detail-label">Address</div>
                    <div class="detail-value"><%= clinic != null ? clinic.getAddress() : ""%></div>

                    <div class="detail-label">Service</div>
                    <div class="detail-value"><%= service != null ? service.getName() : ""%></div>

                    <div class="detail-label">Service Type</div>
                    <div class="detail-value"><%= service != null ? service.getServiceType() : ""%></div>

                    <div class="detail-label">Date</div>
                    <div class="detail-value"><%= appointment.getAppointmentDate()%></div>

                    <div class="detail-label">Timeslot</div>
                    <div class="detail-value"><%= appointment.getTimeSlot()%></div>

                    <div class="detail-label">Created At</div>
                    <div class="detail-value"><%= appointment.getCreatedAt()%></div>
                </div>

                <div class="btn-row">
                    <% if (isPatient) { %>
                    <a class="btn-action btn-reschedule" href="<%= request.getContextPath()%>/AppointmentReschedule?appointmentId=<%= appointment.getAppointmentId()%>">Reschedule</a>
                    <a class="btn-action btn-cancel" href="<%= request.getContextPath()%>/AppointmentCancel?appointmentId=<%= appointment.getAppointmentId()%>">Cancel</a>
                    <a class="btn-action btn-back" href="<%= request.getContextPath()%>/AppointmentRecordsPatient">Back</a>
                    <% } else { %>
                    <a class="btn-action btn-back" href="<%= request.getContextPath()%>/AppointmentRecordsStaff">Back</a>
                    <% } %>
                </div>
            </div>
            <% }%>
        </div>
    </body>
</html>