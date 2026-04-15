<%-- 
    Document   : ClinicAndServiceInfomation
    Created on : 2026/04/16, 2:22:51
    Author     : amzte
--%>
<%@ page import="java.util.List" %>
<%@ page import="ict.bean.ClinicBean, ict.bean.ServiceBean, ict.bean.ServiceCapacityBean" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Clinic and Service Information</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/clinicServiceInfomation.css">
    </head>
    <body>
        <%
            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            List<ServiceCapacityBean> capacities = (List<ServiceCapacityBean>) request.getAttribute("capacities");
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container">
            <h1 class="page-title">Clinic and Service Information</h1>

            <% if (clinics != null) {
                for (ClinicBean clinic : clinics) {%>

            <details class="clinic-card">
                <summary><%= clinic.getName()%></summary>
                <div class="clinic-details-body">
                    <div class="clinic-basic-info">
                        <p><strong>District: </strong><%= clinic.getDistrict()%></p>
                        <p><strong>Address: </strong><%= clinic.getAddress()%></p>
                        <p><strong>Opening hours: </strong><%= clinic.getOpenTime()%> - <%= clinic.getCloseTime()%></p>
                        <p><strong>Closed on: </strong><%= clinic.getCloseDay()%></p>
                    </div>
                    <table class="service-capacity-table">
                        <tr>
                            <th>Service Name</th>
                            <th>Type</th>
                            <th>Duration (mins)</th>
                            <th>Timeslot</th>
                            <th>Quota</th>
                        </tr>

                        <%
                            if (capacities != null) {
                                for (ServiceCapacityBean cap : capacities) {
                                    if (cap.getClinicId() == clinic.getClinicId()) {
                                        String serviceName = "";
                                        String serviceType = "";
                                        int durationMins = 0;

                                        if (services != null) {
                                            for (ServiceBean s : services) {
                                                if (s.getServiceId() == cap.getServiceId()) {
                                                    serviceName = s.getName();
                                                    serviceType = s.getServiceType();
                                                    durationMins = s.getDurationMins();
                                                    break;
                                                }
                                            }
                                        }
                        %>
                        <tr>
                            <td><%= serviceName%></td>
                            <td><%= serviceType%></td>
                            <td><%= durationMins%></td>
                            <td><%= cap.getTimeSlot()%></td>
                            <td><%= cap.getQuota()%></td>
                        </tr>
                        <%         }
                                }
                            }
                        %>
                    </table>
                </div>
            </details>
            <% }
        } else {%>
            <p>No clinic data.</p>
            <% }%>
        </div>
    </body>
</html>
