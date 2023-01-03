package za.nmu.wrpv;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import za.nmu.wrpv.messages.R;

public class Notification {
    private static final int NOTIFICATION_ID = 1;
    private static final String channelID = "orderReady";

    public static void displayNotification(Activity activity) {
        String channelName = activity.getString(R.string.order_title);
        String channelDescription = activity.getResources().getString(R.string.order_text);

        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra("pending", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, channelID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(activity.getResources().getString(R.string.order_title))
                .setContentText(activity.getResources().getString(R.string.order_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(activity);
        manager.notify(NOTIFICATION_ID, builder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            NotificationManager manager1 = activity.getSystemService(NotificationManager.class);
            manager1.createNotificationChannel(channel);
        }
    }

    public static void cancel(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
        nm.cancel(NOTIFICATION_ID);
    }
}
