/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.patient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ict.bean.ClinicBean;
import ict.bean.PatientProfileBean;
import ict.bean.ServiceBean;
import ict.bean.ServiceCapacityBean;
import ict.bean.UserInfoBean;
import ict.db.AppointmentDB;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.PatientDB;
import ict.db.ServiceCapacityDB;
import ict.db.ServiceDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author amzte
 */
@WebServlet(name = "BookAppointmentController", urlPatterns = {"/BookAppointment"})
public class BookAppointmentController extends HttpServlet {

    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private PatientDB patientDb;
    private AppointmentDB apptDb;
    private ServiceCapacityDB capDb;
    private NotificationDB notifDb;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        capDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);

        capDb.insertDefaultCapacitiesIfEmpty();
    }

    private UserInfoBean getLoginPatient(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        UserInfoBean user = (session != null) ? (UserInfoBean) session.getAttribute("userInfo") : null;
        if (user == null || user.getRole() == null || !"PATIENT".equalsIgnoreCase(user.getRole())) {
            return null;
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = getLoginPatient(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        request.setAttribute("clinics", clinicDb.getAllClinics());
        request.setAttribute("services", serviceDb.getAllServices());

        request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
    }

    private boolean isPastTimeslotToday(String date, String timeSlot) {
        if (date == null || date.isEmpty() || timeSlot == null || timeSlot.isEmpty()) {
            return false;
        }
        try {
            LocalDate selectedDateLocal = LocalDate.parse(date);
            LocalDate today = LocalDate.now();
            if (!selectedDateLocal.isEqual(today)) {
                return false;
            }
            LocalTime nowTime = LocalTime.now();
            LocalTime slotTime = LocalTime.parse(timeSlot);
            return slotTime.isBefore(nowTime);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = getLoginPatient(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/PublicHome");
            return;
        }

        request.setAttribute("clinics", clinicDb.getAllClinics());
        request.setAttribute("services", serviceDb.getAllServices());

        String clinicIdStr = request.getParameter("clinicId");
        String serviceIdStr = request.getParameter("serviceId");
        String date = request.getParameter("appointmentDate");
        String timeSlot = request.getParameter("timeSlot");
        String step = request.getParameter("step");

        Integer clinicId = null;
        Integer serviceId = null;
        try {
            if (clinicIdStr != null && !clinicIdStr.isEmpty()) {
                clinicId = Integer.parseInt(clinicIdStr);
            }
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                serviceId = Integer.parseInt(serviceIdStr);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid clinic or service.");
            request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
            return;
        }

        request.setAttribute("selectedClinicId", clinicId);
        request.setAttribute("selectedServiceId", serviceId);
        request.setAttribute("selectedDate", date);

        // STEP 1:
        if (step == null || "1".equals(step)) {
            if (clinicId == null || serviceId == null || date == null || date.isEmpty()) {
                request.setAttribute("error", "Please select clinic, service and date.");
                request.setAttribute("currentStep", 1);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            try {
                LocalDate selectedDateLocal = LocalDate.parse(date);
                LocalDate today = LocalDate.now();
                if (selectedDateLocal.isBefore(today)) {
                    request.setAttribute("error", "Please select a date that is today or later.");
                    request.setAttribute("currentStep", 1);
                    request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                request.setAttribute("error", "Invalid date format.");
                request.setAttribute("currentStep", 1);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            List<ServiceCapacityBean> capList = capDb.getCapacityByClinicService(clinicId, serviceId);
            Set<String> fullSlots = new HashSet<>();

            if (capList != null) {
                for (ServiceCapacityBean sc : capList) {
                    int used = apptDb.countAppointments(clinicId, serviceId, date, sc.getTimeSlot());
                    if (used >= sc.getQuota()) {
                        fullSlots.add(sc.getTimeSlot());
                    }
                }
            }

            request.setAttribute("capacityList", capList);
            request.setAttribute("fullTimeSlots", fullSlots);
            request.setAttribute("currentStep", 2);
            request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
            return;
        }

        // STEP 2: 
        if ("2".equals(step)) {
            if (clinicId == null || serviceId == null || date == null || date.isEmpty()) {
                request.setAttribute("error", "Please select clinic, service and date.");
                request.setAttribute("currentStep", 1);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            java.util.List<ServiceCapacityBean> capList = capDb.getCapacityByClinicService(clinicId, serviceId);
            java.util.Set<String> fullSlots = new java.util.HashSet<>();

            if (capList != null) {
                for (ServiceCapacityBean sc : capList) {
                    int used = apptDb.countAppointments(clinicId, serviceId, date, sc.getTimeSlot());
                    if (used >= sc.getQuota()) {
                        fullSlots.add(sc.getTimeSlot());
                    }
                }
            }

            request.setAttribute("capacityList", capList);
            request.setAttribute("fullTimeSlots", fullSlots);

            if (isPastTimeslotToday(date, timeSlot)) {
                request.setAttribute("error", "Selected timeslot is already in the past. Please choose a later timeslot.");
                request.setAttribute("currentStep", 2);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            if (timeSlot == null || timeSlot.isEmpty()) {
                request.setAttribute("error", "Please select a timeslot.");
                request.setAttribute("currentStep", 2);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
            if (patient == null) {
                request.setAttribute("error", "Patient profile not found.");
                request.setAttribute("currentStep", 2);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            request.setAttribute("selectedTimeSlot", timeSlot);
            request.setAttribute("patient", patient);
            request.setAttribute("currentStep", 3);
            request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
            return;
        }

        // STEP 3:
        if ("3".equals(step)) {

            if (clinicId == null || serviceId == null || date == null || date.isEmpty()
                    || timeSlot == null || timeSlot.isEmpty()) {
                request.setAttribute("error", "Missing appointment information.");
                request.setAttribute("currentStep", 3);

                PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
                if (patient != null) {
                    request.setAttribute("patient", patient);
                }
                request.setAttribute("selectedTimeSlot", timeSlot);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            if (isPastTimeslotToday(date, timeSlot)) {
                request.setAttribute("error", "Selected timeslot is already in the past. Please go back and choose a later timeslot.");
                request.setAttribute("currentStep", 2);

                java.util.List<ServiceCapacityBean> capList = capDb.getCapacityByClinicService(clinicId, serviceId);
                java.util.Set<String> fullSlots = new HashSet<>();
                if (capList != null) {
                    for (ServiceCapacityBean sc : capList) {
                        int usedSlot = apptDb.countAppointments(clinicId, serviceId, date, sc.getTimeSlot());
                        if (usedSlot >= sc.getQuota()) {
                            fullSlots.add(sc.getTimeSlot());
                        }
                    }
                }
                request.setAttribute("capacityList", capList);
                request.setAttribute("fullTimeSlots", fullSlots);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            ServiceCapacityDB scDb = capDb;
            ServiceCapacityBean cap = scDb.getCapacity(clinicId, serviceId, timeSlot);
            if (cap == null) {
                request.setAttribute("error", "No capacity rule for selected slot.");
                request.setAttribute("currentStep", 3);

                PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
                if (patient != null) {
                    request.setAttribute("patient", patient);
                }
                request.setAttribute("selectedTimeSlot", timeSlot);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            int used = apptDb.countAppointments(clinicId, serviceId, date, timeSlot);
            if (used >= cap.getQuota()) {
                List<ServiceCapacityBean> capList = capDb.getCapacityByClinicService(clinicId, serviceId);
                Set<String> fullSlots = new HashSet<>();

                if (capList != null) {
                    for (ServiceCapacityBean sc : capList) {
                        int usedSlot = apptDb.countAppointments(clinicId, serviceId, date, sc.getTimeSlot());
                        if (usedSlot >= sc.getQuota()) {
                            fullSlots.add(sc.getTimeSlot());
                        }
                    }
                }

                request.setAttribute("capacityList", capList);
                request.setAttribute("fullTimeSlots", fullSlots);
                request.setAttribute("error", "Selected timeslot is full. Please choose another one.");
                request.setAttribute("currentStep", 2);
                request.setAttribute("selectedTimeSlot", timeSlot);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
            if (patient == null) {
                request.setAttribute("error", "Patient profile not found.");
                request.setAttribute("currentStep", 3);
                request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
                return;
            }

            int existingApptId = apptDb.findExistingAppointmentId(
                    patient.getPatientId(), clinicId, serviceId, date, timeSlot);

            if (existingApptId > 0) {
                request.setAttribute("error", "You already have an appointment for this slot (ID: "
                        + existingApptId + "). Please do not submit appointments repeatedly.");
            } else {
                int apptId = apptDb.createAppointment(patient.getPatientId(), clinicId, serviceId, date, timeSlot, "REQUESTED");
                if (apptId <= 0) {
                    request.setAttribute("error", "Failed to create appointment. Please try again.");
                } else {
                    request.setAttribute("success", "Appointment Requested (ID: " + apptId + "), pending confirmation. You will receive a notification once the appointment is confirmed or if there are any updates.");

                    ClinicBean clinic = (clinicId != null) ? clinicDb.getClinicByID(clinicId) : null;
                    ServiceBean service = (serviceId != null) ? serviceDb.getServiceById(serviceId) : null;

                    String clinicName = (clinic != null && clinic.getName() != null && !clinic.getName().isEmpty()) ? clinic.getName() : ("Clinic ID: " + clinicId);
                    String serviceName = (service != null && service.getName() != null && !service.getName().isEmpty()) ? service.getName() : ("Service ID: " + serviceId);

                    String title = "Appointment request submitted";
                    StringBuilder msg = new StringBuilder();
                    msg.append("Clinic: ").append(clinicName)
                            .append("\nService: ").append(serviceName)
                            .append("\nAppointment ID: ").append(apptId)
                            .append("\nDate: ").append(date)
                            .append("\nTimeslot: ").append(timeSlot)
                            .append("\nWe have received your appointment and will process it as soon as possible and reply to you via message.");

                    try {
                        notifDb.createNotification(user.getUserId(), "IMPORTANT", title, msg.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            request.setAttribute("selectedTimeSlot", timeSlot);
            request.setAttribute("patient", patient);
            request.setAttribute("currentStep", 3);
            request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/patient/bookAppointment.jsp").forward(request, response);
    }
}
