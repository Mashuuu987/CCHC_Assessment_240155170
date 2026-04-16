<%-- 
    Document   : ClinicAndServiceInfomation
    Created on : 2026/04/16, 2:22:51
    Author     : amzte
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
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

            Set<String> districtsSet = new HashSet<>();
            Set<String> serviceTypesSet = new HashSet<>();

            if (clinics != null) {
                for (ClinicBean clinic : clinics) {
                    if (clinic.getDistrict() != null && !clinic.getDistrict().isEmpty()) {
                        districtsSet.add(clinic.getDistrict());
                    }
                }
            }

            if (services != null) {
                for (ServiceBean service : services) {
                    if (service.getServiceType() != null && !service.getServiceType().isEmpty()) {
                        serviceTypesSet.add(service.getServiceType());
                    }
                }
            }

            List<String> districtsList = new ArrayList<>(districtsSet);
            districtsList.sort(null);

            List<String> serviceTypesList = new ArrayList<>(serviceTypesSet);
            serviceTypesList.sort(null);
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
                    Set<String> clinicServiceTypes = new HashSet<>();
                    if (capacities != null && services != null) {
                        for (ServiceCapacityBean cap : capacities) {
                            if (cap.getClinicId() == clinic.getClinicId()) {
                                for (ServiceBean s : services) {
                                    if (s.getServiceId() == cap.getServiceId()) {
                                        clinicServiceTypes.add(s.getServiceType());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    String serviceTypesStr = String.join(",", clinicServiceTypes);
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
