package org.stbeaumont.habitjournal.model;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int position = intent.getIntExtra("pos", 0);

        Intent background = new Intent(context, NotificationService.class);
        background.putExtra("pos", position);
        context.startService(background);

    }
}
