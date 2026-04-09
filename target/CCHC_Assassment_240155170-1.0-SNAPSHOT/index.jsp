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
                   <h2 class="feature-card-title">預約服務</h2>
                   <p class="feature-card-text">預約門診或檢查時段，管理未來預約。</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">候診隊列</h2>
                   <p class="feature-card-text">查看現時候診隊列及自己的排隊號碼。</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">診所與服務資訊</h2>
                   <p class="feature-card-text">瀏覽各社區診所、開診時間及提供的服務。</p>
               </a>

               <a class="feature-card notification-card" href="<%= loggedIn ? ctx + "/NotificationController" : ctx + "/LoginController" %>">
                   <% if (notifUnreadCount > 0 && notifBadgeClass != null) { %>
                       <div class="notification-badge <%= notifBadgeClass %>"><%= notifUnreadCount %></div>
                   <% } %>
                   <h2 class="feature-card-title">通知中心</h2>
                   <p class="feature-card-text">查看系統發出的預約提醒及一般通知。</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">個人資料</h2>
                   <p class="feature-card-text">檢視及更新個人基本資料與聯絡方式。</p>
               </a>

               <a class="feature-card" href="<%= ctx + "/LoginController" %>">
                   <h2 class="feature-card-title">預約及就診紀錄</h2>
                   <p class="feature-card-text">查看過去預約及就診歷史，方便跟進。</p>
               </a>
           </div>

           <div class="section">
               <h2 class="section-title">公告與最新消息</h2>
               <p class="section-subtitle">以下為示範內容，日後可由通知功能自動顯示。</p>
               <ul class="announcement-list">
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-04-01 · 系統公告</div>
                       <p class="announcement-title">流感疫苗加開接種名額</p>
                       <p class="announcement-text">本月將於部分診所加開流感疫苗接種時段，請於「預約服務」中選擇相關服務。</p>
                   </li>
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-03-20 · 特別通告</div>
                       <p class="announcement-title">復活節期間診所開診安排</p>
                       <p class="announcement-text">復活節假期期間部分診所服務時間有所調整，建議預約前先查看診所開診時間。</p>
                   </li>
                   <li class="announcement-item">
                       <div class="announcement-meta">2026-03-05 · 系統維護</div>
                       <p class="announcement-title">系統維護通知</p>
                       <p class="announcement-text">系統將於每晚 02:00–03:00 進行例行維護，期間可能短暫影響登入及預約功能。</p>
                   </li>
               </ul>
           </div>

           <div class="section">
               <h2 class="section-title">如何使用本系統？</h2>
               <p class="section-subtitle">三個步驟完成預約與候診：</p>

               <div class="steps-grid">
                   <div class="step-card">
                       <div class="step-number">Step 1</div>
                       <h3 class="step-title">登入或註冊帳戶</h3>
                       <p class="step-text">使用你在診所登記的資料註冊新帳戶，或以現有帳戶登入。</p>
                   </div>

                   <div class="step-card">
                       <div class="step-number">Step 2</div>
                       <h3 class="step-title">選擇診所及服務</h3>
                       <p class="step-text">根據地區與需要選擇診所、醫生或醫療服務，並挑選合適時段。</p>
                   </div>

                   <div class="step-card">
                       <div class="step-number">Step 3</div>
                       <h3 class="step-title">確認預約與候診</h3>
                       <p class="step-text">提交預約後即可在系統查看預約詳情與候診隊列情況。</p>
                   </div>
               </div>
           </div>

           <div class="section">
               <h2 class="section-title">FAQ</h2>
               <p class="section-subtitle">以下明頁為示範問題，日後可以連結至詳細說。</p>
               <ul class="faq-list">
                   <li class="faq-item">
                       <p class="faq-question">Q：如果我忘記密碼怎麼辦？</p>
                       <p class="faq-answer">A：請向診所職員查詢或使用系統提供的重設密碼功能（完成後你可在此連結到相應頁面）。</p>
                   </li>
                   <li class="faq-item">
                       <p class="faq-question">Q：預約後可以更改時間或取消嗎？</p>
                       <p class="faq-answer">A：可以，登入後到「預約及就診紀錄」中選擇想更改的預約並按「改期」或「取消」。</p>
                   </li>
                   <li class="faq-item">
                       <p class="faq-question">Q：候診隊列是即時更新的嗎？</p>
                       <p class="faq-answer">A：系統會定期更新候診資訊，實際叫號仍以診所現場顯示及廣播為準。</p>
                   </li>
               </ul>
           </div>

           <div class="footer">
               Community Clinic Health Center © 2026 · 240155170 Hui Ho Fung Matthew.
           </div>
       </div>
    </body>
</html>
