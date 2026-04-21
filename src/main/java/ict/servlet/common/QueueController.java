package ict.servlet.common;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ict.bean.ClinicBean;
import ict.bean.PatientProfileBean;
import ict.bean.QueueSettingBean;
import ict.bean.QueueTicketBean;
import ict.bean.ServiceBean;
import ict.bean.StaffProfileBean;
import ict.bean.UserInfoBean;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.QueueSettingDB;
import ict.db.QueueTicketDB;
import ict.db.ServiceDB;
import ict.db.StaffDB;
import ict.db.UserDB;
import ict.util.AppointmentNotificationUtil;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "QueueController", urlPatterns = {"/Queue"})
public class QueueController extends HttpServlet {

    private UserDB userDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private PatientDB patientDb;
    private StaffDB staffDb;
    private QueueTicketDB queueTicketDb;
    private QueueSettingDB queueSettingDb;
    private NotificationDB notifDb;
    private AppointmentNotificationUtil apptNotifUtil;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        staffDb = new StaffDB(dbUrl, dbUser, dbPassword);
        queueTicketDb = new QueueTicketDB(dbUrl, dbUser, dbPassword);
        queueSettingDb = new QueueSettingDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        apptNotifUtil = new AppointmentNotificationUtil(notifDb, clinicDb, serviceDb);
        userDb = new UserDB(dbUrl, dbUser,dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        boolean isPatient = UserCheckUtil.hasRole(user, "PATIENT");
        boolean isStaff = UserCheckUtil.hasRole(user, "STAFF");
        if (!isPatient && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        PatientProfileBean patient = null;
        StaffProfileBean staff = null;
        Integer staffClinicId = null;
        if (isPatient) {
            patient = patientDb.getPatientByUserId(user.getUserId());
        }
        if (isStaff) {
            staff = staffDb.getStaffByUserId(user.getUserId());
            if (staff == null || staff.getClinicId() == null) {
                request.setAttribute("error", "No clinic is linked to this staff account.");
                request.getRequestDispatcher("/patient/queue.jsp").forward(request, response);
                return;
            }
            staffClinicId = staff.getClinicId();
        }

        String clinicIdStr = request.getParameter("clinicId");
        String serviceIdStr = request.getParameter("serviceId");
        String error = (String) request.getAttribute("error");
        String success = (String) request.getAttribute("success");

        List<ClinicBean> clinics;
        if (isStaff) {
            ClinicBean staffClinic = clinicDb.getClinicByID(staffClinicId);
            if (staffClinic == null) {
                request.setAttribute("error", "Your assigned clinic cannot be found.");
                request.getRequestDispatcher("/patient/queue.jsp").forward(request, response);
                return;
            }
            clinics = java.util.Collections.singletonList(staffClinic);
        } else {
            clinics = clinicDb.getAllClinics();
        }
        List<ServiceBean> services = serviceDb.getAllServices();
        List<QueueSettingBean> settings = isStaff ? queueSettingDb.getQueueSettingsByClinicId(staffClinicId) : queueSettingDb.getAllQueueSettings();
        String today = LocalDate.now().toString();

        Integer selectedClinicId = null;
        Integer selectedServiceId = null;
        try {
            if (request.getAttribute("selectedClinicId") instanceof Integer) {
                selectedClinicId = (Integer) request.getAttribute("selectedClinicId");
            }
            if (request.getAttribute("selectedServiceId") instanceof Integer) {
                selectedServiceId = (Integer) request.getAttribute("selectedServiceId");
            }
            if (!isStaff && clinicIdStr != null && !clinicIdStr.isEmpty()) {
                selectedClinicId = Integer.valueOf(clinicIdStr);
            }
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                selectedServiceId = Integer.valueOf(serviceIdStr);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid clinic or service selection.");
        }

        if (isStaff) {
            selectedClinicId = staffClinicId;
        }

        if (selectedClinicId == null && !settings.isEmpty()) {
            selectedClinicId = settings.get(0).getClinicId();
        }
        if (selectedServiceId == null && !settings.isEmpty()) {
            selectedServiceId = settings.get(0).getServiceId();
        }

        ClinicBean selectedClinic = findClinicById(clinics, selectedClinicId);
        ServiceBean selectedService = findServiceById(services, selectedServiceId);
        QueueSettingBean selectedSetting = null;
        if (selectedClinicId != null && selectedServiceId != null) {
            selectedSetting = queueSettingDb.getQueueSetting(selectedClinicId, selectedServiceId);
        }

        List<QueueTicketBean> selectedQueueTickets = new ArrayList<>();
        QueueTicketBean myTodayTicket = null;
        ClinicBean myTodayTicketClinic = null;
        ServiceBean myTodayTicketService = null;
        QueueTicketBean currentCalledTicket = null;
        int currentCalledNumber = 0;
        int myTodayEstimatedWaitMinutes = 0;
        int estimatedWaitMinutes = 0;
        boolean canJoinQueue = false;

        if (selectedClinicId != null && selectedServiceId != null) {
            selectedQueueTickets = queueTicketDb.getTicketsByClinicServiceDate(selectedClinicId, selectedServiceId, today);
        }

        if (patient != null) {
            myTodayTicket = queueTicketDb.getLatestNonCalledTicketByPatientDate(patient.getPatientId(), today);
            if (myTodayTicket == null) {
                myTodayTicket = queueTicketDb.getLatestTicketByPatientDate(patient.getPatientId(), today);
            }
            if (myTodayTicket != null) {
                myTodayTicketClinic = clinicDb.getClinicByID(myTodayTicket.getClinicId());
                myTodayTicketService = serviceDb.getServiceById(myTodayTicket.getServiceId());
                if (myTodayTicketService != null) {
                    myTodayEstimatedWaitMinutes = Math.max(myTodayTicketService.getDurationMins(), 1) * Math.max(myTodayTicket.getQueueNumber() - 1, 0);
                }
            }
        }

        if (selectedClinicId != null && selectedServiceId != null) {
            currentCalledTicket = queueTicketDb.getCurrentCalledTicket(selectedClinicId, selectedServiceId, today);
            if (currentCalledTicket != null) {
                currentCalledNumber = currentCalledTicket.getQueueNumber();
            }
        }

        if (selectedClinicId != null && selectedServiceId != null) {
            if (selectedSetting != null && selectedSetting.isEnabled() && selectedSetting.isAllowIssueTicket()
                    && selectedClinic != null && selectedService != null) {
                boolean hasNonCalledTicket = patient != null && queueTicketDb.hasNonCalledTicketByPatientDate(patient.getPatientId(), today);
                canJoinQueue = isPatient && isQueueOpenToday(selectedClinic) && !hasNonCalledTicket;
                estimatedWaitMinutes = queueTicketDb.countTicketsByClinicServiceDate(selectedClinicId, selectedServiceId, today) * Math.max(selectedService.getDurationMins(), 1);
            }
        }

        request.setAttribute("clinics", clinics);
        request.setAttribute("services", services);
        request.setAttribute("queueSettings", settings);
        request.setAttribute("selectedClinicId", selectedClinicId);
        request.setAttribute("selectedServiceId", selectedServiceId);
        request.setAttribute("selectedClinic", selectedClinic);
        request.setAttribute("selectedService", selectedService);
        request.setAttribute("selectedSetting", selectedSetting);
        request.setAttribute("isPatient", isPatient);
        request.setAttribute("isStaff", isStaff);
        request.setAttribute("staff", staff);
        request.setAttribute("patient", patient);
        request.setAttribute("selectedQueueTickets", selectedQueueTickets);
        request.setAttribute("myTodayTicket", myTodayTicket);
        request.setAttribute("myTodayTicketClinic", myTodayTicketClinic);
        request.setAttribute("myTodayTicketService", myTodayTicketService);
        request.setAttribute("currentCalledTicket", currentCalledTicket);
        request.setAttribute("currentCalledNumber", currentCalledNumber);
        request.setAttribute("myTodayEstimatedWaitMinutes", myTodayEstimatedWaitMinutes);
        request.setAttribute("estimatedWaitMinutes", estimatedWaitMinutes);
        request.setAttribute("canJoinQueue", canJoinQueue);
        request.setAttribute("today", today);
        request.setAttribute("error", error);
        request.setAttribute("success", success);

        request.getRequestDispatcher("/common/queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = UserCheckUtil.getLoginUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }
        boolean isPatient = UserCheckUtil.hasRole(user, "PATIENT");
        boolean isStaff = UserCheckUtil.hasRole(user, "STAFF");
        if (!isPatient && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        String action = request.getParameter("action");
        if (isStaff && ("callNext".equals(action) || "markSkipped".equals(action) || "markServed".equals(action))) {
            handleStaffQueueAction(request, response, user, action);
            return;
        }

        if (!isPatient || !"joinQueue".equals(action)) {
            doGet(request, response);
            return;
        }

        String clinicIdStr = request.getParameter("clinicId");
        String serviceIdStr = request.getParameter("serviceId");
        String today = LocalDate.now().toString();

        Integer clinicId;
        Integer serviceId;
        try {
            clinicId = Integer.valueOf(clinicIdStr);
            serviceId = Integer.valueOf(serviceIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid clinic or service selection.");
            doGet(request, response);
            return;
        }

        request.setAttribute("selectedClinicId", clinicId);
        request.setAttribute("selectedServiceId", serviceId);

        PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
        if (patient == null) {
            request.setAttribute("error", "Patient profile not found.");
            doGet(request, response);
            return;
        }

        ClinicBean clinic = clinicDb.getClinicByID(clinicId);
        ServiceBean service = serviceDb.getServiceById(serviceId);
        QueueSettingBean setting = queueSettingDb.getQueueSetting(clinicId, serviceId);

        if (clinic == null || service == null || setting == null) {
            request.setAttribute("error", "Queue rule not found for selected clinic and service.");
            doGet(request, response);
            return;
        }

        if (!setting.isEnabled() || !setting.isAllowIssueTicket()) {
            request.setAttribute("error", "This clinic/service is not accepting queue tickets now.");
            doGet(request, response);
            return;
        }

        if (!isQueueOpenToday(clinic)) {
            request.setAttribute("error", "This clinic is closed today.");
            doGet(request, response);
            return;
        }

        if (queueTicketDb.hasNonCalledTicketByPatientDate(patient.getPatientId(), today)) {
            request.setAttribute("error", "You already have a queue ticket today. You can only take another ticket after your current ticket is called.");
            doGet(request, response);
            return;
        }

        int currentCount = queueTicketDb.countTicketsByClinicServiceDate(clinicId, serviceId, today);
        if (setting.getMaxTicketsPerDay() > 0 && currentCount >= setting.getMaxTicketsPerDay()) {
            request.setAttribute("error", "Today queue quota has been reached.");
            doGet(request, response);
            return;
        }

        int queueNumber = queueTicketDb.getNextQueueNumber(clinicId, serviceId, today);
        int ticketId = queueTicketDb.createQueueTicket(patient.getPatientId(), clinicId, serviceId, today, queueNumber, "WAITING");
        if (ticketId <= 0) {
            request.setAttribute("error", "Failed to create queue ticket.");
            doGet(request, response);
            return;
        }
        apptNotifUtil.notifyQueueTake(user.getUserId(), clinicId, serviceId, today, queueNumber);
        request.setAttribute("success", "Queue ticket created successfully. Your queue number is " + queueNumber + ".");
        doGet(request, response);
    }

    private void handleStaffQueueAction(HttpServletRequest request, HttpServletResponse response, UserInfoBean user, String action)
            throws ServletException, IOException {

        StaffProfileBean staff = staffDb.getStaffByUserId(user.getUserId());
        if (staff == null || staff.getClinicId() == null) {
            request.setAttribute("error", "No clinic is linked to this staff account.");
            doGet(request, response);
            return;
        }

        Integer clinicId;
        Integer serviceId;
        try {
            clinicId = Integer.valueOf(request.getParameter("clinicId"));
            serviceId = Integer.valueOf(request.getParameter("serviceId"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid clinic or service selection.");
            doGet(request, response);
            return;
        }

        if (!clinicId.equals(staff.getClinicId())) {
            request.setAttribute("error", "You can only manage queue for your own clinic.");
            doGet(request, response);
            return;
        }

        request.setAttribute("selectedClinicId", clinicId);
        request.setAttribute("selectedServiceId", serviceId);

        String today = LocalDate.now().toString();
        QueueTicketBean currentCalled = queueTicketDb.getCurrentCalledTicket(clinicId, serviceId, today);

        if ("callNext".equals(action)) {
            if (currentCalled != null) {
                request.setAttribute("error", "A queue number is already called. Mark it as skipped or served first.");
                doGet(request, response);
                return;
            }

            QueueTicketBean nextWaiting = queueTicketDb.getNextWaitingTicket(clinicId, serviceId, today);
            if (nextWaiting == null) {
                request.setAttribute("error", "No waiting queue ticket found.");
                doGet(request, response);
                return;
            }

            boolean updated = queueTicketDb.updateTicketStatus(nextWaiting.getTicketId(), "CALLED");
            request.setAttribute(updated ? "success" : "error", updated ? "Now calling queue #" + nextWaiting.getQueueNumber() + "." : "Failed to call the next queue number.");      
            UserInfoBean targetUser = userDb.getUserByPatientId(nextWaiting.getPatientId());
            apptNotifUtil.notifyQueueCalled(targetUser.getUserId(), clinicId, serviceId, today, nextWaiting.getQueueNumber());
            doGet(request, response);
            return;
        }

        if (currentCalled == null) {
            request.setAttribute("error", "No currently called queue number for this service.");
            doGet(request, response);
            return;
        }

        if ("markSkipped".equals(action)) {
            boolean updated = queueTicketDb.updateTicketStatus(currentCalled.getTicketId(), "SKIPPED");
            request.setAttribute(updated ? "success" : "error", updated ? "Queue #" + currentCalled.getQueueNumber() + " marked as skipped." : "Failed to mark queue as skipped.");
            UserInfoBean targetUser = userDb.getUserByPatientId(currentCalled.getPatientId());
            apptNotifUtil.notifyQueueSkipped(targetUser.getUserId(), clinicId, serviceId, today, currentCalled.getQueueNumber());
            doGet(request, response);
            return;
        }

        if ("markServed".equals(action)) {
            boolean updated = queueTicketDb.updateTicketStatus(currentCalled.getTicketId(), "SERVED");
            request.setAttribute(updated ? "success" : "error", updated ? "Queue #" + currentCalled.getQueueNumber() + " marked as served." : "Failed to mark queue as served.");
            UserInfoBean targetUser = userDb.getUserByPatientId(currentCalled.getPatientId());
            apptNotifUtil.notifyQueueServed(targetUser.getUserId(), clinicId, serviceId, today, currentCalled.getQueueNumber());
            doGet(request, response);
        }
    }

    private ClinicBean findClinicById(List<ClinicBean> clinics, Integer clinicId) {
        if (clinics == null || clinicId == null) {
            return null;
        }
        for (ClinicBean clinic : clinics) {
            if (clinic.getClinicId() == clinicId) {
                return clinic;
            }
        }
        return null;
    }

    private ServiceBean findServiceById(List<ServiceBean> services, Integer serviceId) {
        if (services == null || serviceId == null) {
            return null;
        }
        for (ServiceBean service : services) {
            if (service.getServiceId() == serviceId) {
                return service;
            }
        }
        return null;
    }

    private boolean isQueueOpenToday(ClinicBean clinic) {
        if (clinic == null) {
            return false;
        }
        String closeDay = clinic.getCloseDay();
        if (closeDay == null || closeDay.isBlank()) {
            return true;
        }
        String todayName = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return !todayName.equalsIgnoreCase(closeDay.trim());
    }
}
