<%-- 
    Document   : clinicAndServiceInfomation
    Created on : 2026/04/16, 2:22:51
    Author     : amzte
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="ict.bean.ClinicBean, ict.bean.ServiceBean, ict.bean.ServiceCapacityBean" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Clinic and Service Information</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/clinicServiceInfomation.css">
        <script>
            function filterClinics() {
                const selectedDistrict = document.getElementById("districtFilter").value;
                const selectedServiceType = document.getElementById("serviceTypeFilter").value;
                const clinicCards = document.querySelectorAll(".clinic-card");
                let visibleCount = 0;

                clinicCards.forEach(card => {
                    const clinicDistrict = card.getAttribute("data-district");
                    const hasServiceType = selectedServiceType === "" || 
                        card.getAttribute("data-service-types").includes(selectedServiceType);
                    const matchesDistrict = selectedDistrict === "" || clinicDistrict === selectedDistrict;

                    if (matchesDistrict && hasServiceType) {
                        card.style.display = "block";
                        visibleCount++;
                    } else {
                        card.style.display = "none";
                    }
                });

                const noResultsMsg = document.getElementById("noResultsMessage");
                if (visibleCount === 0) {
                    noResultsMsg.style.display = "block";
                } else {
                    noResultsMsg.style.display = "none";
                }
            }

            function resetFilters() {
                document.getElementById("districtFilter").value = "";
                document.getElementById("serviceTypeFilter").value = "";
                filterClinics();
            }

            document.addEventListener("DOMContentLoaded", function() {
                document.getElementById("districtFilter").addEventListener("change", filterClinics);
                document.getElementById("serviceTypeFilter").addEventListener("change", filterClinics);
            });
        </script>
    </head>
    <body>
        <%
            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            List<ServiceCapacityBean> capacities = (List<ServiceCapacityBean>) request.getAttribute("capacities");
            List<String> districtsList = (List<String>) request.getAttribute("districtsList");
            List<String> serviceTypesList = (List<String>) request.getAttribute("serviceTypesList");
            Map<Integer, ServiceBean> serviceById = (Map<Integer, ServiceBean>) request.getAttribute("serviceById");
            Map<Integer, List<ServiceCapacityBean>> capacitiesByClinic = (Map<Integer, List<ServiceCapacityBean>>) request.getAttribute("capacitiesByClinic");
            Map<Integer, String> clinicServiceTypesStrMap = (Map<Integer, String>) request.getAttribute("clinicServiceTypesStrMap");
        %>

        <%@ include file="/heading.jsp" %>

        <div class="main-container">
            <h1 class="page-title">Clinic and Service Information</h1>

            <div class="search-filter-section">
                <h2>Search & Filter</h2>
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="districtFilter">District:</label>
                        <select id="districtFilter">
                            <option value="">-- All Districts --</option>
                            <% for (String district : districtsList) { %>
                            <option value="<%= district %>"><%= district %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label for="serviceTypeFilter">Service Type:</label>
                        <select id="serviceTypeFilter">
                            <option value="">-- All Service Types --</option>
                            <% for (String serviceType : serviceTypesList) { %>
                            <option value="<%= serviceType %>"><%= serviceType %></option>
                            <% } %>
                        </select>
                    </div>

                    <div>
                        <button class="filter-button" onclick="resetFilters()">Reset</button>
                    </div>
                </div>
            </div>

            <div id="noResultsMessage" class="no-results-message" style="display: none;">
                No clinics found matching your criteria.
            </div>

            <% if (clinics != null) {
                for (ClinicBean clinic : clinics) {
                    String serviceTypesStr = clinicServiceTypesStrMap != null ? clinicServiceTypesStrMap.get(clinic.getClinicId()) : "";
                    if (serviceTypesStr == null) {
                        serviceTypesStr = "";
                    }
            %>

            <details class="clinic-card" data-district="<%= clinic.getDistrict() %>" data-service-types="<%= serviceTypesStr %>">
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
                            List<ServiceCapacityBean> clinicCaps = capacitiesByClinic != null ? capacitiesByClinic.get(clinic.getClinicId()) : null;

                            if (clinicCaps != null) {
                                for (ServiceCapacityBean cap : clinicCaps) {
                                    String serviceName = "";
                                    String serviceType = "";
                                    int durationMins = 0;

                                    ServiceBean s = serviceById != null ? serviceById.get(cap.getServiceId()) : null;
                                    if (s != null) {
                                        serviceName = s.getName();
                                        serviceType = s.getServiceType();
                                        durationMins = s.getDurationMins();
                                    }
                        %>
                        <tr>
                            <td><%= serviceName%></td>
                            <td><%= serviceType%></td>
                            <td><%= durationMins%></td>
                            <td><%= cap.getTimeSlot()%></td>
                            <td><%= cap.getQuota()%></td>
                        </tr>
                        <%      }
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
