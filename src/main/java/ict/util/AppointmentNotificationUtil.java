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

    private void sendAppointmentImportant(int userId,
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
        msg.append("<hr>Clinic: ").append(clinicName)
                .append("\nService: ").append(serviceName)
                .append("\nAppointment ID: ").append(apptId)
                .append("\nDate: ").append(date)
                .append("\nTimeslot: ").append(timeSlot)
                .append("\n<hr>").append(actionLine);
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
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment request submitted",
                "We have received your appointment and will process it as soon as possible and reply to you via message.", null);
    }

    public void notifyAppointmentConfirmed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment confirmed",
                "Your appointment has been confirmed by clinic staff.", null);
    }

    public void notifyAppointmentCancelledByStaff(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot,
            String reason) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment cancelled by staff",
                "Your appointment has been cancelled by clinic staff.", reason);
    }

    public void notifyAppointmentCancelledByPatient(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment cancelled",
                "You cancelled this appointment successfully.", null);
    }

    public void notifyRescheduleRequestedPending(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Reschedule request submitted",
                "Your reschedule request has been submitted and is pending staff confirmation.", null);
    }

    public void notifyRescheduleConfirmed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Reschedule confirmed",
                "Your appointment reschedule has been confirmed.", null);
    }

    public void notifyAppointmentMissed(int userId,
            int clinicId,
            int serviceId,
            int apptId,
            String date,
            String timeSlot) {
        sendAppointmentImportant(userId, clinicId, serviceId, apptId, date, timeSlot,
                "Appointment missed",
                "This appointment has been marked as missed because attendance was not recorded.", null);
    }

    private void sendQueueNotification(int userId,
            int clinicId,
            int serviceId,
            String date,
            int queueNumber,
            String title,
            String actionLine,
            String type) {

        ClinicBean clinic = clinicDb.getClinicByID(clinicId);
        ServiceBean service = serviceDb.getServiceById(serviceId);

        String clinicName = (clinic != null && clinic.getName() != null && !clinic.getName().isEmpty())
                ? clinic.getName() : ("Clinic ID: " + clinicId);

        String serviceName = (service != null && service.getName() != null && !service.getName().isEmpty())
                ? service.getName() : ("Service ID: " + serviceId);

        StringBuilder msg = new StringBuilder();
        msg.append("<hr>Clinic: ").append(clinicName)
                .append("\nService: ").append(serviceName)
                .append("\nDate: ").append(date)
                .append("\nQueue number: ").append(queueNumber)
                .append("\n<hr>").append(actionLine);

        try {
            notifDb.createNotification(userId, type, title, msg.toString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to create queue notification", e);
        }
    }

    public void notifyQueueCalled(int userId,
            int clinicId,
            int serviceId,
            String date,
            int queueNumber) {

        sendQueueNotification(userId, clinicId, serviceId, date, queueNumber,
                "Queue number called",
                "It is your turn now. Please proceed to the counter/clinic room as soon as possible.","IMPORTANT");
    }

    public void notifyQueueSkipped(int userId,
            int clinicId,
            int serviceId,
            String date,
            int queueNumber) {

        sendQueueNotification(userId, clinicId, serviceId, date, queueNumber,
                "Queue number skipped",
                "You were skipped because you did not respond in time. Please contact staff if you are still present.","IMPORTANT");
    }

    public void notifyQueueTake(int userId,
            int clinicId,
            int serviceId,
            String date,
            int queueNumber) {
        
        sendQueueNotification(userId, clinicId, serviceId, date, queueNumber,
                "Queue ticket issued",
                "Please stay near the clinic when your number is approaching.","NORMAL");
    }
    
    public void notifyQueueServed(int userId,
            int clinicId,
            int serviceId,
            String date,
            int queueNumber) {
        
        sendQueueNotification(userId, clinicId, serviceId, date, queueNumber,
                "Queue ticket Done",
                "Thank you for your comming, we hope you have are healthy body.","NORMAL");
    }
}
