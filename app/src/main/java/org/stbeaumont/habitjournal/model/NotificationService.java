package org.stbeaumont.habitjournal.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.stbeaumont.habitjournal.controller.DataStorage;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;

//TODO: Figure out how to get this working so I don't have to have the app open to receive notifications

public class NotificationService extends Service {

    @Override
    public void onCreate() {
        sendBroadcast();
    }

    @Override
    public void onDestroy() {
        sendBroadcast();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sendBroadcast();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendBroadcast() {
        Context context = this.getApplicationContext();
        DataStorage data = new DataStorage(context);
        ArrayList<Habit> habits = data.loadData();

        for (int i = 0; i < habits.size(); i++) {
            NotificationAlarm notificationAlarm = new NotificationAlarm(context, i);
            notificationAlarm.scheduleNextNotification(LocalDate.now(), LocalTime.now());
        }
    }
}
