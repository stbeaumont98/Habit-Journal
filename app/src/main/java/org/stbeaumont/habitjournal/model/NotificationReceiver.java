package org.stbeaumont.habitjournal.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.controller.HomeActivity;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

public class NotificationReceiver extends BroadcastReceiver {

    private String CHANNEL_ID = "habit-reminder";

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int position = intent.getIntExtra("pos", 0);
        int notificationID = intent.getIntExtra("id", 0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra("pos", position);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationAlarm notificationAlarm = new NotificationAlarm(context, position);
        notificationAlarm.scheduleNextNotification(LocalDate.now(), LocalTime.now());

        manager.notify(notificationID, builder.build());

    }
}
