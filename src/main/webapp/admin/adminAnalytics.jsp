<%-- 
    Document   : adminAnalytics
    Created on : 2026/04/25, 18:42:05
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Analytics &amp; Reports</title>

        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/adminAnalytics.css">
    </head>
    <body>
        <%@ include file="/heading.jsp" %>

        <%
            String ctx = request.getContextPath();
            String error = (String) request.getAttribute("error");

            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");

            String clinicSel = (String) request.getAttribute("filterClinicSel");
            String serviceSel = (String) request.getAttribute("filterServiceSel");
            String monthSel = (String) request.getAttribute("filterMonth");
            if (clinicSel == null) {
                clinicSel = "";
            }
            if (serviceSel == null) {
                serviceSel = "";
            }
            if (monthSel == null) {
                monthSel = "";
            }

            String clinicLabel = (String) request.getAttribute("resultClinicLabel");
            String serviceLabel = (String) request.getAttribute("resultServiceLabel");
            if (clinicLabel == null) {
                clinicLabel = "";
            }
            if (serviceLabel == null) {
                serviceLabel = "";
            }

            Long totalAvailable = (Long) request.getAttribute("totalAvailable");
            Integer bookedCount = (Integer) request.getAttribute("bookedCount");
            Integer noShowCount = (Integer) request.getAttribute("noShowCount");
            Double utilization = (Double) request.getAttribute("utilization");

            if (bookedCount == null) {
                bookedCount = 0;
            }
            if (noShowCount == null) {
                noShowCount = 0;
            }
            if (utilization == null) {
                utilization = 0.0;
            }

            String utilPct = String.format("%.1f%%", utilization * 100.0);

            List<Map<String, Object>> detailRows = (List<Map<String, Object>>) request.getAttribute("detailRows");
            if (detailRows == null)
                detailRows = new java.util.ArrayList<>();
        %>

        <div class="an-wrap">
            <div class="an-head">
                <div>
                    <h1 class="an-title">Analytics &amp; Reports</h1>
                </div>
                <div class="an-actions">
                    <a class="an-btn-back" href="<%= ctx%>/AdminDashboard">Back</a>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="an-alert an-alert-error"><%= error%></div>
            <% }%>

            <div class="an-card">
                <form method="get" action="<%= ctx%>/AdminAnalytics" class="an-form">
                    <div class="an-field">
                        <label class="an-label">Clinic</label>
                        <select class="an-select" name="clinicId" required>
                            <option value="" <%= clinicSel.isEmpty() ? "selected" : ""%>>Select clinic...</option>
                            <option value="ALL" <%= "ALL".equalsIgnoreCase(clinicSel) ? "selected" : ""%>>ALL</option>

                            <% if (clinics != null) {
                                    for (ClinicBean c : clinics) {
                                        String val = String.valueOf(c.getClinicId());
                                        boolean sel = val.equals(clinicSel);
                            %>
                            <option value="<%= val%>" <%= sel ? "selected" : ""%>><%= c.getClinicId()%> - <%= c.getName()%></option>
                            <%     }
                        }%>
                        </select>
                    </div>

                    <div class="an-field">
                        <label class="an-label">Service</label>
                        <select class="an-select" name="serviceId" required>
                            <option value="" <%= serviceSel.isEmpty() ? "selected" : ""%>>Select service...</option>
                            <option value="ALL" <%= "ALL".equalsIgnoreCase(serviceSel) ? "selected" : ""%>>ALL</option>

                            <% if (services != null) {
                                    for (ServiceBean s : services) {
                                        String val = String.valueOf(s.getServiceId());
                                        boolean sel = val.equals(serviceSel);
                            %>
                            <option value="<%= val%>" <%= sel ? "selected" : ""%>><%= s.getServiceId()%> - <%= s.getName()%></option>
                            <%     }
                        }%>
                        </select>
                    </div>

                    <div class="an-field">
                        <label class="an-label">Month</label>
                        <input class="an-input" type="month" name="month" value="<%= monthSel%>" required>
                    </div>

                    <div class="an-field an-field-btn">
                        <label class="an-label">&nbsp;</label>
                        <button type="submit" class="an-btn-primary">Generate</button>
                    </div>
                </form>
            </div>

            <% if (totalAvailable != null) {%>
            <div class="an-grid">
                <div class="an-metric">
                    <div class="an-metric-title">Utilization Rate</div>
                    <div class="an-metric-value"><%= utilPct%></div>
                    <div class="an-metric-desc">
                        <div><b><%= clinicLabel%></b></div>
                        <div><b><%= serviceLabel%></b></div>
                        <div>Month: <b><%= monthSel%></b></div>
                        <div style="margin-top:6px;">Booked: <b><%= bookedCount%></b> / Available: <b><%= totalAvailable%></b></div>
                    </div>
                </div>

                <div class="an-metric">
                    <div class="an-metric-title">No-show Count</div>
                    <div class="an-metric-value"><%= noShowCount%></div>
                    <div class="an-metric-desc">
                        <div><b><%= clinicLabel%></b></div>
                        <div><b><%= serviceLabel%></b></div>
                        <div>Month: <b><%= monthSel%></b></div>
                    </div>
                </div>
            </div>

            <div class="an-table-card">
                <h3 class="an-table-title">Summary Table</h3>

                <table class="an-table">
                    <thead>
                        <tr>
                            <th>Clinic</th>
                            <th>Service</th>
                            <th>Month</th>
                            <th>Total Available</th>
                            <th>Booked</th>
                            <th>Utilization</th>
                            <th>No-show</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><%= clinicLabel%></td>
                            <td><%= serviceLabel%></td>
                            <td><%= monthSel%></td>
                            <td><%= totalAvailable%></td>
                            <td><%= bookedCount%></td>
                            <td><%= utilPct%></td>
                            <td><%= noShowCount%></td>
                        </tr>

                        <% if ("ALL".equalsIgnoreCase(clinicSel) || "ALL".equalsIgnoreCase(serviceSel)) { %>
                        <% for (Map<String, Object> r : detailRows) {
                                String cName = (String) r.get("clinicName");
                                String sName = (String) r.get("serviceName");
                                Long avail = (Long) r.get("available");
                                Integer booked = (Integer) r.get("booked");
                                Integer noShow = (Integer) r.get("noShow");
                                Double util = (Double) r.get("utilization");

                                if (cName == null) {
                                    cName = "";
                                }
                                if (sName == null) {
                                    sName = "";
                                }
                                if (avail == null) {
                                    avail = 0L;
                                }
                                if (booked == null) {
                                    booked = 0;
                                }
                                if (noShow == null) {
                                    noShow = 0;
                                }
                                if (util == null) {
                                    util = 0.0;
                                }

                                String rowPct = String.format("%.1f%%", util * 100.0);
                        %>
                        <tr>
                            <td><%= cName%></td>
                            <td><%= sName%></td>
                            <td><%= monthSel%></td>
                            <td><%= avail%></td>
                            <td><%= booked%></td>
                            <td><%= rowPct%></td>
                            <td><%= noShow%></td>
                        </tr>
                        <% } %>
                        <% } %>

                    </tbody>
                </table>

                <% if (("ALL".equalsIgnoreCase(clinicSel) || "ALL".equalsIgnoreCase(serviceSel)) && detailRows.isEmpty()) { %>
                <div class="an-alert an-alert-error" style="margin-top:12px;">
                    No meaningful clinic-service rows found (no capacity and no appointments) for this selection.
                </div>
                <% } %>
            </div>
            <% }%>
        </div>
    </body>
</html>
