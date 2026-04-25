<%-- 
    Document   : adminClinicDetail
    Created on : 2026/04/25, 15:58:12
    Author     : amzte
--%>

<%@page import="ict.bean.ClinicBean"%>
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
            const card = document.getElementById("detailCard");
            if (card) card.classList.add("edit-mode");
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
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");

    String cancelUrl = (clinic != null) ? (ctx + "/AdminClinicDetail?clinicId=" + clinic.getClinicId()) : (ctx + "/AdminClinicList");

    String openTime = (clinic != null && clinic.getOpenTime() != null) ? clinic.getOpenTime() : "";
    String closeTime = (clinic != null && clinic.getCloseTime() != null) ? clinic.getCloseTime() : "";
    String closeDay = (clinic != null && clinic.getCloseDay() != null) ? clinic.getCloseDay() : "";
%>

<div class="detail-wrap">
    <div class="ud-header">
        <h2 class="h2-title">Clinic Detail</h2>
        <div class="ud-actions">
            <a class="btn-action btn-back" href="<%= ctx %>/AdminClinicList">Back</a>
        </div>
    </div>

    <% if (success != null) { %>
        <div class="success-box"><%= success %></div>
    <% } %>

    <% if (error != null) { %>
        <div class="error-box"><%= error %></div>
    <% } %>

    <% if (clinic == null && error == null) { %>
        <div class="error-box">No clinic data.</div>
    <% } else if (clinic != null) { %>

    <form method="post" action="<%= ctx %>/AdminClinicDetail">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="clinicId" value="<%= clinic.getClinicId() %>">

        <div class="detail-card" id="detailCard">

            <div class="btn-row">
                <div class="actions-view">
                    <a class="btn-action btn-reschedule" href="#" onclick="return enterEditMode();">Edit</a>
                    <a class="btn-action btn-back" href="<%= ctx %>/AdminClinicList">Back</a>
                </div>
                <div class="actions-edit">
                    <button type="submit" class="btn-action btn-reschedule"
                            onclick="return confirm('Confirm update this clinic?');">Save</button>
                    <a class="btn-action btn-back" href="#" onclick="return cancelEdit('<%= cancelUrl %>');">Cancel</a>
                </div>
            </div>

            <h3 class="sec-title">Clinic Info</h3>

            <div class="detail-grid grid-tight">
                <div class="detail-label">Clinic ID</div>
                <div class="detail-value"><%= clinic.getClinicId() %></div>

                <div class="detail-label">Name *</div>
                <div class="detail-value">
                    <span class="view-only"><%= clinic.getName() %></span>
                    <input class="records-filter-input edit-only" name="name" value="<%= clinic.getName() %>" required>
                </div>

                <div class="detail-label">District</div>
                <div class="detail-value">
                    <span class="view-only"><%= clinic.getDistrict() != null ? clinic.getDistrict() : " - " %></span>
                    <input class="records-filter-input edit-only" name="district"
                           value="<%= clinic.getDistrict() != null ? clinic.getDistrict() : "" %>">
                </div>

                <div class="detail-label">Address</div>
                <div class="detail-value">
                    <span class="view-only"><%= clinic.getAddress() != null ? clinic.getAddress() : " - " %></span>
                    <input class="records-filter-input edit-only" name="address"
                           value="<%= clinic.getAddress() != null ? clinic.getAddress() : "" %>">
                </div>

                <div class="detail-label">Open Time</div>
                <div class="detail-value">
                    <span class="view-only"><%= openTime.isBlank() ? " - " : openTime %></span>
                    <input class="records-filter-input edit-only" type="time" name="openTime" value="<%= openTime %>">
                </div>

                <div class="detail-label">Close Time</div>
                <div class="detail-value">
                    <span class="view-only"><%= closeTime.isBlank() ? " - " : closeTime %></span>
                    <input class="records-filter-input edit-only" type="time" name="closeTime" value="<%= closeTime %>">
                </div>

                <div class="detail-label">Close Day</div>
                <div class="detail-value">
                    <span class="view-only"><%= closeDay.isBlank() ? " - " : closeDay %></span>
                    <select class="records-filter-select edit-only" name="closeDay">
                        <option value="" <%= closeDay.isBlank() ? "selected" : "" %> >- None -</option>
                        <option value="Monday"    <%= "Monday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Monday</option>
                        <option value="Tuesday"   <%= "Tuesday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Tuesday</option>
                        <option value="Wednesday" <%= "Wednesday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Wednesday</option>
                        <option value="Thursday"  <%= "Thursday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Thursday</option>
                        <option value="Friday"    <%= "Friday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Friday</option>
                        <option value="Saturday"  <%= "Saturday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Saturday</option>
                        <option value="Sunday"    <%= "Sunday".equalsIgnoreCase(closeDay) ? "selected" : "" %>>Sunday</option>
                    </select>
                </div>

            </div>
        </div>
    </form>
    <% } %>
</div>

</body>
</html>