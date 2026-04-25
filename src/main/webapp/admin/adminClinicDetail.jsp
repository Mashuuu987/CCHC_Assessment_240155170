<%-- 
    Document   : adminClinicDetail
    Created on : 2026/04/25, 15:58:12
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page import="ict.bean.ServiceCapacityBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Clinic Detail</title>

        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminUserDetail-adminClinicDetail.css">
        <script>
            function enterEditMode() {
                const card = document.getElementById("clinicCard");
                if (card)
                    card.classList.add("edit-mode");
                return false;
            }
            function cancelEdit(url) {
                window.location.href = url;
                return false;
            }
        </script>
    </head>

    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();

            ClinicBean clinic = (ClinicBean) request.getAttribute("clinic");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            Integer selectedServiceId = (Integer) request.getAttribute("selectedServiceId");
            ServiceBean selectedService = (ServiceBean) request.getAttribute("selectedService");
            List<ServiceCapacityBean> capList = (List<ServiceCapacityBean>) request.getAttribute("capacityList");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            String cancelUrl = (clinic != null) ? (ctx + "/AdminClinicDetail?clinicId=" + clinic.getClinicId()
                    + (selectedServiceId != null ? "&serviceId=" + selectedServiceId : "")) : (ctx + "/AdminClinicList");

            String openTime = (clinic != null && clinic.getOpenTime() != null) ? clinic.getOpenTime() : "";
            String closeTime = (clinic != null && clinic.getCloseTime() != null) ? clinic.getCloseTime() : "";
            String closeDay = (clinic != null && clinic.getCloseDay() != null) ? clinic.getCloseDay() : "";
        %>

        <div class="detail-wrap">
            <div class="ud-header">
                <h2 class="h2-title">Clinic Detail</h2>
                <div class="ud-actions">
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminClinicList">Back</a>
                </div>
            </div>

            <% if (success != null) {%>
            <div class="success-box"><%= success%></div>
            <% } %>
            <% if (error != null) {%>
            <div class="error-box"><%= error%></div>
            <% } %>

            <% if (clinic == null && error == null) { %>
            <div class="error-box">No clinic data.</div>
            <% } else if (clinic != null) {%>
            <form method="post" action="<%= ctx%>/AdminClinicDetail">
                <input type="hidden" name="action" value="updateClinic">
                <input type="hidden" name="clinicId" value="<%= clinic.getClinicId()%>">
                <% if (selectedServiceId != null) {%>
                <input type="hidden" name="serviceId" value="<%= selectedServiceId%>">
                <% }%>

                <div class="detail-card" id="clinicCard">
                    <h3 class="sec-title">Clinic Info</h3>

                    <div class="detail-grid grid-tight">
                        <div class="detail-label">Clinic ID</div>
                        <div class="detail-value"><%= clinic.getClinicId()%></div>

                        <div class="detail-label">Name *</div>
                        <div class="detail-value">
                            <span class="view-only"><%= clinic.getName()%></span>
                            <input class="records-filter-input edit-only" name="name" value="<%= clinic.getName()%>" required>
                        </div>

                        <div class="detail-label">District</div>
                        <div class="detail-value">
                            <span class="view-only"><%= clinic.getDistrict() != null ? clinic.getDistrict() : " - "%></span>
                            <input class="records-filter-input edit-only" name="district"
                                   value="<%= clinic.getDistrict() != null ? clinic.getDistrict() : ""%>">
                        </div>

                        <div class="detail-label">Address</div>
                        <div class="detail-value">
                            <span class="view-only"><%= clinic.getAddress() != null ? clinic.getAddress() : " - "%></span>
                            <input class="records-filter-input edit-only" name="address"
                                   value="<%= clinic.getAddress() != null ? clinic.getAddress() : ""%>">
                        </div>

                        <div class="detail-label">Open Time</div>
                        <div class="detail-value">
                            <span class="view-only"><%= openTime.isBlank() ? " - " : openTime%></span>
                            <input class="records-filter-input edit-only" type="time" name="openTime" value="<%= openTime%>">
                        </div>

                        <div class="detail-label">Close Time</div>
                        <div class="detail-value">
                            <span class="view-only"><%= closeTime.isBlank() ? " - " : closeTime%></span>
                            <input class="records-filter-input edit-only" type="time" name="closeTime" value="<%= closeTime%>">
                        </div>

                        <div class="detail-label">Close Day</div>
                        <div class="detail-value">
                            <span class="view-only"><%= closeDay.isBlank() ? " - " : closeDay%></span>
                            <select class="records-filter-select edit-only" name="closeDay">
                                <option value="" <%= closeDay.isBlank() ? "selected" : ""%>>- None -</option>
                                <option value="Monday"    <%= "Monday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Monday</option>
                                <option value="Tuesday"   <%= "Tuesday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Tuesday</option>
                                <option value="Wednesday" <%= "Wednesday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Wednesday</option>
                                <option value="Thursday"  <%= "Thursday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Thursday</option>
                                <option value="Friday"    <%= "Friday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Friday</option>
                                <option value="Saturday"  <%= "Saturday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Saturday</option>
                                <option value="Sunday"    <%= "Sunday".equalsIgnoreCase(closeDay) ? "selected" : ""%>>Sunday</option>
                            </select>
                        </div>
                    </div>
                    <div class="btn-row">
                        <div class="actions-view">
                            <a class="btn-action btn-reschedule" href="#" onclick="return enterEditMode();">Edit</a>
                        </div>
                        <div class="actions-edit">
                            <button type="submit" class="btn-action btn-reschedule"
                                    onclick="return confirm('Confirm update this clinic?');">Save</button>
                            <a class="btn-action btn-back" href="#" onclick="return cancelEdit('<%= cancelUrl%>');">Cancel</a>
                        </div>
                    </div>
                </div>
            </form>
            <div class="detail-card" style="margin-top:14px;">
                <h3 class="sec-title">Services</h3>

                <table class="records-table">
                    <thead>
                        <tr>
                            <th>Service ID</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Duration (mins)</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (services == null || services.isEmpty()) { %>
                        <tr><td colspan="5" class="empty">No services found.</td></tr>
                        <% } else {
                            for (ServiceBean s : services) {
                                boolean isSel = (selectedServiceId != null && selectedServiceId == s.getServiceId());
                        %>
                        <tr>
                            <td><%= s.getServiceId()%></td>
                            <td><%= s.getName()%></td>
                            <td><%= s.getServiceType() != null ? s.getServiceType() : ""%></td>
                            <td><%= s.getDurationMins()%></td>
                            <td>
                                <div class="action-cell">
                                    <a class="btn-details" href="<%= ctx%>/AdminClinicDetail?clinicId=<%= clinic.getClinicId()%>&serviceId=<%= s.getServiceId()%>">
                                        <%= isSel ? "Viewing" : "Manage Capacity"%>
                                    </a>
                                </div>
                            </td>
                        </tr>
                        <% }
                            } %>
                    </tbody>
                </table>
            </div>
            <div class="detail-card" style="margin-top:14px;">
                <h3 class="sec-title">Capacity Rules
                    <% if (selectedService != null) {%>
                    - <%= selectedService.getName()%> (Service ID: <%= selectedService.getServiceId()%>)
                    <% } %>
                </h3>

                <% if (selectedServiceId == null) { %>
                <div class="empty">Please select a service above to manage capacity rules.</div>
                <% } else {%>
                <form method="post" action="<%= ctx%>/AdminClinicDetail" class="records-search-row" style="margin-bottom:12px;">
                    <input type="hidden" name="action" value="createCapacity">
                    <input type="hidden" name="clinicId" value="<%= clinic.getClinicId()%>">
                    <input type="hidden" name="serviceId" value="<%= selectedServiceId%>">


                    <input class="records-filter-input"
                           type="text"
                           name="timeSlot"
                           placeholder="TimeSlot (HH:mm, e.g. 09:00 or 24:00)"
                           pattern="(24:00|([01][0-9]|2[0-3]):[0-5][0-9])"
                           title="Please enter HH:mm 00:00-24:00"
                           required>

                    <input class="records-filter-input" type="number" name="quota" placeholder="Quota" min="0" required>
                    <button type="submit" class="records-filter-reset"
                            onclick="return confirm('Create this capacity rule?');">Add Rule</button>
                </form>

                <table class="records-table">
                    <thead>
                        <tr>
                            <th>TimeSlot</th>
                            <th>Quota</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (capList == null || capList.isEmpty()) { %>
                        <tr><td colspan="3" class="empty">No capacity rules for this service/clinic yet.</td></tr>
                        <% } else {
                            for (ServiceCapacityBean cap : capList) {%>
                        <tr>
                            <td><%= cap.getTimeSlot()%></td>

                            <td>
                                <input class="records-filter-input" type="number" name="quota" min="0"
                                       value="<%= cap.getQuota()%>" style="max-width:140px;"
                                       form="f-update-<%= cap.getCapacityId()%>">
                            </td>
                            <td>
                                <div class="action-cell">
                                    <form id="f-update-<%= cap.getCapacityId()%>" method="post" action="<%= ctx%>/AdminClinicDetail"
                                          onsubmit="return confirm('Update quota?');">
                                        <input type="hidden" name="action" value="updateCapacity">
                                        <input type="hidden" name="clinicId" value="<%= clinic.getClinicId()%>">
                                        <input type="hidden" name="serviceId" value="<%= selectedServiceId%>">
                                        <input type="hidden" name="capacityId" value="<%= cap.getCapacityId()%>">
                                        <button type="submit" class="records-filter-reset">Save</button>
                                    </form>
                                    <form method="post" action="<%= ctx%>/AdminClinicDetail"
                                          onsubmit="return confirm('Delete this capacity rule?');">
                                        <input type="hidden" name="action" value="deleteCapacity">
                                        <input type="hidden" name="clinicId" value="<%= clinic.getClinicId()%>">
                                        <input type="hidden" name="serviceId" value="<%= selectedServiceId%>">
                                        <input type="hidden" name="capacityId" value="<%= cap.getCapacityId()%>">
                                        <input type="hidden" name="timeSlot" value="<%= cap.getTimeSlot()%>">
                                        <button type="submit" class="btn-danger">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <% }
                            } %>
                    </tbody>
                </table>
                <% } %>
            </div>
            <% }%>
        </div>
    </body>
</html>