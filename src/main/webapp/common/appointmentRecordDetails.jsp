<%-- 
    Document   : appointmentRecordDetails
    Created on : 2026/04/16, 18:03:54
    Author     : amzte
--%>
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.List,java.util.Set"%>
<%@page import="ict.bean.AppointmentBean, ict.bean.PatientProfileBean, ict.bean.ClinicBean, ict.bean.ServiceBean, ict.bean.ServiceCapacityBean, ict.bean.UserInfoBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Appointment Detail</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/appointmentRecordDetails.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <script>
            function hideRescheduleTimeslots() {
                const section = document.getElementById('rescheduleTimeslotSection');
                const confirmButton = document.getElementById('confirmRescheduleButton');
                if (section) {
                    section.style.display = 'none';
                }
                if (confirmButton) {
                    confirmButton.disabled = true;
                }
                const radios = document.querySelectorAll('input[name="newTimeSlot"]');
                radios.forEach(function (radio) {
                    radio.checked = false;
                });
            }

            function showRescheduleTimeslots() {
                const section = document.getElementById('rescheduleTimeslotSection');
                const confirmButton = document.getElementById('confirmRescheduleButton');
                if (section) {
                    section.style.display = 'block';
                }
                if (confirmButton) {
                    confirmButton.disabled = false;
                }
            }

            document.addEventListener('DOMContentLoaded', function () {
                const dateInput = document.getElementById('rescheduleDateInput');
                if (dateInput) {
                    dateInput.addEventListener('change', hideRescheduleTimeslots);
                }
            });
        </script>
    </head>
    <body>
        <%@ include file="/heading.jsp" %>
        <%
            AppointmentBean appointment = (AppointmentBean) request.getAttribute("appointment");
            ClinicBean clinic = (ClinicBean) request.getAttribute("clinic");
            ServiceBean service = (ServiceBean) request.getAttribute("service");
            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patient");
            String error = (String) request.getAttribute("error");

            String success = (String) request.getAttribute("success");
            Boolean showRescheduleForm = (Boolean) request.getAttribute("showRescheduleForm");
            String selectedNewDate = (String) request.getAttribute("selectedNewDate");
            String selectedNewTimeSlot = (String) request.getAttribute("selectedNewTimeSlot");
            List<ServiceCapacityBean> capList = (List<ServiceCapacityBean>) request.getAttribute("capacityList");
            Set<String> fullSlots = (Set<String>) request.getAttribute("fullTimeSlots");
            String minDate = LocalDate.now().toString();

            Boolean isPatient = (Boolean) request.getAttribute("isPatient");
            Boolean isStaff = (Boolean) request.getAttribute("isStaff");
        %>
        <div class="detail-wrap">
            <h2>Appointment Detail</h2>

            <% if (success != null) {%>
            <div class="success-box"><%= success%></div>
            <% } %>

            <% if (error != null) {%>
            <div class="error-box"><%= error%></div>
            <% } %>

            <% if (appointment == null) { %>
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
                    <% if (isPatient) {%>
                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordPatientAction" class="inline-form">
                        <input type="hidden" name="action" value="prepareReschedule" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <button type="submit" class="btn-action btn-reschedule">Reschedule</button>
                    </form>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordPatientAction" class="inline-form"
                          onsubmit="return confirm('Are you sure you want to cancel this appointment?');">
                        <input type="hidden" name="action" value="cancel" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <input type="hidden" name="confirmCancel" value="YES" />
                        <button type="submit" class="btn-action btn-cancel">Cancel</button>
                    </form>

                    <a class="btn-action btn-back" href="<%= request.getContextPath()%>/AppointmentRecordsPatient">Back</a>
                    <% } else if (isStaff != null && isStaff) {%>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordStaffAction" class="inline-form">
                        <input type="hidden" name="action" value="confirm" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <button type="submit" class="btn-action btn-reschedule">Confirm</button>
                    </form>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordStaffAction" class="inline-form">
                        <input type="hidden" name="action" value="complete" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <button type="submit" class="btn-action btn-reschedule">Completed</button>
                    </form>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordStaffAction" class="inline-form">
                        <input type="hidden" name="action" value="noShow" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <button type="submit" class="btn-action btn-cancel">No Show</button>
                    </form>

                    <!-- Cancel with reason -->
                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordStaffAction" class="inline-form"
                          onsubmit="return confirm('Cancel this appointment by clinic?');">
                        <input type="hidden" name="action" value="cancelByClinic" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <input type="text" name="reason" placeholder="Reason (required)" required class="records-filter-input" />
                        <button type="submit" class="btn-action btn-cancel">Cancel</button>
                    </form>

                    <a class="btn-action btn-back" href="<%= request.getContextPath()%>/AppointmentRecordsStaff">Back</a>

                    <a class="btn-action btn-back" href="<%= request.getContextPath()%>/AppointmentRecordsStaff">Back</a>
                    <% }%>
                </div>
                <% if (isPatient && showRescheduleForm != null && showRescheduleForm) {%>
                <div class="reschedule-panel">
                    <h3>Reschedule Appointment</h3>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordPatientAction" class="reschedule-date-form">
                        <input type="hidden" name="action" value="prepareReschedule" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />

                        <label class="status-pill <%= statusClass%>">New Date</label>
                        <input id="rescheduleDateInput" class="records-filter-input" type="date" min="<%= minDate%>" name="newDate" value="<%= selectedNewDate != null ? selectedNewDate : ""%>" required />
                        <button type="submit" class="btn-action btn-reschedule">Load Times</button>
                    </form>

                    <form method="post" action="<%= request.getContextPath()%>/AppointmentRecordPatientAction"
                          onsubmit="return confirm('Are you sure you want to reschedule this appointment?');">
                        <input type="hidden" name="action" value="confirmReschedule" />
                        <input type="hidden" name="appointmentId" value="<%= appointment.getAppointmentId()%>" />
                        <input type="hidden" name="newDate" value="<%= selectedNewDate != null ? selectedNewDate : ""%>" />
                        <input type="hidden" name="confirmReschedule" value="YES" />

                        <% if (selectedNewDate != null) { %>
                        <div id="rescheduleTimeslotSection" class="timeslot-list">
                            <% } else { %>
                            <div id="rescheduleTimeslotSection" class="timeslot-list" style="display:none;">
                                <% } %>
                                <% if (capList != null && !capList.isEmpty()) {
                                        for (ServiceCapacityBean cap : capList) {
                                            boolean isFull = fullSlots != null && fullSlots.contains(cap.getTimeSlot());
                                            boolean checked = selectedNewTimeSlot != null && selectedNewTimeSlot.equals(cap.getTimeSlot());
                                %>
                                <div class="timeslot-item <%= isFull ? "timeslot-disabled" : ""%>">
                                    <label>
                                        <input type="radio" name="newTimeSlot" value="<%= cap.getTimeSlot()%>"
                                               <%= checked ? "checked" : ""%> <%= isFull ? "disabled" : ""%> />
                                        <span><%= cap.getTimeSlot()%> <%= isFull ? "(Full)" : ""%></span>
                                    </label>
                                </div>
                                <%   }
                                }%>
                            </div>

                            <button id="confirmRescheduleButton" type="submit" class="btn-action btn-reschedule" <%= selectedNewDate != null ? "" : "disabled"%>>Confirm Reschedule</button>
                    </form>
                </div>
                <% }%>
            </div>
            <% }%>
        </div>
    </body>
</html>