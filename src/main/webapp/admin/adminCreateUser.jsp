<%-- 
    Document   : adminCreateUser
    Created on : 2026/04/24, 3:48:29
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create User</title>

        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminCreateUser.css">
    </head>

    <body>
        <%@ include file="/heading.jsp" %>

        <%!
            public String nv(java.util.Map<String, String> m, String k) {
                if (m == null) {
                    return "";
                }
                String s = m.get(k);
                return s == null ? "" : s;
            }
        %>

        <%
            String ctx = request.getContextPath();
            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            String selectedRole = (String) request.getAttribute("selectedRole");
            if (selectedRole == null) {
                selectedRole = "";
            }
            Map< String, String> formData = (Map<String, String>) request.getAttribute("formData");
            if (formData == null) {
                formData = new HashMap<>();
            }
        %>

        <div class="cr-wrap">
            <div class="cr-head">
                <div>
                    <h1 class="cr-title">Create User</h1>
                </div>
                <div class="cr-actions">
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminUserList">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% } %>

            <div class="cr-card">
                <% if (selectedRole.isBlank()) {%>
                <form method="post" action="<%= ctx%>/AdminCreateUser">
                    <input type="hidden" name="action" value="chooseRole">

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Role</label>
                            <select class="records-filter-select" name="role" required>
                                <option value="PATIENT">PATIENT</option>
                                <option value="STAFF">STAFF</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>
                    </div>

                    <div class="cr-actions-bottom">
                        <button type="submit" class="records-filter-reset">Next</button>
                    </div>
                </form>

                <% } else {%>

                <form method="post" action="<%= ctx%>/AdminCreateUser"
                      onsubmit="return confirm('Confirm create this user?');">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="roleLocked" value="<%= selectedRole%>">

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Role</label>
                            <div class="cr-readonly"><%= selectedRole%></div>
                        </div>

                        <div class="cr-field">
                            <label class="cr-label">Username</label>
                            <input class="records-filter-input" type="text" name="username" value="<%= nv(formData, "username")%>" required>
                        </div>

                        <div class="cr-field">
                            <label class="cr-label">Password</label>
                            <input class="records-filter-input" type="password" name="password" required>
                        </div>
                    </div>

                    <% if ("PATIENT".equalsIgnoreCase(selectedRole)) {%>
                    <div class="profile-box">
                        <h3 class="sec-title">Patient Profile</h3>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">HKID / ID</label>
                                <input class="records-filter-input" type="text" name="patientHKID" value="<%= nv(formData, "patientHKID")%>" required>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Phone</label>
                                <input class="records-filter-input" type="text" name="patientPhone" value="<%= nv(formData, "patientPhone")%>" required>
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">First Name</label>
                                <input class="records-filter-input" type="text" name="patientFirstName" value="<%= nv(formData, "patientFirstName")%>" required>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Last Name</label>
                                <input class="records-filter-input" type="text" name="patientLastName" value="<%= nv(formData, "patientLastName")%>" required>
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">Gender</label>
                                <select class="records-filter-select" name="patientGender" required>
                                    <option value="M" <%= "M".equalsIgnoreCase(nv(formData, "patientGender")) ? "selected" : ""%>>M</option>
                                    <option value="F" <%= "F".equalsIgnoreCase(nv(formData, "patientGender")) ? "selected" : ""%>>F</option>
                                </select>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">DOB</label>
                                <input class="records-filter-input" type="date" name="patientDOB" value="<%= nv(formData, "patientDOB")%>" required>
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">Email</label>
                                <input class="records-filter-input" type="email" name="patientEmail" value="<%= nv(formData, "patientEmail")%>">
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Address</label>
                                <input class="records-filter-input" type="text" name="patientAddress" value="<%= nv(formData, "patientAddress")%>">
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">Emergency Contact Name</label>
                                <input class="records-filter-input" type="text" name="patientEcName" value="<%= nv(formData, "patientEcName")%>">
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Emergency Contact Phone</label>
                                <input class="records-filter-input" type="text" name="patientEcPhone" value="<%= nv(formData, "patientEcPhone")%>">
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <% if ("STAFF".equalsIgnoreCase(selectedRole) || "ADMIN".equalsIgnoreCase(selectedRole)) {%>
                    <div class="profile-box">
                        <h3 class="sec-title">Staff Profile</h3>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">HKID / ID</label>
                                <input class="records-filter-input" type="text" name="staffHKID" value="<%= nv(formData, "staffHKID")%>" required>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Position</label>
                                <input class="records-filter-input" type="text" name="staffPosition" value="<%= nv(formData, "staffPosition")%>">
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">First Name</label>
                                <input class="records-filter-input" type="text" name="staffFirstName" value="<%= nv(formData, "staffFirstName")%>" required>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">Last Name</label>
                                <input class="records-filter-input" type="text" name="staffLastName" value="<%= nv(formData, "staffLastName")%>" required>
                            </div>
                        </div>

                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">Gender</label>
                                <select class="records-filter-select" name="staffGender" required>
                                    <option value="M" <%= "M".equalsIgnoreCase(nv(formData, "staffGender")) ? "selected" : ""%>>M</option>
                                    <option value="F" <%= "F".equalsIgnoreCase(nv(formData, "staffGender")) ? "selected" : ""%>>F</option>
                                </select>
                            </div>
                            <div class="cr-field">
                                <label class="cr-label">DOB</label>
                                <input class="records-filter-input" type="date" name="staffDOB" value="<%= nv(formData, "staffDOB")%>" required>
                            </div>
                        </div>

                        <% if ("STAFF".equalsIgnoreCase(selectedRole)) { %>
                        <div class="cr-row">
                            <div class="cr-field">
                                <label class="cr-label">Clinic</label>

                                <select class="records-filter-select" name="staffClinicId" required>
                                    <option value="">Select clinic...</option>
                                    <%
                                        String savedClinic = nv(formData, "staffClinicId");
                                        if (clinics != null) {
                                            for (ClinicBean c : clinics) {
                                                boolean sel = savedClinic.equals(String.valueOf(c.getClinicId()));
                                    %>
                                    <option value="<%= c.getClinicId()%>" <%= sel ? "selected" : ""%>>
                                        <%= c.getClinicId()%> - <%= c.getName()%>
                                    </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% }%>
                    <div class="cr-actions-bottom">
                        <button type="submit" class="records-filter-reset">Create</button>
                        <a class="btn-action btn-back" href="<%= ctx%>/AdminCreateUser?reset=1">Change Role</a>
                    </div>
                </form>
                <% }%>
            </div>
        </div>
    </body>
</html>