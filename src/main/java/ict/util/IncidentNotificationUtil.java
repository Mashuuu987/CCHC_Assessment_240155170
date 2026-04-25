/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ict.util;

/**
 *
 * @author amzte
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import ict.db.NotificationDB;

public class IncidentNotificationUtil {

    private static final Logger LOGGER = Logger.getLogger(IncidentNotificationUtil.class.getName());

    private final NotificationDB notifDb;

    public IncidentNotificationUtil(NotificationDB notifDb) {
        this.notifDb = notifDb;
    }

    private void sendIncidentImportant(int userId,
            int incidentId,
            String title,
            String actionLine,
            String reasonOrRemark) {

        StringBuilder msg = new StringBuilder();
        msg.append("<hr>")
                .append("Incident ID: ").append(incidentId)
                .append("\n<hr>")
                .append(actionLine);

        if (reasonOrRemark != null && !reasonOrRemark.isBlank()) {
            msg.append("\nRemark: ").append(reasonOrRemark.trim());
        }

        try {

            notifDb.createNotification(userId, "IMPORTANT", title, msg.toString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to create incident notification", e);
        }
    }

    public void notifyIncidentClosedByAdmin(int staffUserId,
            int incidentId,
            String remark) {

        sendIncidentImportant(
                staffUserId,
                incidentId,
                "Incident closed",
                "Your incident has been closed by Admin.",
                remark
        );
    }
}
