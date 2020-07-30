package org.stbeaumont.habitjournal.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.controller.DataStorage;
import org.stbeaumont.habitjournal.controller.HomeActivity;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//TODO: Figure out how to get this working so I don't have to have the app open to receive notifications

public class NotificationService extends Service {

    private String title;
    private int position;
    private int notificationID;
    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        stopTimerTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DataStorage data = new DataStorage(getApplicationContext());
        ArrayList<Habit> habits = data.loadData();
        position = intent.getIntExtra("pos", 0);
        if (habits.isEmpty()) {
            stopTimerTask();
        } else {
            title = habits.get(position).getName();
            notificationID = habits.get(position).getNotificationID();
            startTimer();
        }
        return START_STICKY;
    }

    public void startTimer () {
        timer = new Timer() ;
        initializeTimerTask() ;
        timer.schedule( timerTask , 5000 , 5000 );
    }

    public void stopTimerTask () {
        if ( timer != null ) {
            timer .cancel() ;
            timer = null;
        }
    }

    public void initializeTimerTask () {
        timerTask = new TimerTask() {
            public void run () {
                handler.post( new Runnable() {
                    public void run () {
                        showNotification(NotificationService.this) ;
                    }
                });
            }
        };
    }

    private void showNotification(Context context) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra("pos", position);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,  "habit-reminder")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText("Don't forget to complete your habit for today!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationAlarm notificationAlarm = new NotificationAlarm(context, position);
        notificationAlarm.scheduleNextNotification(LocalDate.now(), LocalTime.now());

        manager.notify(notificationID, builder.build());
    }
}
