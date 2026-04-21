package ict.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import ict.bean.ClinicBean;
import ict.bean.ServiceBean;
import ict.db.ClinicDB;
import ict.db.NotificationDB;
import ict.db.ServiceDB;


public class AppointmentNotificationUtil {

        private static final Logger LOGGER = Logger.getLogger(AppointmentNotificationUtil.class.getName());

    private final NotificationDB notifDb;
    private final ClinicDB clinicDb;
    private final ServiceDB serviceDb;

    public AppointmentNotificationUtil(NotificationDB notifDb, ClinicDB clinicDb, ServiceDB serviceDb) {
        this.notifDb = notifDb;
        this.clinicDb = clinicDb;
        this.serviceDb = serviceDb;
    }

    private void sendImportant(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot,
            String title,
            String actionLine,
            String reason) {

        ClinicBean clinic = clinicDb.getClinicByID(clinicId);
        ServiceBean service = serviceDb.getServiceById(serviceId);

        String clinicName = (clinic != null && clinic.getName() != null && !clinic.getName().isEmpty())
                ? clinic.getName() : ("Clinic ID: " + clinicId);
        String serviceName = (service != null && service.getName() != null && !service.getName().isEmpty())
                ? service.getName() : ("Service ID: " + serviceId);

        StringBuilder msg = new StringBuilder();
        msg.append("Clinic: ").append(clinicName)
                .append("\nService: ").append(serviceName)
                .append("\nAppointment ID: ").append(apptId)
                .append("\nDate: ").append(date)
                .append("\nTimeslot: ").append(timeSlot)
                .append("\n\n").append(actionLine);
                if (reason != null && !reason.isBlank()) {
                        msg.append("\nReason: ").append(reason);
                }

        try {
            notifDb.createNotification(userId, "IMPORTANT", title, msg.toString());
        } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to create appointment notification", e);
        }
    }

    public void notifyAppointmentRequestedPending(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment request submitted",
                "We have received your appointment and will process it as soon as possible and reply to you via message.",null);
    }

    public void notifyAppointmentConfirmed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment confirmed",
                "Your appointment has been confirmed by clinic staff.",null);
    }

    public void notifyAppointmentCancelledByStaff(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot,
            String reason) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment cancelled by staff",
                "Your appointment has been cancelled by clinic staff.",reason);
    }

    public void notifyAppointmentCancelledByPatient(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment cancelled",
                "You cancelled this appointment successfully.",null);
    }

    public void notifyRescheduleRequestedPending(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Reschedule request submitted",
                "Your reschedule request has been submitted and is pending staff confirmation.",null);
    }

    public void notifyRescheduleConfirmed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Reschedule confirmed",
                "Your appointment reschedule has been confirmed.",null);
    }

    public void notifyAppointmentReminder24Hours(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment reminder (24 hours)",
                "Reminder: your appointment is within 24 hours. Please arrive on time.",null);
    }

    public void notifyAppointmentMissed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment missed",
                "This appointment has been marked as missed because attendance was not recorded.",null);
    }
}
