<%-- 
    Document   : adminClinicCreate
    Created on : 2026/04/25, 21:44:31
    Author     : amzte
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Create Clinic</title>

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
                    <h1 class="cr-title">Create Clinic</h1>
                </div>
                <div class="cr-actions">
                    <a class="btn-action btn-back" href="<%= ctx%>/AdminClinicList">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="inc-alert inc-alert-error"><%= error%></div>
            <% }%>

            <div class="cr-card">
                <form method="post" action="<%= ctx%>/AdminClinicCreate"
                      onsubmit="return confirm('Create this clinic?');">

                    <div class="cr-row">
                        <div class="cr-field" style="flex:1;">
                            <label class="cr-label">Name *</label>
                            <input class="records-filter-input" type="text" name="name" required>
                        </div>
                    </div>

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">District</label>
                            <input class="records-filter-input" type="text" name="district">
                        </div>
                        <div class="cr-field" style="flex:1;">
                            <label class="cr-label">Address</label>
                            <input class="records-filter-input" type="text" name="address">
                        </div>
                    </div>

                    <div class="cr-row">
                        <div class="cr-field">
                            <label class="cr-label">Open Time</label>
                            <input class="records-filter-input" type="time" name="openTime">
                        </div>
                        <div class="cr-field">
                            <label class="cr-label">Close Time</label>
                            <input class="records-filter-input" type="time" name="closeTime">
                        </div>
                        <div class="cr-field">
                            <label class="cr-label">Close Day</label>
                            <select class="records-filter-select" name="closeDay">
                                <option value="">- None -</option>
                                <option value="Monday">Monday</option>
                                <option value="Tuesday">Tuesday</option>
                                <option value="Wednesday">Wednesday</option>
                                <option value="Thursday">Thursday</option>
                                <option value="Friday">Friday</option>
                                <option value="Saturday">Saturday</option>
                                <option value="Sunday">Sunday</option>
                            </select>
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