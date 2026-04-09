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
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f7fb;
                margin: 0;
            }

            .register-wrapper {
                display: flex;
                justify-content: center;
                align-items: flex-start;
                padding-top: 40px;
            }

            .register-card {
                background-color: #ffffff;
                padding: 24px 32px;
                border-radius: 8px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.08);
                width: 520px;
            }

            .register-title {
                font-size: 20px;
                font-weight: 600;
                margin-bottom: 16px;
                color: #1f3d5a;
                text-align: center;
            }

            .field-group {
                margin-bottom: 12px;
            }

            .field-label {
                display: block;
                margin-bottom: 4px;
                font-size: 14px;
                color: #444;
            }

            .field-input {
                width: 100%;
                padding: 8px 10px;
                border-radius: 4px;
                border: 1px solid #ccd4e0;
                font-size: 14px;
                box-sizing: border-box;
            }

            .btn-primary {
                padding: 10px 0;
                width: 100%;
                border-radius: 4px;
                border: none;
                background-color: #1982c4;
                color: #fff;
                font-size: 15px;
                cursor: pointer;
            }

            .btn-primary:hover {
                opacity: 0.95;
            }
        </style>
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
                <div style="color:#c0392b;font-size:13px;margin-bottom:12px;text-align:center;">
                    <%= registerError%>
                </div>
                <% }%>

                <form method="post" action="<%= request.getContextPath()%>/PatientRegisterController">
                    <div class="field-group">
                        <label class="field-label" for="username">Username</label>
                        <input class="field-input" type="text" id="username" name="username"  value="<%= rUsername != null ? rUsername : "" %>"required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="password">Password</label>
                        <input class="field-input" type="password" id="password" name="password" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="confirmPassword">Confirm password</label>
                        <input class="field-input" type="password" id="confirmPassword" name="confirmPassword" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="hkid">HKID / ID number</label>
                        <input class="field-input" type="text" id="hkid" name="hkid" value="<%= rHkid != null ? rHkid : "" %>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="firstName">First name</label>
                        <input class="field-input" type="text" id="firstName" name="firstName" value="<%= rFirstName != null ? rFirstName : "" %>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="lastName">Last name</label>
                        <input class="field-input" type="text" id="lastName" name="lastName" value="<%= rLastName != null ? rLastName : "" %>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="gender">Gender</label>
                        <select class="field-input" id="gender" name="gender" required>
                            <option value="">-- Please select --</option>
                            <option value="M" <%= "M".equals(rGender) ? "selected" : "" %> >Male</option>
                            <option value="F" <%= "F".equals(rGender) ? "selected" : "" %> >Female</option>
                        </select>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="dob">Date of birth</label>
                        <input class="field-input" type="date" id="dob" name="dob" value="<%= rDob != null ? rDob : "" %>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="phone">Phone</label>
                        <input class="field-input" type="text" id="phone" name="phone" value="<%= rPhone != null ? rPhone : "" %>" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="email">Email (optional)</label>
                        <input class="field-input" type="email" id="email" name="email" value="<%= rEmail != null ? rEmail : "" %>" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="address">Address (optional)</label>
                        <textarea class="field-input" id="address" name="address" rows="2" value="<%= rAddress != null ? rAddress : "" %>" ></textarea>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContactFullName">Emergency contact full name (optional)</label>
                        <input class="field-input" type="text" id="emergencyContactFullName" name="emergencyContactFullName" value="<%= rEcName != null ? rEcName : "" %>" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContact">Emergency contact phone (optional)</label>
                        <input class="field-input" type="text" id="emergencyContact" name="emergencyContact" value="<%= rEcPhone != null ? rEcPhone : "" %>" >
                    </div>

                    <div class="field-group">
                        <input type="submit" class="btn-primary" value="Register">
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
