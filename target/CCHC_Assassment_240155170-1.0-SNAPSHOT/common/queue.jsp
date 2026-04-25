<%-- 
    Document   : queue
    Created on : 2026/04/21
--%>
<%@page import="java.util.List"%>
<%@page import="ict.bean.QueueTicketBean"%>
<%@page import="ict.bean.QueueSettingBean"%>
<%@page import="ict.bean.ClinicBean"%>
<%@page import="ict.bean.ServiceBean"%>
<%@page import="ict.bean.PatientProfileBean"%>
<%@page import="ict.bean.StaffProfileBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Queue</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/queue.css">
        <script>
            function initQueueDetailPanel() {
                const clinicSelect = document.getElementById("clinicId");
                const serviceSelect = document.getElementById("serviceId");

                if (!clinicSelect || !serviceSelect) {
                    return;
                }

                const openCloseEl = document.getElementById("infoOpenClose");
                const closeDayEl = document.getElementById("infoCloseDay");
                const durationEl = document.getElementById("infoDuration");
                const queueOpenEl = document.getElementById("infoQueueOpen");
                const maxPerDayEl = document.getElementById("infoMaxPerDay");
                const settingMap = {};
                const settingItems = document.querySelectorAll(".queue-setting-data-item");

                settingItems.forEach(function (item) {
                    const key = item.getAttribute("data-key") || "";
                    settingMap[key] = {
                        openClose: item.getAttribute("data-open-close") || "-",
                        closeDay: item.getAttribute("data-close-day") || "-",
                        duration: item.getAttribute("data-duration") || "-",
                        queueOpen: item.getAttribute("data-queue-open") || "No",
                        maxPerDay: item.getAttribute("data-max-per-day") || "-"
                    };
                });

                function refreshDetail() {
                    const key = (clinicSelect.value || "") + "_" + (serviceSelect.value || "");
                    const item = settingMap[key];

                    if (!item) {
                        openCloseEl.textContent = "-";
                        closeDayEl.textContent = "-";
                        durationEl.textContent = "-";
                        queueOpenEl.textContent = "No";
                        maxPerDayEl.textContent = "-";
                        return;
                    }

                    openCloseEl.textContent = item.openClose;
                    closeDayEl.textContent = item.closeDay || "-";
                    durationEl.textContent = item.duration;
                    queueOpenEl.textContent = item.queueOpen;
                    maxPerDayEl.textContent = item.maxPerDay;
                }

                if (!clinicSelect.disabled) {
                    clinicSelect.addEventListener("change", refreshDetail);
                }
                serviceSelect.addEventListener("change", refreshDetail);
                refreshDetail();
            }

            document.addEventListener("DOMContentLoaded", initQueueDetailPanel);
        </script>
    </head>
    <body>
        <%
            String ctx = request.getContextPath();
            List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
            List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
            List<QueueSettingBean> queueSettings = (List<QueueSettingBean>) request.getAttribute("queueSettings");
            List<QueueTicketBean> selectedQueueTickets = (List<QueueTicketBean>) request.getAttribute("selectedQueueTickets");
            QueueTicketBean myTodayTicket = (QueueTicketBean) request.getAttribute("myTodayTicket");
            ClinicBean myTodayTicketClinic = (ClinicBean) request.getAttribute("myTodayTicketClinic");
            ServiceBean myTodayTicketService = (ServiceBean) request.getAttribute("myTodayTicketService");
            QueueTicketBean currentCalledTicket = (QueueTicketBean) request.getAttribute("currentCalledTicket");
            QueueSettingBean selectedSetting = (QueueSettingBean) request.getAttribute("selectedSetting");
            PatientProfileBean patient = (PatientProfileBean) request.getAttribute("patient");
            StaffProfileBean staff = (StaffProfileBean) request.getAttribute("staff");
            Boolean isPatient = (Boolean) request.getAttribute("isPatient");
            Boolean isStaff = (Boolean) request.getAttribute("isStaff");
            Integer selectedClinicId = (Integer) request.getAttribute("selectedClinicId");
            Integer selectedServiceId = (Integer) request.getAttribute("selectedServiceId");
            Integer currentCalledNumber = (Integer) request.getAttribute("currentCalledNumber");
            Integer myTodayEstimatedWaitMinutes = (Integer) request.getAttribute("myTodayEstimatedWaitMinutes");
            Integer myTodayWaitingAheadCount = (Integer) request.getAttribute("myTodayWaitingAheadCount");
            String today = (String) request.getAttribute("today");
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            boolean patientMode = isPatient != null && isPatient;
            boolean staffMode = isStaff != null && isStaff;
        %>
        <%@ include file="/heading.jsp" %>

        <div class="main-container queue-page">
            <div class="queue-hero">
                <div>
                    <p class="queue-kicker">Waiting Queue</p>
                    <h1 class="page-title"><%= patientMode ? "Join today's clinic queue" : "Manage today's clinic queue"%></h1>
                    <p class="queue-lead"><%= patientMode ? "Select a clinic and service, then take a ticket if the clinic is accepting queue patients now." : "Select a service for your clinic and manage the live queue flow."%></p>
                </div>
                <div class="queue-hero-stats">
                    <div class="queue-stat">
                        <span class="queue-stat-label">Date</span>
                        <span class="queue-stat-value"><%= today%></span>
                    </div>
                </div>
            </div>

            <% if (error != null) {%>
            <div class="queue-alert queue-alert-error"><%= error%></div>
            <% } %>
            <% if (success != null) {%>
            <div class="queue-alert queue-alert-success"><%= success%></div>
            <% }%>

            <div class="queue-layout">
                <div class="queue-panel">
                    <h2 class="section-title"><%= patientMode ? "Take a queue ticket" : "View current queue"%></h2>
                    <form class="queue-form" method="<%= patientMode ? "post" : "get"%>" action="<%= ctx%>/Queue">

                        <div class="queue-field">
                            <label class="queue-label">Clinic</label>
                            <select name="clinicId" class="queue-input" id="clinicId" <%= staffMode ? "disabled" : ""%>>
                                <% if (clinics != null) {
                                        for (ClinicBean clinic : clinics) {
                                            boolean selected = selectedClinicId != null && selectedClinicId == clinic.getClinicId();
                                %>
                                <option value="<%= clinic.getClinicId()%>" <%= selected ? "selected" : ""%>><%= clinic.getName()%></option>
                                <%     }
                                    } %>
                            </select>
                            <% if (staffMode && selectedClinicId != null) {%>
                            <input type="hidden" name="clinicId" value="<%= selectedClinicId%>" />
                            <% } %>
                        </div>

                        <div class="queue-field">
                            <label class="queue-label">Service</label>
                            <select name="serviceId" class="queue-input" id="serviceId">
                                <% if (services != null) {
                                        for (ServiceBean service : services) {
                                            boolean selected = selectedServiceId != null && selectedServiceId == service.getServiceId();
                                %>
                                <option value="<%= service.getServiceId()%>" <%= selected ? "selected" : ""%>><%= service.getName()%> (<%= service.getDurationMins()%> mins)</option>
                                <%     }
                                    }%>
                            </select>
                        </div>

                        <div class="queue-actions">
                            <% if (patientMode) { %>
                            <button type="submit" name="action" value="joinQueue" class="queue-btn queue-btn-primary">
                                Take queue ticket
                            </button>

                            <button type="submit" name="action" value="viewQueue" class="queue-btn">
                                View current queue
                            </button>
                            <% } else { %>
                            <button type="submit" class="queue-btn queue-btn-primary">
                                View current queue
                            </button>
                            <% }%>
                        </div>
                    </form>

                    <div class="queue-setting-box">
                        <div><strong>Clinic hours:</strong> <span id="infoOpenClose"><%= (selectedSetting != null) ? (selectedSetting.getClinicOpenTime() + " - " + selectedSetting.getClinicCloseTime()) : "-"%></span></div>
                        <div><strong>Closed on:</strong> <span id="infoCloseDay"><%= (selectedSetting != null && selectedSetting.getClinicCloseDay() != null && !selectedSetting.getClinicCloseDay().isEmpty()) ? selectedSetting.getClinicCloseDay() : "-"%></span></div>
                        <div><strong>Service time:</strong> <span id="infoDuration"><%= (selectedSetting != null) ? (selectedSetting.getDurationMins() + " mins") : "-"%></span></div>
                        <div><strong>Accepting queue:</strong> <span id="infoQueueOpen"><%= (selectedSetting != null && selectedSetting.isEnabled() && selectedSetting.isAllowIssueTicket()) ? "Yes" : "No"%></span></div>
                        <div><strong>Max queue per day:</strong> <span id="infoMaxPerDay"><%= (selectedSetting != null) ? (selectedSetting.getMaxTicketsPerDay() <= 0 ? "Unlimited" : String.valueOf(selectedSetting.getMaxTicketsPerDay())) : "-"%></span></div>
                    </div>
                </div>

                <div class="queue-panel">
                    <h2 class="section-title"><%= patientMode ? "Your current queue" : "Current called queue"%></h2>
                    <% if (patientMode) { %>
                    <% if (myTodayTicket == null) { %>
                    <div class="queue-empty">No ticket for today yet.</div>
                    <% } else {%>
                    <div class="queue-ticket-card">
                        <div class="queue-ticket-number">#<%= myTodayTicket.getQueueNumber()%></div>
                        <div><strong>Clinic:</strong> <%= (myTodayTicketClinic != null) ? myTodayTicketClinic.getName() : ("Clinic ID: " + myTodayTicket.getClinicId())%></div>
                        <div><strong>Service:</strong> <%= (myTodayTicketService != null) ? myTodayTicketService.getName() : ("Service ID: " + myTodayTicket.getServiceId())%></div>
                        <div><strong>Status:</strong> <%= myTodayTicket.getStatus()%></div>
                        <div><strong>Ticket date:</strong> <%= myTodayTicket.getQueueDate()%></div>
                        <div><strong>My Estimated wait:</strong> <%= myTodayEstimatedWaitMinutes != null ? myTodayEstimatedWaitMinutes : 0%> minutes</div>
                        <div><strong>People ahead (WAITING):</strong> <%= myTodayWaitingAheadCount != null ? myTodayWaitingAheadCount : 0%></div>
                        <div><strong>Now Ticket Estimated wait:</strong> <%= myTodayEstimatedWaitMinutes != null ? myTodayEstimatedWaitMinutes : 0%> minutes</div>

                    </div>
                    <% } %>
                    <% } else { %>
                    <% if (currentCalledTicket == null) { %>
                    <div class="queue-empty">Currently calling: none</div>
                    <% } else {%>
                    <div class="queue-ticket-card">
                        <div class="queue-ticket-number">#<%= currentCalledNumber != null ? currentCalledNumber : currentCalledTicket.getQueueNumber()%></div>
                        <div><strong>Status:</strong> <%= currentCalledTicket.getStatus()%></div>
                        <div><strong>Ticket date:</strong> <%= currentCalledTicket.getQueueDate()%></div>
                        <div><strong>Now calling:</strong> #<%= currentCalledTicket.getQueueNumber()%></div>
                    </div>
                    <% }%>

                    <div class="queue-actions queue-section-space">
                        <form method="post" action="<%= ctx%>/Queue" class="queue-inline-form">
                            <input type="hidden" name="action" value="callNext" />
                            <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : ""%>" />
                            <input type="hidden" name="serviceId" value="<%= selectedServiceId != null ? selectedServiceId : ""%>" />
                            <button type="submit" class="queue-btn queue-btn-primary">Next queue</button>
                        </form>
                        <form method="post" action="<%= ctx%>/Queue" class="queue-inline-form">
                            <input type="hidden" name="action" value="markServed" />
                            <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : ""%>" />
                            <input type="hidden" name="serviceId" value="<%= selectedServiceId != null ? selectedServiceId : ""%>" />
                            <button type="submit" class="queue-btn queue-btn-success">Mark served</button>
                        </form>
                        <form method="post" action="<%= ctx%>/Queue" class="queue-inline-form">
                            <input type="hidden" name="action" value="markSkipped" />
                            <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : ""%>" />
                            <input type="hidden" name="serviceId" value="<%= selectedServiceId != null ? selectedServiceId : ""%>" />
                            <button type="submit" class="queue-btn queue-btn-danger">Mark skipped</button>
                        </form>
                    </div>
                    <% } %>

                    <h2 class="section-title queue-section-space">Today's queue list</h2>
                    <div class="queue-table-wrap">
                        <table class="queue-table">
                            <thead>
                                <tr>
                                    <th>Ticket No.</th>
                                    <th>Status</th>
                                    <th>Take At</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (selectedQueueTickets
                                            == null || selectedQueueTickets.isEmpty()) { %>
                                <tr><td colspan="3" class="queue-empty-row">No queue ticket yet.</td></tr>
                                <% } else {
                                    for (QueueTicketBean ticket : selectedQueueTickets) {
                                %>
                                <tr>
                                    <td><%= ticket.getQueueNumber()%></td>
                                    <td><span class="queue-status status-<%= ticket.getStatus().toLowerCase()%>"><%= ticket.getStatus()%></span></td>
                                    <td><%= ticket.getCreatedAt()%></td>
                                </tr>
                                <%     }
                                    } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>


            <div class="queue-panel queue-section-space">
                <h2 class="section-title">Available queue rules</h2>
                <div class="clinic-list">
                    <% if (clinics != null) {
                            for (ClinicBean clinic : clinics) {

                                boolean clinicHasOpen = false;
                                boolean clinicHasAny = false;

                                if (queueSettings != null) {
                                    for (QueueSettingBean setting : queueSettings) {
                                        if (setting.getClinicId() == clinic.getClinicId()) {

                                            if (setting.isAllowIssueTicket()) {
                                                clinicHasAny = true;
                                            }

                                            if (setting.isEnabled() && setting.isAllowIssueTicket()) {
                                                clinicHasOpen = true;
                                            }
                                        }
                                    }
                                }
                    %>

                    <div class="clinic-card">
                        <div class="clinic-info-name"><%= clinic.getName()%></div>

                        <% if (queueSettings != null) {
                                for (QueueSettingBean setting : queueSettings) {
                                    if (setting.getClinicId() == clinic.getClinicId()
                                            && setting.isAllowIssueTicket()) {
                        %>
                        <div class="clinic-info-line">
                            <%= setting.getServiceName()%> (<%= setting.getDurationMins()%> mins)
                        </div>
                        <%         }
                                }
                            }
                        %>

                        <div class="clinic-info-line">
                            <span class="clinic-info-label">Open:</span>
                            <span><%= clinic.getOpenTime()%> - <%= clinic.getCloseTime()%></span>
                        </div>

                        <div class="clinic-info-line">
                            <span class="clinic-info-label">Closed:</span>
                            <span><%= clinic.getCloseDay()%></span>
                        </div>

                        <div class="clinic-info-line">
                            <span class="clinic-info-label">Queue:</span>
                            <span><%= (clinicHasAny && clinicHasOpen) ? "OPEN" : "CLOSED"%></span>
                        </div>
                    </div>

                    <%     }
                        }
                    %>
                </div>
            </div>
        </div>


        <div id="queueSettingData" style="display:none;">
            <% if (queueSettings
                        != null) {
                    for (QueueSettingBean setting : queueSettings) {
                        String key = setting.getClinicId() + "_" + setting.getServiceId();
                        String closeDay = setting.getClinicCloseDay() == null ? "-" : setting.getClinicCloseDay();
                        String openClose = String.valueOf(setting.getClinicOpenTime()) + " - " + String.valueOf(setting.getClinicCloseTime());
                        String duration = setting.getDurationMins() + " mins";
                        String maxPerDay = setting.getMaxTicketsPerDay() <= 0 ? "Unlimited" : String.valueOf(setting.getMaxTicketsPerDay());
                        String queueOpen = (setting.isEnabled() && setting.isAllowIssueTicket()) ? "Yes" : "No";
            %>
            <span class="queue-setting-data-item"
                  data-key="<%= key%>"
                  data-open-close="<%= openClose%>"
                  data-close-day="<%= closeDay%>"
                  data-duration="<%= duration%>"
                  data-queue-open="<%= queueOpen%>"
                  data-max-per-day="<%= maxPerDay%>"></span>
            <%     }
                    }%>
        </div>
    </body>
</html>
