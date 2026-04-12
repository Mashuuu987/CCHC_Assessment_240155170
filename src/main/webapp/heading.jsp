<%-- 
    Document   : heading
    Created on : 2026/04/07, 18:06:40
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="ict" uri="/WEB-INF/tlds/ict-taglib.tld" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/heading.css" />

<ict:heading bgColor="#ffffff" width="100%">
    <div class="head-header">
        <a href="${pageContext.request.contextPath}/index.jsp" class="head-title">
            <img src="${pageContext.request.contextPath}/img/CCHC.png" class="head-logo" alt="CCHC Logo">
            CCHC Community Clinic Appointment &amp; Queue System
        </a>

        <div class="head-actions">
            <c:set var="currentUser" value="${sessionScope.userInfo}" />
            <c:choose>
                <c:when test="${not empty currentUser and not empty currentUser.username}">
                    <c:set var="firstName" value="${sessionScope.displayFirstName}" />
                    <c:set var="lastName" value="${sessionScope.displayLastName}" />

                    <c:choose>
                        <c:when test="${not empty firstName and not empty lastName}">
                            Welcome,
                            ${firstName} ${lastName} - ${currentUser.username}
                        </c:when>
                        <c:otherwise>
                            Welcome, ${currentUser.username}
                        </c:otherwise>
                    </c:choose>
                    (${currentUser.role})
                    &nbsp;|&nbsp;
                    <form action="${pageContext.request.contextPath}/Login" method="post">
                        <input type="hidden" name="action" value="logout" />
                        <button type="submit" class="btn">Logout</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/Login" method="post">
                        <input type="hidden" name="action" value="login" />
                        <button type="submit" class="btn btn-outline">Login</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</ict:heading>
