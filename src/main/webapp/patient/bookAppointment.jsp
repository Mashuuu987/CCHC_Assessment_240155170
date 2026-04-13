<%-- 
    Document   : bookAppointment
    Created on : 2026/04/13, 20:00:27
    Author     : amzte
--%>
<%@page import="ict.bean.UserInfoBean, ict.bean.ServiceCapacityBean, java.util.List, ict.bean.ClinicBean, ict.bean.ServiceBean, ict.bean.PatientProfileBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Appointment</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/bookAppointment.css">
    </head>
    <body>
        <%
            UserInfoBean user = (UserInfoBean) session.getAttribute("userInfo");
            if (user == null || !"PATIENT".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/Login");
                return;
            }

            String ctx = request.getContextPath();
            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            List<ServiceCapacityBean> capList = (List<ServiceCapacityBean>) request.getAttribute("capacityList");

            Integer selectedClinicId = (Integer) request.getAttribute("selectedClinicId");
            Integer selectedServiceId = (Integer) request.getAttribute("selectedServiceId");
            String selectedDate = (String) request.getAttribute("selectedDate");
            String selectedTimeSlot = (String) request.getAttribute("selectedTimeSlot");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patient");

            Integer stepAttr = (Integer) request.getAttribute("currentStep");
            int currentStep = 1;
            if (patient != null && selectedTimeSlot != null) {
                currentStep = 3;
            } else if (capList != null && selectedClinicId != null && selectedServiceId != null && selectedDate != null) {
                currentStep = 2;
            } else if (stepAttr != null) {
                currentStep = stepAttr;
            }

            ClinicBean selectedClinic = null;
            if (selectedClinicId != null && clinics != null) {
                for (ClinicBean cb : clinics) {
                    if (cb.getClinicId() == selectedClinicId) {
                        selectedClinic = cb;
                        break;
                    }
                }
            }

            ServiceBean selectedService = null;
            if (selectedServiceId != null && services != null) {
                for (ServiceBean sb : services) {
                    if (sb.getServiceId() == selectedServiceId) {
                        selectedService = sb;
                        break;
                    }
                }
            }
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container">
            <h1 class="page-title">Book Appointment</h1>

            <div class="step-indicator">
                <div class="step-item <%= (currentStep == 1) ? "step-item-active" : ""%>">
                    <div class="step-number">1</div>
                    <div class="step-label">Select clinic / service / date</div>
                </div>
                <div class="step-line"></div>
                <div class="step-item <%= (currentStep == 2) ? "step-item-active" : ""%>">
                    <div class="step-number">2</div>
                    <div class="step-label">Choose timeslot</div>
                </div>
                <div class="step-line"></div>
                <div class="step-item <%= (currentStep == 3) ? "step-item-active" : ""%>">
                    <div class="step-number">3</div>
                    <div class="step-label">Confirm</div>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="bookAppointment-title bookAppointment-message-error"><%= error%></div>
            <% } else if (success != null) {%>
            <div class ="bookAppointment-message"><%= success%></div>
            <div style="font-size:64px; text-align:center; color:#28a745; margin-top:20px;">&#10004;</div>
            <div class="bookAppointment-actions">
                    <button type="button" class="btn-back" onclick="window.location='<%= ctx%>/index.jsp'">Back to homepage</button>
                </div>
            <% }%>

            <%-- Step 1: clinic / service / date --%>
            <% if (currentStep == 1) { %>
            <form method="post" action="<%= ctx%>/BookAppointment" class="bookAppointment-form">
                <input type="hidden" name="step" value="1" />

                <div class="bookAppointment-row">
                    <label class="bookAppointment-label">Clinic</label>
                    <select name="clinicId" class="bookAppointment-input">
                        <option value="">-- Please select --</option>
                        <% if (clinics != null) {
                                for (ClinicBean cb : clinics) {
                                    boolean sel = (selectedClinicId != null && selectedClinicId == cb.getClinicId());
                        %>
                        <option value="<%= cb.getClinicId()%>" <%= sel ? "selected" : ""%>>
                            <%= cb.getName()%>
                        </option>
                        <%      }
                            } %>
                    </select>
                </div>

                <div class="bookAppointment-row">
                    <label class="bookAppointment-label">Service</label>
                    <select name="serviceId" class="bookAppointment-input">
                        <option value="">-- Please select --</option>
                        <% if (services != null) {
                                for (ServiceBean cb : services) {
                                    boolean sel = (selectedServiceId != null && selectedServiceId == cb.getServiceId());
                        %>
                        <option value="<%= cb.getServiceId()%>" <%= sel ? "selected" : ""%>>
                            <%= cb.getName()%>
                        </option>
                        <%      }
                            }%>
                    </select>
                </div>
                <div class="bookAppointment-row">
                    <label class="bookAppointment-label">Date</label>
                    <input type="date" name="appointmentDate" class="bookAppointment-input" value="<%= (selectedDate != null) ? selectedDate : ""%>" />
                </div>

                <div class="bookAppointment-actions">
                    <button type="submit" class="btn-primary">Next: choose timeslot</button>
                </div>
            </form>

            <%-- Clinic information cards --%>
            <% if (clinics != null) { %>
            <div class="clinic-list">
                <h2 class="section-title">Clinic information</h2>
                <% for (ClinicBean cb : clinics) { %>
                <div class="clinic-card">
                    <div class="clinic-info-name"><%= cb.getName()%></div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">District:</span>
                        <span><%= cb.getDistrict()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Address:</span>
                        <span><%= cb.getAddress()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Opening hours:</span>
                        <span><%= cb.getOpenTime()%> - <%= cb.getCloseTime()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Closed on:</span>
                        <span><%= cb.getCloseDay()%></span>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
            <% } %>

            <%-- Step 2: choose timeslot --%>
            <% if (currentStep == 2 && capList != null && selectedClinicId != null && selectedServiceId != null && selectedDate != null) { 
                   java.util.Set<String> fullSlots = (java.util.Set<String>) request.getAttribute("fullTimeSlots");
               %>
            <form method="post" action="<%= ctx%>/BookAppointment" class="bookAppointment-form">
                <input type="hidden" name="step" value="2" />
                <input type="hidden" name="clinicId" value="<%= selectedClinicId%>" />
                <input type="hidden" name="serviceId" value="<%= selectedServiceId%>" />
                <input type="hidden" name="appointmentDate" value="<%= selectedDate%>" />

                <% if (selectedClinic != null) { %>
                <div class="clinic-info">
                    <div class="clinic-info-name"><%= selectedClinic.getName()%></div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">District:</span>
                        <span><%= selectedClinic.getDistrict()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Address:</span>
                        <span><%= selectedClinic.getAddress()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Opening hours:</span>
                        <span><%= selectedClinic.getOpenTime()%> - <%= selectedClinic.getCloseTime()%></span>
                    </div>
                    <div class="clinic-info-line">
                        <span class="clinic-info-label">Closed on:</span>
                        <span><%= selectedClinic.getCloseDay()%></span>
                    </div>
                </div>
                <% } %>

                <div class="bookAppointment-row">
                    <label class="bookAppointment-label">Available timeslots on <%= selectedDate%></label>
                    <% if (capList != null && !capList.isEmpty()) { %>
                    <div class="timeslot-list">
                        <% for (ServiceCapacityBean cap : capList) { 
                               boolean isFull = (fullSlots != null && fullSlots.contains(cap.getTimeSlot()));
                           %>
                        <div class="timeslot-item <%= isFull ? "timeslot-disabled" : "" %>">
                            <label>
                                <input type="radio" name="timeSlot" value="<%= cap.getTimeSlot()%>" <%= (selectedTimeSlot != null && selectedTimeSlot.equals(cap.getTimeSlot())) ? "checked" : ""%> <%= isFull ? "disabled" : "" %> />
                                <span><%= cap.getTimeSlot()%><%= isFull ? " (Full)" : "" %></span>
                            </label>
                        </div>
                        <% } %>
                    </div>
                    <% } else { %>
                    <div class="bookAppointment-message">
                        No available timeslots for this clinic and service. Please choose another combination.
                    </div>
                    <% } %>
                </div>
                <div class="bookAppointment-actions">
                    <button type="submit" class="btn-primary">Next: review &amp; confirm</button>
                </div>
                
                <div class="bookAppointment-actions">
                    <button type="button" class="btn-back" onclick="window.location='<%= ctx%>/BookAppointment'">Restart</button>
                </div>
            </form>
            <% } %>

            <%-- Step 3: confirmation summary (hidden after success) --%>
            <% if (success == null && currentStep == 3 && selectedClinicId != null && selectedServiceId != null && selectedDate != null && selectedTimeSlot != null && patient != null) { %>
            <form method="post" action="<%= ctx%>/BookAppointment" class="bookAppointment-form">
                <input type="hidden" name="step" value="3" />
                <input type="hidden" name="clinicId" value="<%= selectedClinicId%>" />
                <input type="hidden" name="serviceId" value="<%= selectedServiceId%>" />
                <input type="hidden" name="appointmentDate" value="<%= selectedDate%>" />
                <input type="hidden" name="timeSlot" value="<%= selectedTimeSlot%>" />

                <h2 class="section-title">Confirm your appointment</h2>

                <div class="review-grid">
                    <div class="review-section">
                        <h3>Patient</h3>
                        <div class="review-main"><%= patient.getFirstName()%> <%= patient.getLastName()%></div>
                        <div><strong>HKID:</strong> <%= patient.getHKID()%></div>
                        <div><strong>Date of Birth:</strong> <%= patient.getDOB()%></div>
                        <div><strong>Phone:</strong> <%= patient.getPhone()%></div>
                        <div><strong>Email:</strong> <%= patient.getEmail()%></div>
                    </div>

                    <div class="review-section">
                        <h3>Appointment</h3>
                        <% if (selectedClinic != null) { %>
                        <div class="review-main"><%= selectedClinic.getName()%></div>
                        <div><strong>Address:</strong> <%= selectedClinic.getAddress()%></div>
                        <% } %>
                        <% if (selectedService != null) { %>
                        <div><strong>Service:</strong> <%= selectedService.getName()%></div>
                        <% } %>
                        <div><strong>Date:</strong> <%= selectedDate%></div>
                        <div><strong>Timeslot:</strong> <%= selectedTimeSlot%></div>
                    </div>
                </div>

                <div class="bookAppointment-actions">
                    <button type="submit" name="action" value="confirm" class="btn-primary">Confirm appointment</button>
                    <button type="button" class="btn-back" onclick="window.location='<%= ctx%>/BookAppointment'">Restart</button>
                </div>
            </form>
            <% } %>
        </div>
    </body>
</html>
