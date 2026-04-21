/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.servlet.patient;

import ict.bean.AppointmentBean;
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
import ict.util.AppointmentNotificationUtil;
import ict.util.UserCheckUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author amzte
 */
@WebServlet(name = "AppointmentRecordPatientActionController", urlPatterns = {"/AppointmentRecordPatientAction"})
public class AppointmentRecordPatientActionController extends HttpServlet {

    private AppointmentDB apptDb;
    private PatientDB patientDb;
    private ClinicDB clinicDb;
    private ServiceDB serviceDb;
    private ServiceCapacityDB capDb;
    private NotificationDB notifDb;
    private AppointmentNotificationUtil appointmentNotificationUtil;

    @Override
    public void init() {
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");

        apptDb = new AppointmentDB(dbUrl, dbUser, dbPassword);
        patientDb = new PatientDB(dbUrl, dbUser, dbPassword);
        clinicDb = new ClinicDB(dbUrl, dbUser, dbPassword);
        serviceDb = new ServiceDB(dbUrl, dbUser, dbPassword);
        capDb = new ServiceCapacityDB(dbUrl, dbUser, dbPassword);
        notifDb = new NotificationDB(dbUrl, dbUser, dbPassword);
        appointmentNotificationUtil = new AppointmentNotificationUtil(notifDb, clinicDb, serviceDb);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UserInfoBean user = UserCheckUtil.requireRole(request, response, "PATIENT");
        if (user == null) {
            return;
        }else{
            request.setAttribute("isPatient", true);
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("appointmentId");

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        PatientProfileBean patient = patientDb.getPatientByUserId(user.getUserId());
        if (patient == null) {
            request.setAttribute("error", "Patient profile not found.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        AppointmentBean appt = apptDb.getAppointmentByAppointmentId(appointmentId);
        if (appt == null) {
            request.setAttribute("error", "Appointment not found.");
            request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
            return;
        }

        if (appt.getPatientId() != patient.getPatientId()) {
            request.setAttribute("error", "You are not allowed to modify this appointment.");
            forwardDetail(request, response, appt, patient, null, null);
            return;
        }

        if (!isStatusModifiable(appt.getStatus())) {
            request.setAttribute("error", "Only REQUESTED/CONFIRMED appointments can be modified.");
            forwardDetail(request, response, appt, patient, null, null);
            return;
        }

        if ("prepareReschedule".equals(action)) {
            String newDate = request.getParameter("newDate");
            request.setAttribute("showRescheduleForm", true);
            forwardDetail(request, response, appt, patient, newDate, null);
            return;
        }

        if ("confirmReschedule".equals(action)) {
            String newDate = request.getParameter("newDate");
            String newTimeSlot = request.getParameter("newTimeSlot");
            String confirm = request.getParameter("confirmReschedule");

            if (!isModifiableOnlyBeforeOneDay(appt.getAppointmentDate())) {
                request.setAttribute("error", "You can only cancel/reschedule the day and timeslot before the appointment date (24 hours in advance).");
                forwardDetail(request, response, appt, patient, null, null);
                return;
            }

            if (!"YES".equals(confirm)) {
                request.setAttribute("error", "Please confirm reschedule.");
                forwardDetail(request, response, appt, patient, newDate, null);
                return;
            }

            if (newDate == null || newDate.trim().isEmpty()
                    || newTimeSlot == null || newTimeSlot.trim().isEmpty()) {
                request.setAttribute("error", "Please select date and timeslot.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            try {
                LocalDate d = LocalDate.parse(newDate);
                if (d.isBefore(LocalDate.now())) {
                    request.setAttribute("error", "New date cannot be in the past.");
                    forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                    return;
                }

                if (d.equals(LocalDate.now())) {
                    LocalTime now = LocalTime.now();
                    LocalTime slot = LocalTime.parse(newTimeSlot);
                    if (slot.isBefore(now)) {
                        request.setAttribute("error", "Selected timeslot is already in the past.");
                        forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                        return;
                    }
                }
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Invalid date or timeslot format.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            ServiceCapacityBean cap = capDb.getCapacity(appt.getClinicId(), appt.getServiceId(), newTimeSlot);
            if (cap == null) {
                request.setAttribute("error", "No capacity rule for selected timeslot.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            boolean sameAsCurrent = newDate.equals(appt.getAppointmentDate())
                    && newTimeSlot.equals(appt.getTimeSlot());
            if (sameAsCurrent) {
                request.setAttribute("error", "Please select a different date/time from your current appointment.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            int existingApptId = apptDb.findExistingAppointmentId(
                    patient.getPatientId(),
                    appt.getClinicId(),
                    appt.getServiceId(),
                    newDate,
                    newTimeSlot
            );
            if (existingApptId > 0 && existingApptId != appointmentId) {
                request.setAttribute("error", "You already have an appointment in this timeslot.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            int used = apptDb.countAppointments(appt.getClinicId(), appt.getServiceId(), newDate, newTimeSlot);
            if (used >= cap.getQuota()) {
                request.setAttribute("error", "Selected timeslot is full.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
                return;
            }

            boolean ok = apptDb.rescheduleAppointment(appointmentId, newDate, newTimeSlot);
            if (ok) {
                request.setAttribute("success", "Reschedule successful.");
                appt = apptDb.getAppointmentByAppointmentId(appointmentId);
                appointmentNotificationUtil.notifyRescheduleRequestedPending(
                        user.getUserId(),
                        appt.getClinicId(),
                        appt.getServiceId(),
                        appt.getAppointmentId(),
                        appt.getAppointmentDate(),
                        appt.getTimeSlot());
                forwardDetail(request, response, appt, patient, null, null);
            } else {
                request.setAttribute("error", "Reschedule failed. Please try again.");
                forwardDetail(request, response, appt, patient, newDate, newTimeSlot);
            }
            return;
        }

        if ("cancel".equals(action)) {
            String confirm = request.getParameter("confirmCancel");
            if (!"YES".equals(confirm)) {
                request.setAttribute("error", "Please confirm cancellation.");
                forwardDetail(request, response, appt, patient, null, null);
                return;
            }

            if (!isModifiableOnlyBeforeOneDay(appt.getAppointmentDate())) {
                request.setAttribute("error", "You can only cancel/reschedule the day and timeslot before the appointment date (24 hours in advance).");
                forwardDetail(request, response, appt, patient, null, null);
                return;
            }

            boolean ok = apptDb.updateAppointmentStatus(appointmentId, "CANCELLED_BY_PATIENT");
            if (ok) {
                request.setAttribute("success", "Appointment cancelled successfully.");
                appt = apptDb.getAppointmentByAppointmentId(appointmentId);
                appointmentNotificationUtil.notifyAppointmentCancelledByPatient(
                        user.getUserId(),
                        appt.getClinicId(),
                        appt.getServiceId(),
                        appt.getAppointmentId(),
                        appt.getAppointmentDate(),
                        appt.getTimeSlot());
            } else {
                request.setAttribute("error", "Cancel failed. Please try again.");
            }
            forwardDetail(request, response, appt, patient, null, null);
            return;
        }

        request.setAttribute("error", "Unknown action.");
        forwardDetail(request, response, appt, patient, null, null);
    }

    private boolean isStatusModifiable(String status) {
        return "REQUESTED".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status);
    }

    private boolean isModifiableOnlyBeforeOneDay(String appointmentDate) {
    try {
        LocalDate apptDate = LocalDate.parse(appointmentDate);
        return LocalDate.now().isBefore(apptDate.minusDays(1));
    } catch (Exception e) {
        return false;
    }
}

    private void forwardDetail(HttpServletRequest request, HttpServletResponse response,
            AppointmentBean appt, PatientProfileBean patient, String selectedNewDate, String selectedNewTimeSlot)
            throws ServletException, IOException {

        ClinicBean clinic = clinicDb.getClinicByID(appt.getClinicId());
        ServiceBean service = serviceDb.getServiceById(appt.getServiceId());

        request.setAttribute("appointment", appt);
        request.setAttribute("clinic", clinic);
        request.setAttribute("service", service);
        request.setAttribute("patient", patient);

        boolean showRescheduleForm = Boolean.TRUE.equals(request.getAttribute("showRescheduleForm")) || selectedNewDate != null;
        if (showRescheduleForm) {
            List<ServiceCapacityBean> capList = capDb.getCapacityByClinicService(appt.getClinicId(), appt.getServiceId());
            Set<String> fullSlots = new HashSet<>();

            if (capList != null) {
                for (ServiceCapacityBean sc : capList) {
                    int used = apptDb.countAppointments(appt.getClinicId(), appt.getServiceId(), selectedNewDate, sc.getTimeSlot());

                    boolean currentSameSlot = selectedNewDate != null
                            && selectedNewDate.equals(appt.getAppointmentDate())
                            && sc.getTimeSlot().equals(appt.getTimeSlot());
                    if (currentSameSlot) {
                        used = Math.max(0, used - 1);
                    }

                    if (used >= sc.getQuota()) {
                        fullSlots.add(sc.getTimeSlot());
                    }
                }
            }

            request.setAttribute("showRescheduleForm", true);
            request.setAttribute("selectedNewDate", selectedNewDate);
            request.setAttribute("selectedNewTimeSlot", selectedNewTimeSlot);
            request.setAttribute("capacityList", capList);
            request.setAttribute("fullTimeSlots", fullSlots);
        } else {
            request.removeAttribute("showRescheduleForm");
        }

        request.getRequestDispatcher("/common/appointmentRecordDetails.jsp").forward(request, response);
    }
}
