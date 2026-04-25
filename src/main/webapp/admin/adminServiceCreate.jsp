<%-- 
    Document   : adminServiceCreate
    Created on : 2026/04/25, 21:52:59
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create Service</title>

        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminClinicCreate-adminServiceCreate.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
            String error = (String) request.getAttribute("error");
        %>

        <div class="cr-wrap">
            <div class="cr-head">
                <div>
                    <h1 class="cr-title">Create Service</h1>
                </div>
                <div class="cr-actions">
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminClinicList">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% }%>

            <div class="cr-card">
                <form method="post" action="<%= ctx%>/AdminServiceCreate"
                      onsubmit="return confirm('Create this service?');">

                    <div class="cr-row">
                        <div class="cr-field" style="flex:1;">
                            <label class="cr-label">Name *</label>
                            <input class="records-filter-input" type="text" name="name" required>
                        </div>
                    </div>

                    <div class="cr-row">
                        <div class="cr-field" style="flex:1;">
                            <label class="cr-label">Description</label>
                            <textarea class="records-filter-input" name="description" rows="5"></textarea>
                        </div>
                    </div>

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Service Type</label>
                            <select class="records-filter-select" name="serviceType">
                                <option value="CONSULTATION">CONSULTATION</option>
                                <option value="VACCINATION">VACCINATION</option>
                                <option value="SCREENING">SCREENING</option>
                            </select>
                        </div>

                        <div class="cr-field">
                            <label class="cr-label">Duration (mins) *</label>
                            <input class="records-filter-input" type="number" name="durationMins" min="1" required>
                        </div>
                    </div>

                    <div class="cr-actions-bottom">
                        <button type="submit" class="records-filter-reset">Create</button>
                    </div>
                </form>
            </div>
        </div>

    </body>
</html>