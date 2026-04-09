<%-- 
    Document   : login
    Created on : 2026/04/07, 21:13:43
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f7fb;
                margin: 0;
            }

            .login-wrapper {
                display: flex;
                justify-content: center;
                align-items: flex-start;
                padding-top: 40px;
            }

            .login-card {
                background-color: #ffffff;
                padding: 24px 32px;
                border-radius: 8px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.08);
                width: 420px;
            }

            .login-title {
                font-size: 20px;
                font-weight: 600;
                margin-bottom: 16px;
                color: #1f3d5a;
                text-align: center;
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

            .login-section {
                margin-bottom: 16px;
            }

            .role-options {
                margin-bottom: 12px;
                font-size: 14px;
                color: #444;
            }

            .btn-primary {
                width: 100%;
                padding: 10px 0;
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

            .register-box {
                margin-top: 20px;
                padding-top: 12px;
                border-top: 1px solid #e0e6f0;
                font-size: 14px;
            }

            .register-box h3 {
                margin: 0 0 8px 0;
                font-size: 16px;
                color: #1f3d5a;
            }

            .register-note {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
            }

            .login-error {
                color:#c0392b;
                font-size:13px;
                margin-bottom:12px;
                text-align:center;
            }
        </style>
        <script>
            function onRoleChange(radio) {
                var regBox = document.getElementById("registerBox");
                if (radio.value === "PATIENT") {
                    regBox.style.display = "block";
                } else {
                    regBox.style.display = "none";
                }
            }
        </script>
    </head>
    <body>
        <%
            String selectedRole = (String) request.getAttribute("selectedRole");
            if (selectedRole == null || selectedRole.isEmpty()) {
                selectedRole = "PATIENT";
            }
            String enteredUsername = (String) request.getAttribute("enteredUsername");
            if (enteredUsername == null) {
                enteredUsername = "";
            }
            String registerSuccess = (String)request.getAttribute("registerSuccess");
        %>
        <%@ include file="/heading.jsp" %>
        <div class="login-wrapper">
            <div class="login-card">
                <div class="login-title">Sign in to CCHC</div>

                <% 
                    if (registerSuccess != null) {
                %>
                <div style="color:#27ae60;font-size:13px;margin-bottom:12px;text-align:center;">
                    <%= registerSuccess %>
                </div>
                <%
                    }

                    String loginError = (String) request.getAttribute("loginError");
                    if (loginError != null) {
                %>
                <div class="login-error">
                    <%= loginError%>
                </div>
                <%
                    }
                %>

                <form method="post" action="<%= request.getContextPath()%>/LoginController">
                    <input type="hidden" name="action" value="authenticate" />

                    <div class="role-options">
                        <label>
                            <input type="radio" name="loginRole" value="PATIENT"
                                   onclick="onRoleChange(this)" <%= "PATIENT".equalsIgnoreCase(selectedRole) ? "checked" : ""%> >
                            Patient login
                        </label>
                        &nbsp;&nbsp;
                        <label>
                            <input type="radio" name="loginRole" value="STAFF"
                                   onclick="onRoleChange(this)" <%= "STAFF".equalsIgnoreCase(selectedRole) ? "checked" : ""%> >
                            Staff login
                        </label>
                    </div>

                    <div class="login-section">
                        <label class="field-label" for="username">Username</label>
                        <input class="field-input" type="text" id="username" name="username" maxlength="50" value="<%= enteredUsername%>" />
                    </div>

                    <div class="login-section">
                        <label class="field-label" for="password">Password</label>
                        <input class="field-input" type="password" id="password" name="password" maxlength="50" />
                    </div>

                    <div class="login-section">
                        <input type="submit" class="btn-primary" value="Login" />
                    </div>
                </form>

                <div id="registerBox" class="register-box">
                    <h3>New patient?</h3>
                    <div class="register-note">
                        Visitors need to register as patients in order to use the various functions.
                    </div>
                    <form method="get" action="login-Process/patientRegister.jsp">
                        <input type="submit" class="btn-primary" value="Register as Patient" />
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
