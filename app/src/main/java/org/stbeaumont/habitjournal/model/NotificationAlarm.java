package org.stbeaumont.habitjournal.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationAlarm {

    Context context;
    Habit habit;
    String title;
    String content;
    int position;
    String id;

    public NotificationAlarm(Context context, Habit habit, int pos, String id) {
        this.context = context;
        this.habit = habit;
        this.title = habit.getName();
        this.content = "Don't forget to complete your habit for today!";
        this.position = pos;
        this.id = id;
    }

    public void setUpAlarms() {

        int hour = (int) TimeUnit.MILLISECONDS.toHours(habit.getReminderTime());
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(habit.getReminderTime()) - (int) TimeUnit.HOURS.toMinutes(hour);

        switch (habit.getFrequency()) {
            case 0:
                scheduleDailyNotifications(habit.getDaysOfWeek(), hour, min);
                break;
            case 1:
                scheduleWeeklyNotifications(habit.getWeeklyInterval(), hour, min);
                break;
            case 2:
                scheduleMonthlyNotifications(habit.getDayOfMonth(), hour, min);
                break;
            default:
                break;
        }
    }

    private void scheduleDailyNotifications(ArrayList<Boolean> daysOfWeek, int hour, int min) {

        int[] days = {Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};

        for (int i = 0; i < 7; i++) {
            if (daysOfWeek.get(i)) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, days[i]);

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);

                if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                }

                Intent intent = new Intent(context, CustomReceiver.class);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("pos", position);
                intent.putExtra("id", id);

                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pIntent);
            }
        }
    }

    private void scheduleWeeklyNotifications(int weeklyInterval, int hour, int min) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 7);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        Intent intent = new Intent(context, CustomReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("pos", position);
        intent.putExtra("id", id);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7 * weeklyInterval, pIntent);

    }

    private void scheduleMonthlyNotifications(int dayOfMonth, int hour, int min) {

        Calendar calendar = Calendar.getInstance();

        if (dayOfMonth == 31) {
            int currentMonth = calendar.get(Calendar.MONTH);

            currentMonth++;

            if (currentMonth > Calendar.DECEMBER) {
                currentMonth = Calendar.JANUARY;
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
            }

            // reset calendar to next month
            calendar.set(Calendar.MONTH, currentMonth);
            // get the maximum possible days in this month
            int maximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            // set the calendar to maximum day (e.g in case of fEB 28th, or leap 29th)
            calendar.set(Calendar.DAY_OF_MONTH, maximumDay);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        Intent intent = new Intent(context, CustomReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("pos", position);
        intent.putExtra("id", id);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

    }
}
