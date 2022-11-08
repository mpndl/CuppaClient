package za.nmu.wrpv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import za.nmu.wrpv.messages.R;

public class Notification {
    private static final String channelID = "orderReady";
    private static final String channelName = ServerHandler.activity.getString(R.string.order_title);
    private static final String channelDescription = ServerHandler.activity.getResources().getString(R.string.order_text);
    public static void displayNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ServerHandler.activity, channelID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(ServerHandler.activity.getResources().getString(R.string.order_title))
                .setContentText(ServerHandler.activity.getResources().getString(R.string.order_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat manager = NotificationManagerCompat.from(ServerHandler.activity);
        manager.notify(1, builder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            NotificationManager manager1 = ServerHandler.activity.getSystemService(NotificationManager.class);
            manager1.createNotificationChannel(channel);
        }
    }
}
