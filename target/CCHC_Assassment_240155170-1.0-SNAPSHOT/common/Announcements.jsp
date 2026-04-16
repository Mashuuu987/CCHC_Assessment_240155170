<%-- 
    Document   : Announcements
    Created on : 2026/04/16, 3:37:18
    Author     : amzte
--%>

<%@page import="java.util.List"%>
<%@page import="ict.bean.AnnouncementsBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Announcements</title>
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/common.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/announcement.css">
        <script>
            function filterAnnouncements() {
                const typeValue = document.getElementById("annTypeFilter").value;
                const titleValue = document.getElementById("annTitleFilter").value.trim().toLowerCase();
                const items = document.querySelectorAll(".announcement-item");
                let visibleCount = 0;

                items.forEach(function(item) {
                    const itemType = (item.getAttribute("data-type") || "").toUpperCase();
                    const itemTitle = (item.getAttribute("data-title") || "").toLowerCase();

                    const typeMatched = typeValue === "ALL" || itemType === typeValue;
                    const titleMatched = titleValue === "" || itemTitle.indexOf(titleValue) !== -1;

                    if (typeMatched && titleMatched) {
                        item.style.display = "";
                        visibleCount++;
                    } else {
                        item.style.display = "none";
                    }
                });

                const emptyHint = document.getElementById("annSearchEmpty");
                if (emptyHint) {
                    emptyHint.style.display = visibleCount === 0 ? "block" : "none";
                }
            }

            function resetAnnouncementFilter() {
                document.getElementById("annTypeFilter").value = "ALL";
                document.getElementById("annTitleFilter").value = "";
                filterAnnouncements();
            }

            document.addEventListener("DOMContentLoaded", function() {
                const typeFilter = document.getElementById("annTypeFilter");
                const titleFilter = document.getElementById("annTitleFilter");

                if (typeFilter) {
                    typeFilter.addEventListener("change", filterAnnouncements);
                }
                if (titleFilter) {
                    titleFilter.addEventListener("input", filterAnnouncements);
                }
            });
        </script>
    </head>
    <body>
        <%
             List<AnnouncementsBean> list = (List<AnnouncementsBean>) request.getAttribute("announcements");
        %>
        <%@ include file="/heading.jsp" %>
        <div class="main-container">
            <h2 class="section-title">Announcements</h2>

            <div class="announcement-search-row">
                <select id="annTypeFilter" class="announcement-filter-select" aria-label="Filter announcement type">
                    <option value="ALL">All Types</option>
                    <option value="NORMAL">NORMAL</option>
                    <option value="IMPORTANT">IMPORTANT</option>
                    <option value="URGENT">URGENT</option>
                </select>
                <input id="annTitleFilter" class="announcement-filter-input" type="text" placeholder="Search title..." aria-label="Search announcement title" />
                <button type="button" class="announcement-filter-reset" onclick="resetAnnouncementFilter()">Reset</button>
            </div>

            <%
                if (list == null || list.isEmpty()) {
            %>
            <p>No announcements yet.</p>
            <%
            } else {
                for (AnnouncementsBean ann : list) {
                    String badgeClass = "notification-badge-normal";
                    if ("IMPORTANT".equalsIgnoreCase(ann.getType())) {
                        badgeClass = "notification-badge-important";
                    } else if ("URGENT".equalsIgnoreCase(ann.getType())) {
                        badgeClass = "notification-badge-urgent";
                    }
                    String timeText = ann.getPublishTime();
                    if (timeText == null || timeText.isEmpty() || "null".equalsIgnoreCase(timeText)) {
                        timeText = ann.getCreatedAt();
                    }
            %>
            <details class="announcement-item announcement-card" data-type="<%= ann.getType()%>" data-title="<%= ann.getTitle()%>">
                <summary class="announcement-summary">
                    <div class="announcement-summary-main">
                        <strong class="announcement-title"><%= ann.getTitle()%></strong>
                        <span class="announcement-time"><%= timeText%></span>
                    </div>
                    <span class="announcement-type-dot <%= badgeClass%>" title="<%= ann.getType()%>"><%= ann.getType()%></span>
                </summary>
                <div class="announcement-content">
                    <p class="announcement-text"><%= ann.getContent()%></p>
                </div>
            </details>
            <%
                    }
                }
            %>
            <p id="annSearchEmpty" class="announcement-search-empty" style="display:none;">No matching announcements.</p>
        </div>
    </body>
</html>
