<%-- 
    Document   : index
    Created on : 2026/04/07, 19:25:16
    Author     : amzte
--%>
<%@page import="ict.bean.UserInfoBean, ict.db.NotificationDB, ict.bean.NotificationBean, java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>CCHC</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/index.css">
    </head>
    <body>
       <%
           UserInfoBean user = (UserInfoBean) session.getAttribute("userInfo");
           boolean loggedIn = (user != null);
           String role = loggedIn ? user.getRole() : null;
           boolean isPatient = "PATIENT".equalsIgnoreCase(role);
           String ctx = request.getContextPath();

                int notifUnreadCount = 0;
                String notifBadgeClass = null;

                if (loggedIn) {
                    try {
                        String dbUrl = application.getInitParameter("dbUrl");
                        String dbUser = application.getInitParameter("dbUser");
                        String dbPassword = application.getInitParameter("dbPassword");

                        NotificationDB notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
                        List<NotificationBean> notifList = notifDb.getNotificationsByUserId(user.getUserId());

                        boolean hasUrgent = false;
                        boolean hasImportant = false;

                        for (NotificationBean n : notifList) {
                            if (n != null && !n.isRead()) {
                                notifUnreadCount++;
                                String t = n.getType();
                                if (t != null) {
                                    String upper = t.toUpperCase();
                                    if ("URGENT".equals(upper)) {
                                        hasUrgent = true;
                                    } else if ("IMPORTANT".equals(upper)) {
                                        hasImportant = true;
                                    }
                                }
                            }
                        }

                        if (notifUnreadCount > 0) {
                            notifBadgeClass = "notification-badge-normal";
                            if (hasImportant) {
                                notifBadgeClass = "notification-badge-important";
                            }
                            if (hasUrgent) {
                                notifBadgeClass = "notification-badge-urgent";
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
       %>
       <%@ include file="/heading.jsp" %>
       <div class="main-container">
           <div class="hero">
               <h1 class="hero-title">Community Clinic Health Center</h1>
               <p class="hero-subtitle">A super online service platform for community clinics 
                that helps you more easily book outpatient appointments, track waiting/queue status, 
                and follow up on health records.</p>
               <div class="hero-badges">
                   <span class="hero-badge">Quick online appointments</span>
                   <span class="hero-badge">Real-time waiting queue</span>
                   <span class="hero-badge">Appointment and visit records</span>
               </div>
           </div>

           <h2 class="page-title">Patient feature overview</h2>
           <p class="page-subtitle">Please choose commonly used patient features. (Some function need to login first)</p>

           <div class="feature-grid">
               <a class="feature-card" href="<%= isPatient ? ctx + "/PatientHomeController" : ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">Appointment Service</h2>
                   <p class="feature-card-text">Schedule appointments for outpatient visits or examinations, and manage future appointments.</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">Waiting Queue</h2>
                   <p class="feature-card-text">View the current waiting list and your own queue number.</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">Clinic and Service Information</h2>
                   <p class="feature-card-text">Browse the various community clinics, their opening hours, and the services they offer.</p>
               </a>

               <a class="feature-card notification-card" href="<%= loggedIn ? ctx + "/NotificationController" : ctx + "/LoginController" %>">
                   <% if (notifUnreadCount > 0 && notifBadgeClass != null) { %>
                       <div class="notification-badge <%= notifBadgeClass %>"><%= notifUnreadCount %></div>
                   <% } %>
                   <h2 class="feature-card-title">Notification Center</h2>
                   <p class="feature-card-text">View appointment reminders and general notifications sent by the system.</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">Personal information</h2>
                   <p class="feature-card-text">Review and update your personal information and contact details.</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">Appointment and medical records</h2>
                   <p class="feature-card-text">Viewing past appointments and medical records makes it easier to follow up.</p>
               </a>
           </div>

           <div class="section">
               <h2 class="section-title">Announcements and Latest News</h2>
               <p class="section-subtitle">Click to view details.</p>
               <ul class="announcement-list">
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-04-01 - System Announcement</div>
                       <p class="announcement-title">System Announcement!</p>
                       <p class="announcement-text">Hello ! System Announcement !</p>
                   </li>
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-03-20 - Special Announcement</div>
                       <p class="announcement-title">System Maintenance!</p>
                       <p class="announcement-text">Hello ! Special Announcement !</p>
                   </li>
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-03-05 - System Maintenance</div>
                       <p class="announcement-title">System Maintenance!</p>
                       <p class="announcement-text">Hello ! System Maintenance !</p>
                   </li>
               </ul>
           </div>

           <div class="section">
               <h2 class="section-title">How do I use this system?</h2>
               <p class="section-subtitle">Complete the appointment and waiting process in three steps:</p>

               <div class="steps-grid">
                   <div class="step-card">
                       <div class="step-number">Step 1</div>
                       <h3 class="step-title">Log in or register an account</h3>
                       <p class="step-text">Register a new account, or log in with an existing account.</p>
                   </div>

                   <div class="step-card">
                       <div class="step-number">Step 2</div>
                       <h3 class="step-title">Choosing a clinic and services</h3>
                       <p class="step-text">Choose a clinic and medical services based on your location and needs, and select a suitable time slot.</p>
                   </div>

                   <div class="step-card">
                       <div class="step-number">Step 3</div>
                       <h3 class="step-title">Confirm appointment and waiting time</h3>
                       <p class="step-text">After submitting your appointment, you can view the appointment details and waiting list status in the system.</p>
                   </div>
               </div>
           </div>

           <div class="section">
               <h2 class="section-title">FAQ</h2>
               <ul class="faq-list">
                   <li class="faq-item">
                       <p class="faq-question">Q：What if I forget my password?</p>
                       <p class="faq-answer">A：Please ask the clinic staff!</p>
                   </li>
                   <li class="faq-item">
                       <p class="faq-question">Q：Can I change the time or cancel my reservation?</p>
                       <p class="faq-answer">A：Yes, after logging in, go to "Appointments and Medical Records," select the appointment you want to change, and click "Reschedule" or "Cancel.".</p>
                   </li>
                   <li class="faq-item">
                       <p class="faq-question">Q：Is the waiting queue updated in real time?</p>
                       <p class="faq-answer">A：Yes, is a read time, but the actual number called will be based on th clinic's display screen.</p>
                   </li>
               </ul>
           </div>

           <div class="footer">
               Community Clinic Health Center © 2026 · 240155170 Hui Ho Fung Matthew.
           </div>
       </div>
    </body>
</html>
