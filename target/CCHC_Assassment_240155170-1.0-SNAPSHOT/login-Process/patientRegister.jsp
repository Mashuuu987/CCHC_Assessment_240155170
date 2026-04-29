<%-- 
    Document   : patientRegister
    Created on : 2026/04/07, 21:47:09
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Patient Registration</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/patientRegister.css" />
    </head>
    <body>
        <%
            String registerError = (String) request.getAttribute("registerError");
            String rUsername = (String) request.getAttribute("username");
            String rHkid = (String) request.getAttribute("hkid");
            String rFirstName = (String) request.getAttribute("firstName");
            String rLastName = (String) request.getAttribute("lastName");
            String rGender = (String) request.getAttribute("gender");
            String rDob = (String) request.getAttribute("dob");
            String rPhone = (String) request.getAttribute("phone");
            String rEmail = (String) request.getAttribute("email");
            String rAddress = (String) request.getAttribute("address");
            String rEcName = (String) request.getAttribute("emergencyContactFullName");
            String rEcPhone = (String) request.getAttribute("emergencyContact");
        %>
        <%@ include file="/heading.jsp" %>

        <div class="register-wrapper">
            <div class="register-card">
                <div class="register-title">Patient Registration</div>

                <% if (registerError != null) {%>
                <div class="register-error">
                    <%= registerError%>
                </div>
                <% }%>

                <form method="post" action="<%= request.getContextPath()%>/PatientRegister">
                    <div class="field-group">
                        <label class="field-label" for="username">Username*</label>
                        <input class="field-input" type="text" id="username" name="username"  value="<%= rUsername != null ? rUsername : ""%>"required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="password">Password*</label>
                        <input class="field-input" type="password" id="password" name="password" placeholder="at least 6" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="confirmPassword">Confirm password*</label>
                        <input class="field-input" type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="hkid">HKID / ID number*</label>
                        <input class="field-input" type="text" id="hkid" name="hkid" value="<%= rHkid != null ? rHkid : ""%>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="firstName">First name*</label>
                        <input class="field-input" type="text" id="firstName" name="firstName" value="<%= rFirstName != null ? rFirstName : ""%>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="lastName">Last name*</label>
                        <input class="field-input" type="text" id="lastName" name="lastName" value="<%= rLastName != null ? rLastName : ""%>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="gender">Gender*</label>
                        <select class="field-input" id="gender" name="gender" required>
                            <option value="">-- Please select --</option>
                            <option value="M" <%= "M".equals(rGender) ? "selected" : ""%> >Male</option>
                            <option value="F" <%= "F".equals(rGender) ? "selected" : ""%> >Female</option>
                        </select>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="dob">Date of birth*</label>
                        <input class="field-input" type="date" id="dob" name="dob" value="<%= rDob != null ? rDob : ""%>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="phone">Phone*</label>
                        <input class="field-input" type="text" id="phone" name="phone" value="<%= rPhone != null ? rPhone : ""%>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="email">Email (optional)</label>
                        <input class="field-input" type="email" id="email" name="email" value="<%= rEmail != null ? rEmail : ""%>" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="address">Address (optional)</label>
                        <textarea class="field-input" id="address" name="address" rows="2" value="<%= rAddress != null ? rAddress : ""%>" ></textarea>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContactFullName">Emergency contact full name (optional)</label>
                        <input class="field-input" type="text" id="emergencyContactFullName" name="emergencyContactFullName" value="<%= rEcName != null ? rEcName : ""%>" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContact">Emergency contact phone (optional)</label>
                        <input class="field-input" type="text" id="emergencyContact" name="emergencyContact" value="<%= rEcPhone != null ? rEcPhone : ""%>" >
                    </div>

                    <div class="field-group">
                        <input type="submit" class="btn-primary" value="Register">
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
