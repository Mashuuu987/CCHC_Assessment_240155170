/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.common;

import java.io.IOException;
import java.util.List;

import ict.bean.AnnouncementsBean;
import ict.db.AnnouncementsDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AnnouncementsController", urlPatterns = {"/Announcements"})
public class AnnouncementsController extends HttpServlet {
    
    private AnnouncementsDB annDb;
    
    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        annDb = new AnnouncementsDB(dbUrl, dbUser, dbPassword);
        annDb.createAnnouncementTable();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<AnnouncementsBean> list = annDb.getLatestVisibleAnnouncements(20);
        request.setAttribute("announcements", list);
        request.getRequestDispatcher("/common/Announcements.jsp").forward(request, response);
    }
}
