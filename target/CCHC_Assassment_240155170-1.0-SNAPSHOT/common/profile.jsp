<%-- 
    Document   : profile
    Created on : 2026/04/13, 5:00:42
    Author     : amzte
--%>

<%@page import="ict.bean.PatientProfileBean, ict.bean.StaffProfileBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/profile.css">
    </head>
    <body>
        <%
            String ctx = request.getContextPath();

            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patientProfile");
            StaffProfileBean staff = (StaffProfileBean) request.getAttribute("staffProfile");
            String message = (String) request.getAttribute("message");
            Boolean isPatient = (Boolean) request.getAttribute("isPatient");
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container">
            <h1 class="page-title">Profile</h1>

            <% if (message != null) {%>
            <div class="profile-message"><%= message%></div>
            <% } %>

            <% if (Boolean.TRUE.equals(isPatient) && patient != null) {%>
            <form method="post" action="<%= ctx%>/Profile" class="profile-form">
                <h2 class="profile-title">Patient Profile</h2>

                <div class="profile-row">
                    <label class="profile-label">Patient ID</label>
                    <input class="profile-input" type="text" name="pid"
                           value="<%= patient.getPatientId()%>" readonly />
                </div>

                <div class="profile-row">
                    <label class="profile-label">HKID / ID</label>
                    <input class="profile-input" type="text" name="hkid"
                           value="<%= patient.getHKID()%>" readonly />
                </div>

                <div class="profile-row">
                    <label class="profile-label">First Name</label>
                    <input class="profile-input" type="text" name="firstName"
                           value="<%= patient.getFirstName()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Last Name</label>
                    <input class="profile-input" type="text" name="lastName"
                           value="<%= patient.getLastName()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Gender</label>
                    <div class="profile-radio-group">
                        <label>
                            <input type="radio" name="gender" value="M"
                                   <%= "M".equalsIgnoreCase(patient.getGender()) ? "checked" : ""%> />
                            Male
                        </label>
                        <label>
                            <input type="radio" name="gender" value="F"
                                   <%= "F".equalsIgnoreCase(patient.getGender()) ? "checked" : ""%> />
                            Female
                        </label>
                    </div>
                </div>

                <div class="profile-row">
                    <label class="profile-label">Date of Birth</label>
                    <input class="profile-input" type="date" name="dob"
                           value="<%= patient.getDOB()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Phone</label>
                    <input class="profile-input" type="text" name="phone"
                           value="<%= patient.getPhone()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Email</label>
                    <input class="profile-input" type="email" name="email"
                           value="<%= patient.getEmail()%>" />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Address</label>
                    <textarea class="profile-textarea" name="address" rows="3"><%= patient.getAddress() == null ? "" : patient.getAddress()%></textarea>
                </div>

                <div class="profile-row">
                    <label class="profile-label">Emergency Contact Name</label>
                    <input class="profile-input" type="text" name="emergencyContactFullName"
                           value="<%= patient.getEmergencyContactFullName() == null ? "" : patient.getEmergencyContactFullName()%>" />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Emergency Contact Phone</label>
                    <input class="profile-input" type="text" name="emergencyContact"
                           value="<%= patient.getEmergencyContact() == null ? "" : patient.getEmergencyContact()%>" />
                </div>

                <div class="profile-actions">
                    <button type="submit" class="btn-primary">Save Changes</button>

                </div>
                <div class="profile-actions">
                    <button type="button" class="btn-back" onclick="window.location.href = '<%= ctx%>/Settings';">
                        Back to settings
                    </button>
                </div>
            </form>
            <% } else if (staff != null) {%>
            <form method="post" action="<%= ctx%>/Profile" class="profile-form">
                <h2 class="profile-title">Staff Profile</h2>

                <div class="profile-row">
                    <label class="profile-label">Staff ID</label>
                    <input class="profile-input" type="text" name="sid"
                           value="<%= staff.getStaffId()%>" readonly />
                </div>

                <div class="profile-row">
                    <label class="profile-label">HKID / ID</label>
                    <input class="profile-input" type="text" name="hkid"
                           value="<%= staff.getHKID()%>" readonly />
                </div>

                <div class="profile-row">
                    <label class="profile-label">First Name</label>
                    <input class="profile-input" type="text" name="firstName"
                           value="<%= staff.getFirstName()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Last Name</label>
                    <input class="profile-input" type="text" name="lastName"
                           value="<%= staff.getLastName()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Gender</label>
                    <div class="profile-radio-group">
                        <label>
                            <input type="radio" name="gender" value="M"
                                   <%= "M".equalsIgnoreCase(staff.getGender()) ? "checked" : ""%> />
                            Male
                        </label>
                        <label>
                            <input type="radio" name="gender" value="F"
                                   <%= "F".equalsIgnoreCase(staff.getGender()) ? "checked" : ""%> />
                            Female
                        </label>
                    </div>
                </div>

                <div class="profile-row">
                    <label class="profile-label">Date of Birth</label>
                    <input class="profile-input" type="date" name="dob"
                           value="<%= staff.getDOB()%>" required />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Clinic ID (optional)</label>
                    <input class="profile-input" type="text" name="clinicId"
                           value="<%= staff.getClinicId() == null ? "" : staff.getClinicId().toString()%>" />
                </div>

                <div class="profile-row">
                    <label class="profile-label">Position</label>
                    <input class="profile-input" type="text" name="position"
                           value="<%= staff.getPosition() == null ? "" : staff.getPosition()%>" />
                </div>

                <div class="profile-actions">
                    <button type="submit" class="btn-primary">Save Changes</button>
                    <button type="button" class="btn-back" onclick="window.location.href = '<%= ctx%>/Settings';">
                        Back to settings
                    </button>
                </div>
            </form>

            <% } else { %>
            <p>No profile data found.</p>
            <% }%>
        </div>
    </body>
</html>
