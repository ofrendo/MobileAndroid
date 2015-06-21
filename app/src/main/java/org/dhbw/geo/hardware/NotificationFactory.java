package org.dhbw.geo.hardware;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.dhbw.geo.R;
import org.dhbw.geo.ui.MainActivity;

/**
 * Created by Oliver on 13.06.2015.
 */
public class NotificationFactory {

    private static int notificationID = 0;

    private static int getNextNotificationID() {
        notificationID++;
        return notificationID;
    }


    /**
     * Creates a temporary notification.
     * @param context Context of the app
     * @param title Title of the notification
     * @param text Text of the notification
     * @return notificationID. This is needed if the notification is to be removed later on.
     */
    public static int createNotification(Context context, String title, String text, boolean ongoing) {
        // Create a new builder
        NotificationCompat.Builder mBuilder = createBuilder(context, title, text);

        // Set the intent to be done when tapping the notification (I think)
        PendingIntent resultPendingIntent = createPendingIntent(context);
        mBuilder.setContentIntent(resultPendingIntent);

        // Check if this notification is supposed to be ongoing
        mBuilder.setOngoing(ongoing);

        // Get notificationManager system service
        NotificationManager notificationManager = getNotificationManager(context);

        // notificationID allows you to update/remove the notification later on.
        int currentNotificationID = getNextNotificationID();

        // build the notification and add vibration
        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_ALL;

        notificationManager.notify(currentNotificationID, notification);
        return currentNotificationID;
    }

    private static PendingIntent createPendingIntent(Context context) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return resultPendingIntent;
    }

    /**
     * Removes a temporary or ongoing notification.
     * @param notificationID
     */
    public static void removeNotification(Context context, int notificationID) {
        NotificationManager notificationManager = getNotificationManager(context);
        notificationManager.cancel(notificationID);
    }

    /**
     * Creates a notification builder
     * @param context Context of the app
     * @param title Title of the notification
     * @param text Text of the notification
     * @return A new instance of a Builder
     */
    private static NotificationCompat.Builder createBuilder(Context context, String title, String text) {
        return new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.launch) //android.R.drawable.ic_menu_send
                        .setContentTitle(title)
                        .setContentText(text);
    }

    /**
     * Utility method to get the NotificationManager system service
     * @param context Context of the app
     * @return The NotificationManager
     */
    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
