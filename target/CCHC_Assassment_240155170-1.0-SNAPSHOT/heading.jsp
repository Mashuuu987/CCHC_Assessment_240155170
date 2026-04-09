<%-- 
    Document   : heading
    Created on : 2026/04/07, 18:06:40
    Author     : amzte
--%>

<%@page import="ict.bean.UserInfoBean"%>
<style>
    .head-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 24px;
        border-radius: 8px;
        background-color: #f3f8ff;
        color: #1f3d5a;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }

    .head-title {
        color: steelblue;
        font-size: 14px;
        font-weight: 700;
        letter-spacing: 0.5px;
        display: flex;
        align-items: center;
        gap: 10px;
        text-decoration:none;
    }

    .head-logo {
        height: 50px;
        width: auto;
    }

    .head-actions {
        font-size: 14px;
    }
    .head-actions form {
        display: inline;
        margin: 0;
    }

    .btn {
        border-radius: 999px;
        padding: 6px 16px;
        border: 1px solid rgba(255,255,255,0.9);
        background-color: #1982c4;
        color: #fff;
        font-size: 13px;
        cursor: pointer;
        backdrop-filter: blur(4px);
    }
    .btn-outline {
        background-color: #fff;
        color: #6f42c1;
        border-color: #fff;
    }
    .btn:hover {
        opacity: 0.9;
    }
</style>

<ict:heading bgColor="#ffffff" width="100%">
    <div class="head-header">
        <a href="<%= request.getContextPath() %>/index.jsp" class="head-title">
            <img src="<%= request.getContextPath() %>/img/CCHC.png" class="head-logo" alt="CCHC Logo">
            CCHC Community Clinic Appointment &amp; Queue System
        </a>

        <div class="head-actions">
            <%
                UserInfoBean user = (UserInfoBean) session.getAttribute("userInfo");
                if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
                    String firstName = (String) session.getAttribute("displayFirstName");
                    String lastName = (String) session.getAttribute("displayLastName");
                    String displayName;
                    if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
                        displayName = firstName + " " + lastName + " - " + user.getUsername();
                    } else {
                        displayName = user.getUsername();
                    }
            %>
            Welcome, <%= displayName %>
            (<%= user.getRole()%>)
            &nbsp;|&nbsp;
            <form action="<%= request.getContextPath() %>/LoginController" method="post">
                <input type="hidden" name="action" value="logout" />
                <button type="submit" class="btn">Logout</button>
            </form>
            <%
            } else {
            %>
            <form action="<%= request.getContextPath() %>/LoginController" method="post">
                <input type="hidden" name="action" value="login" />
                <button type="submit" class="btn btn-outline">Login</button>
            </form>
            <%
                }
            %>
        </div>
    </div>
</ict:heading>
