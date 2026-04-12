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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" />
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

                <form method="post" action="<%= request.getContextPath()%>/Login">
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
