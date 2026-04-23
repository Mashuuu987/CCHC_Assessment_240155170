<%-- 
    Document   : adminUserDetail
    Created on : 2026/04/24, 0:35:22
    Author     : amzte
--%>

<%@page import="ict.bean.UserInfoBean"%>
<%@page import="ict.bean.StaffProfileBean"%>
<%@page import="ict.bean.PatientProfileBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>User Detail</title>

        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminUserDetail.css">

        <script>
            function enterEditMode() {
                const card = document.getElementById("detailCard");
                if (card)
                    card.classList.add("edit-mode");
                return false;
            }

            function cancelEdit(cancelUrl) {
                // reload to discard changes
                window.location.href = cancelUrl;
                return false;
            }
        </script>
    </head>

    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();

            UserInfoBean user = (UserInfoBean) request.getAttribute("user");
            StaffProfileBean staff = (StaffProfileBean) request.getAttribute("staff");
            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patient");
            String clinicName = (String) request.getAttribute("clinicName");

            List<Map<String, Object>> staffClinicOptions
                    = (List<Map<String, Object>>) request.getAttribute("staffClinicOptions");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            String role = (user != null && user.getRole() != null) ? user.getRole() : "";
            boolean isStaffRole = "STAFF".equalsIgnoreCase(role);
            boolean isPatientRole = "PATIENT".equalsIgnoreCase(role);
            boolean isAdminRole = "ADMIN".equalsIgnoreCase(role);

            boolean showStaffSection = (staff != null) && !isPatientRole;
            boolean showPatientSection = (patient != null) && !isStaffRole;

            String cancelUrl = (user != null) ? (ctx + "/AdminUserDetail?userId=" + user.getUserId()) : (ctx + "/AdminUserList");
        %>

        <div class="detail-wrap">

            <div class="ud-header">
                <h2 class="h2-title">User Detail</h2>
            </div>

            <% if (success != null) {%>
            <div class="success-box"><%= success%></div>
            <% } %>

            <% if (error != null) {%>
            <div class="error-box"><%= error%></div>
            <% } %>

            <% if (user == null) { %>
            <div class="error-box">No user data.</div>
            <% } else {%>

            <form method="post" action="<%= ctx%>/AdminUserDetail"
                  onsubmit="return confirm('Confirm update for this user?');">

                <input type="hidden" name="action" value="update">
                <input type="hidden" name="userId" value="<%= user.getUserId()%>">

                <div class="detail-card" id="detailCard">
                    <h3 class="sec-title">UserInfo</h3>
                    <div class="detail-grid grid-tight">
                        <div class="detail-label">User ID</div>
                        <div class="detail-value"><%= user.getUserId()%></div>

                        <div class="detail-label">Username</div>
                        <div class="detail-value">
                            <span class="view-only"><%= user.getUsername()%></span>
                            <input class="records-filter-input edit-only" type="text" name="username"
                                   value="<%= user.getUsername()%>" required>
                        </div>

                        <div class="detail-label">Role</div>
                        <div class="detail-value"><%= user.getRole()%></div>

                        <div class="detail-label">Password</div>
                        <div class="detail-value">********</div>
                    </div>

                    <% if (showStaffSection) {%>
                    <h3 class="sec-title sec-gap">Staff Profile</h3>

                    <div class="detail-grid grid-tight">
                        <div class="detail-label">Staff ID</div>
                        <div class="detail-value"><%= staff.getStaffId()%></div>

                        <% if (!isAdminRole) {%>
                        <div class="detail-label">Clinic</div>
                        <div class="detail-value">
                            <span class="view-only"><%= clinicName != null ? clinicName : " - "%></span>
                            <span class="edit-only">
                                <select class="records-filter-select" name="staffClinicId" required>
                                    <% if (staffClinicOptions != null) {
                                            for (Map<String, Object> opt : staffClinicOptions) {
                                                String val = String.valueOf(opt.get("value"));
                                                String label = String.valueOf(opt.get("label"));
                                                boolean sel = Boolean.TRUE.equals(opt.get("selected"));
                                    %>
                                    <option value="<%= val%>" <%= sel ? "selected" : ""%>><%= label%></option>
                                    <%     }
                                        }%>
                                </select>
                            </span>
                        </div>

                        <div class="detail-label">Clinic ID</div>
                        <div class="detail-value"><%= staff.getClinicId() != null ? staff.getClinicId() : " - "%></div>
                        <% }%>

                        <div class="detail-label">HKID / ID</div>
                        <div class="detail-value"><%= staff.getHKID()%></div>

                        <div class="detail-label">Full Name</div>
                        <div class="detail-value">
                            <span class="view-only"><%= (staff.getFirstName() + " " + staff.getLastName()).trim()%></span>

                            <div class="edit-only edit-two-col">
                                <input class="records-filter-input" name="staffFirstName" value="<%= staff.getFirstName()%>" required>
                                <input class="records-filter-input" name="staffLastName" value="<%= staff.getLastName()%>" required>
                            </div>
                        </div>

                        <div class="detail-label">Gender</div>
                        <div class="detail-value">
                            <span class="view-only"><%= staff.getGender()%></span>
                            <select class="records-filter-select edit-only" name="staffGender" required>
                                <option value="M" <%= "M".equalsIgnoreCase(staff.getGender()) ? "selected" : ""%>>M</option>
                                <option value="F" <%= "F".equalsIgnoreCase(staff.getGender()) ? "selected" : ""%>>F</option>
                            </select>
                        </div>

                        <div class="detail-label">DOB</div>
                        <div class="detail-value">
                            <span class="view-only"><%= staff.getDOB()%></span>
                            <input class="records-filter-input edit-only" type="date" name="staffDOB"
                                   value="<%= staff.getDOB()%>" required>
                        </div>

                        <div class="detail-label">Position</div>
                        <div class="detail-value">
                            <span class="view-only"><%= staff.getPosition() != null ? staff.getPosition() : " - "%></span>
                            <input class="records-filter-input edit-only" name="staffPosition"
                                   value="<%= staff.getPosition() != null ? staff.getPosition() : ""%>">
                        </div>
                    </div>
                    <% } %>

                    <% if (showPatientSection) {%>
                    <h3 class="sec-title sec-gap">Patient Profile</h3>

                    <div class="detail-grid grid-tight">
                        <div class="detail-label">Patient ID</div>
                        <div class="detail-value"><%= patient.getPatientId()%></div>

                        <div class="detail-label">HKID / ID</div>
                        <div class="detail-value"><%= patient.getHKID()%></div>

                        <div class="detail-label">Full Name</div>
                        <div class="detail-value">
                            <span class="view-only"><%= (patient.getFirstName() + " " + patient.getLastName()).trim()%></span>

                            <div class="edit-only edit-two-col">
                                <input class="records-filter-input" name="patientFirstName" value="<%= patient.getFirstName()%>" required>
                                <input class="records-filter-input" name="patientLastName" value="<%= patient.getLastName()%>" required>
                            </div>
                        </div>

                        <div class="detail-label">Gender</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getGender()%></span>
                            <select class="records-filter-select edit-only" name="patientGender" required>
                                <option value="M" <%= "M".equalsIgnoreCase(patient.getGender()) ? "selected" : ""%>>M</option>
                                <option value="F" <%= "F".equalsIgnoreCase(patient.getGender()) ? "selected" : ""%>>F</option>
                            </select>
                        </div>

                        <div class="detail-label">DOB</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getDOB()%></span>
                            <input class="records-filter-input edit-only" type="date" name="patientDOB"
                                   value="<%= patient.getDOB()%>" required>
                        </div>

                        <div class="detail-label">Phone</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getPhone()%></span>
                            <input class="records-filter-input edit-only" name="patientPhone"
                                   value="<%= patient.getPhone()%>" required>
                        </div>

                        <div class="detail-label">Email</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getEmail() != null ? patient.getEmail() : " - "%></span>
                            <input class="records-filter-input edit-only" name="patientEmail"
                                   value="<%= patient.getEmail() != null ? patient.getEmail() : ""%>">
                        </div>

                        <div class="detail-label">Address</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getAddress() != null ? patient.getAddress() : " - "%></span>
                            <input class="records-filter-input edit-only" name="patientAddress"
                                   value="<%= patient.getAddress() != null ? patient.getAddress() : ""%>">
                        </div>

                        <div class="detail-label">Emergency Contact Name</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getEmergencyContactFullName() != null ? patient.getEmergencyContactFullName() : " - "%></span>
                            <input class="records-filter-input edit-only" name="patientEcName"
                                   value="<%= patient.getEmergencyContactFullName() != null ? patient.getEmergencyContactFullName() : ""%>">
                        </div>

                        <div class="detail-label">Emergency Contact Phone</div>
                        <div class="detail-value">
                            <span class="view-only"><%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : " - "%></span>
                            <input class="records-filter-input edit-only" name="patientEcPhone"
                                   value="<%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : ""%>">
                        </div>
                    </div>
                    <% }%>
                    <div class="btn-row">
                        <div class="actions-view">
                            <a class="btn-action btn-reschedule" href="#" onclick="return enterEditMode();">Edit</a>
                            <a class="btn-action btn-back" href="<%= ctx%>/AdminUserList">Back</a>
                        </div>

                        <div class="actions-edit">
                            <button type="submit" class="btn-action btn-reschedule">Save</button>
                            <a class="btn-action btn-back" href="#" onclick="return cancelEdit('<%= cancelUrl%>');">Cancel</a>
                        </div>
                    </div>
                </div>
            </form>

            <% }%>
        </div>

    </body>
</html>
