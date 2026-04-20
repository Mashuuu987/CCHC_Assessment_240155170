<%-- 
    Document   : login
    Created on : 2026/04/07, 21:13:43
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" />
    </head>
    <body>
        <c:set var="enteredUsername" value="${empty enteredUsername ? '' : enteredUsername}" />
        <%@ include file="/heading.jsp" %>
        <div class="login-wrapper">
            <div class="login-card">
                <div class="login-title">Sign in to CCHC</div>

                <c:if test="${not empty registerSuccess}">
                    <div class="register-success">
                        <c:out value="${registerSuccess}" />
                    </div>
                </c:if>
                <c:if test="${not empty loginError}">
                    <div class="login-error">
                        <c:out value="${loginError}" />
                    </div>
                </c:if>


                <form method="post" action="${pageContext.request.contextPath}/Login">
                    <input type="hidden" name="action" value="authenticate" />

                    <div class="login-section">
                        <label class="field-label" for="username">Username</label>
                        <input class="field-input" type="text" id="username" name="username" maxlength="50" value="${enteredUsername}" />
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
                    <form method="get" action="${pageContext.request.contextPath}/PatientRegister">
                        <input type="submit" class="btn-primary" value="Register as Patient" />
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
