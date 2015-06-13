package org.dhbw.geo.hardware;

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
    private static int notificationIDOngoing = 1;

    public static void createNotification(Context context, String title, String text) {

        NotificationCompat.Builder mBuilder = createBuilder(context, title, text);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
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
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = getNotificationManager(context);

        // mId allows you to update the notification later on.
        notificationManager.notify(notificationID, mBuilder.build());

    }

    public static void createOngoingNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder = createBuilder(context, title, text);
        //mBuilder.setProgress(0, 0,  true);
        mBuilder.setOngoing(true);

        NotificationManager notificationManager = getNotificationManager(context);
        notificationManager.notify(notificationID, mBuilder.build());
    }

    private static NotificationCompat.Builder createBuilder(Context context, String title, String text) {
        return new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_menu_send)
                        .setContentTitle(title)
                        .setContentText(text);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
