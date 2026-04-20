<%-- 
    Document   : patientRegister
    Created on : 2026/04/07, 21:47:09
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Patient Registration</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/patientRegister.css" />
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <div class="register-wrapper">
            <div class="register-card">
                <div class="register-title">Patient Registration</div>

                <c:if test="${not empty registerError}">
                <div class="register-error">
                    <c:out value="${registerError}" />
                </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/PatientRegister">
                    <div class="field-group">
                        <label class="field-label" for="username">Username</label>
                        <input class="field-input" type="text" id="username" name="username"  value="${empty username ? '' : username}"required>
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
                        <input class="field-input" type="text" id="hkid" name="hkid" value="${empty hkid ? '' : hkid}" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="firstName">First name</label>
                        <input class="field-input" type="text" id="firstName" name="firstName" value="${empty firstName ? '' : firstName}" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="lastName">Last name</label>
                        <input class="field-input" type="text" id="lastName" name="lastName" value="${empty lastName ? '' : lastName}" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="gender">Gender</label>
                        <select class="field-input" id="gender" name="gender" required>
                            <option value="">-- Please select --</option>
                            <option value="M" ${gender eq "M" ? "selected" : ''}>Male</option>
                            <option value="F" ${gender eq "F" ? "selected" : ''}>Female</option>
                        </select>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="dob">Date of birth</label>
                        <input class="field-input" type="date" id="dob" name="dob" value="${empty dob ? '' : dob}" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="phone">Phone</label>
                        <input class="field-input" type="text" id="phone" name="phone" value="${empty phone ? '' : phone}" required>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="email">Email (optional)</label>
                        <input class="field-input" type="email" id="email" name="email" value="${empty email ? '' : email}" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="address">Address (optional)</label>
                        <textarea class="field-input" id="address" name="address" rows="2" value="${empty address ? '' : address}" ></textarea>
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContactFullName">Emergency contact full name (optional)</label>
                        <input class="field-input" type="text" id="emergencyContactFullName" name="emergencyContactFullName" value="${empty emergencyContactFullName ? '' : emergencyContactFullName}" >
                    </div>

                    <div class="field-group">
                        <label class="field-label" for="emergencyContact">Emergency contact phone (optional)</label>
                        <input class="field-input" type="text" id="emergencyContact" name="emergencyContact" value="${empty emergencyContact ? '' : emergencyContact}"> 
                    </div>

                    <div class="field-group">
                        <input type="submit" class="btn-primary" value="Register">
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
