package com.inochi.smsgateway.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.inochi.smsgateway.R;
import com.inochi.smsgateway.helper.Constants;
import com.inochi.smsgateway.item.NotifItem;
import com.inochi.smsgateway.service.SMSReceiver;

public class SMSNotification {
    public static void notify(final Context context, NotifItem notifItem) {
        final int id = notifItem.getId();
        final String ticker = notifItem.getTicker();
        final String title = notifItem.getTitle();
        final String text = notifItem.getMessage();

        Intent intentReceiver = new Intent(context, SMSReceiver.class);
        intentReceiver.putExtra(Constants.Setting.NOTIF_ID, id);
        intentReceiver.setAction(Constants.Action.CLOSE_NOTIFY);

        PendingIntent closeNotifyIntent = PendingIntent.getBroadcast(context,
                id, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = null;
        NotificationManager notificationManager = createNotificationChannel(context);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.Default.APP)
                .setDefaults(NotificationCompat.FLAG_AUTO_CANCEL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(ticker)
                .setVibrate(pattern)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", closeNotifyIntent)
                ;

        notificationManager.notify(id, builder.build());
    }

    private static NotificationManager createNotificationChannel(Context context) {
        NotificationManager notificationManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = context.getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.Default.APP, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        return notificationManager;
    }

    public static void cancel(final Context context, int id) {
        final NotificationManager nm = createNotificationChannel(context);
        nm.cancel(id);
    }
}
