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
            function toggleEdit() {
                const panel = document.getElementById("editPanel");
                if (panel) {
                    panel.style.display = (panel.style.display === "none" ? "block" : "none");
                }
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

            List<Map<String, Object>> staffClinicOptions = (List<Map<String, Object>>) request.getAttribute("staffClinicOptions");

            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");

            String role = (user != null && user.getRole() != null) ? user.getRole() : "";
            boolean isStaffRole = "STAFF".equalsIgnoreCase(role);
            boolean isPatientRole = "PATIENT".equalsIgnoreCase(role);
            boolean isAdminRole = "ADMIN".equalsIgnoreCase(role);
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

            <div class="detail-card">
                <h3 class="sec-title">UserInfo</h3>
                <div class="detail-grid grid-tight">
                    <div class="detail-label">User ID</div>
                    <div class="detail-value"><%= user.getUserId()%></div>

                    <div class="detail-label">Username</div>
                    <div class="detail-value"><%= user.getUsername()%></div>

                    <div class="detail-label">Role</div>
                    <div class="detail-value"><%= user.getRole()%></div>

                    <div class="detail-label">Password</div>
                    <div class="detail-value">********</div>
                </div>

                <% if ((staff != null) && !isPatientRole) { %>
                <h3 class="sec-title sec-gap">Staff Profile</h3>

                <% if (staff == null) { %>
                <div class="error-box">No staff profile data.</div>
                <% } else {%>
                <div class="detail-grid grid-tight">
                    <div class="detail-label">Staff ID</div>
                    <div class="detail-value"><%= staff.getStaffId()%></div>
                    <%if (!isAdminRole) {%>
                    <div class="detail-label">Clinic</div>
                    <div class="detail-value"><%= clinicName != null ? clinicName : " - "%></div>

                    <div class="detail-label">Clinic ID</div>
                    <div class="detail-value"><%= staff.getClinicId() != null ? staff.getClinicId() : " - "%></div>
                    <% }%>
                    <div class="detail-label">HKID / ID</div>
                    <div class="detail-value"><%= staff.getHKID()%></div>

                    <div class="detail-label">Full Name</div>
                    <div class="detail-value"><%= (staff.getFirstName() + " " + staff.getLastName()).trim()%></div>

                    <div class="detail-label">Gender</div>
                    <div class="detail-value"><%= staff.getGender()%></div>

                    <div class="detail-label">DOB</div>
                    <div class="detail-value"><%= staff.getDOB()%></div>

                    <div class="detail-label">Position</div>
                    <div class="detail-value"><%= staff.getPosition() != null ? staff.getPosition() : " - "%></div>
                </div>
                <% } %>
                <% } %>

                <% if ((patient != null) && !isStaffRole) { %>
                <h3 class="sec-title sec-gap">Patient Profile</h3>

                <% if (patient == null) { %>
                <div class="error-box">No patient profile data.</div>
                <% } else {%>
                <div class="detail-grid grid-tight">
                    <div class="detail-label">Patient ID</div>
                    <div class="detail-value"><%= patient.getPatientId()%></div>

                    <div class="detail-label">HKID / ID</div>
                    <div class="detail-value"><%= patient.getHKID()%></div>

                    <div class="detail-label">Full Name</div>
                    <div class="detail-value"><%= (patient.getFirstName() + " " + patient.getLastName()).trim()%></div>

                    <div class="detail-label">Gender</div>
                    <div class="detail-value"><%= patient.getGender()%></div>

                    <div class="detail-label">DOB</div>
                    <div class="detail-value"><%= patient.getDOB()%></div>

                    <div class="detail-label">Phone</div>
                    <div class="detail-value"><%= patient.getPhone()%></div>

                    <div class="detail-label">Email</div>
                    <div class="detail-value"><%= patient.getEmail() != null ? patient.getEmail() : " - "%></div>

                    <div class="detail-label">Address</div>
                    <div class="detail-value"><%= patient.getAddress() != null ? patient.getAddress() : " - "%></div>

                    <div class="detail-label">Emergency Contact Name</div>
                    <div class="detail-value"><%= patient.getEmergencyContactFullName() != null ? patient.getEmergencyContactFullName() : " - "%></div>

                    <div class="detail-label">Emergency Contact Phone</div>
                    <div class="detail-value"><%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : " - "%></div>
                </div>
                <% } %>
                <% }%>
                <div class="btn-row">
                    <a class="btn-action btn-reschedule" href="#" onclick="return toggleEdit();">Edit</a>
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminUserList">Back</a>
                </div>


                <div id="editPanel" class="action-panel" style="display:none;">
                    <h3 class="sec-title">Edit User</h3>

                    <form method="post" action="<%= ctx%>/AdminUserDetail"
                          onsubmit="return confirm('Confirm update for this user?');">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="userId" value="<%= user.getUserId()%>">

                        <div class="detail-grid grid-tight">
                            <div class="detail-label">Username</div>
                            <div class="detail-value">
                                <input class="records-filter-input" type="text" name="username"
                                       value="<%= user.getUsername()%>" required>
                            </div>

                            <div class="detail-label">Role</div>
                            <div class="detail-value">
                                <span><%= user.getRole()%></span>
                            </div>
                        </div>

                        <% if ((staff != null) && !isPatientRole) {%>
                        <h4 class="sub-title">Staff Profile</h4>
                        <div class="detail-grid grid-tight">
                            <div class="detail-label">First Name</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="staffFirstName"
                                       value="<%= staff.getFirstName()%>" required>
                            </div>

                            <div class="detail-label">Last Name</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="staffLastName"
                                       value="<%= staff.getLastName()%>" required>
                            </div>

                            <div class="detail-label">Gender</div>
                            <div class="detail-value">
                                <select class="records-filter-select" name="staffGender" required>
                                    <option value="M" <%= "M".equalsIgnoreCase(staff.getGender()) ? "selected" : ""%>>M</option>
                                    <option value="F" <%= "F".equalsIgnoreCase(staff.getGender()) ? "selected" : ""%>>F</option>
                                </select>
                            </div>

                            <div class="detail-label">DOB</div>
                            <div class="detail-value">
                                <input class="records-filter-input" type="date" name="staffDOB"
                                       value="<%= staff.getDOB()%>" required>
                            </div>

                            <%if (!isAdminRole) {%>
                            <div class="detail-label">Clinic ID</div>
                            <div class="detail-value">
                                <select class="records-filter-select" name="staffClinicId">
                                    <% if (staffClinicOptions != null) {
                                            for (Map<String, Object> opt : staffClinicOptions) {
                                                String val = String.valueOf(opt.get("value"));
                                                String label = String.valueOf(opt.get("label"));
                                                boolean sel = Boolean.TRUE.equals(opt.get("selected"));
                                    %>
                                    <option value="<%= val%>" <%= sel ? "selected" : ""%>><%= label%></option>
                                    <%     }
                                        } %>
                                </select>
                            </div>
                            <% }%>
                            <div class="detail-label">Position</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="staffPosition"
                                       value="<%= staff.getPosition() != null ? staff.getPosition() : ""%>">
                            </div>
                        </div>
                        <% } %>

                        <% if ((patient != null) && !isStaffRole) {%>
                        <h4 class="sub-title">Patient Profile</h4>
                        <div class="detail-grid grid-tight">
                            <div class="detail-label">First Name</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientFirstName"
                                       value="<%= patient.getFirstName()%>" required>
                            </div>

                            <div class="detail-label">Last Name</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientLastName"
                                       value="<%= patient.getLastName()%>" required>
                            </div>

                            <div class="detail-label">Gender</div>
                            <div class="detail-value">
                                <select class="records-filter-select" name="patientGender" required>
                                    <option value="M" <%= "M".equalsIgnoreCase(patient.getGender()) ? "selected" : ""%>>M</option>
                                    <option value="F" <%= "F".equalsIgnoreCase(patient.getGender()) ? "selected" : ""%>>F</option>
                                </select>
                            </div>

                            <div class="detail-label">DOB</div>
                            <div class="detail-value">
                                <input class="records-filter-input" type="date" name="patientDOB"
                                       value="<%= patient.getDOB()%>" required>
                            </div>

                            <div class="detail-label">Phone</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientPhone"
                                       value="<%= patient.getPhone()%>" required>
                            </div>

                            <div class="detail-label">Email</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientEmail"
                                       value="<%= patient.getEmail() != null ? patient.getEmail() : ""%>">
                            </div>

                            <div class="detail-label">Address</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientAddress"
                                       value="<%= patient.getAddress() != null ? patient.getAddress() : ""%>">
                            </div>

                            <div class="detail-label">Emergency Contact Name</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientEcName"
                                       value="<%= patient.getEmergencyContactFullName() != null ? patient.getEmergencyContactFullName() : ""%>">
                            </div>

                            <div class="detail-label">Emergency Contact Phone</div>
                            <div class="detail-value">
                                <input class="records-filter-input" name="patientEcPhone"
                                       value="<%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : ""%>">
                            </div>
                        </div>
                        <% } %>
                        <div class="btn-row">
                            <button type="submit" class="btn-action btn-reschedule">Save</button>
                            <a class="btn-action btn-back" href="#" onclick="return toggleEdit();">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
            <% }%>
        </div>
    </body>
</html>
