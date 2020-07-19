package org.stbeaumont.habitjournal.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificationAlarm {

    private Context context;
    private ArrayList<Habit> habits;
    private String title;
    private String content;
    private int position;
    int id;

    public NotificationAlarm(Context context, ArrayList<Habit> habits, int pos) {
        this.context = context;
        this.habits = habits;
        this.title = habits.get(pos).getName();
        this.content = "Don't forget to complete your habit for today!";
        this.position = pos;
        if (habits.get(pos).getNotificationID() == 0) {
            this.id = getNewNotificationID();
            habits.get(pos).setNotificationID(this.id);
        } else {
            this.id = habits.get(pos).getNotificationID();
        }
    }

    private void scheduleNextNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        LocalDate nextAlarm = getNextAlarmDate(habits.get(position));

        calendar.set(nextAlarm.getYear(), nextAlarm.getMonthValue() - 1, nextAlarm.getDayOfMonth(), habits.get(position).getReminderTime().getHour(), habits.get(position).getReminderTime().getMinute(), 0);

        Intent intent = new Intent(context, CustomReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("pos", position);
        intent.putExtra("id", id);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    public static LocalDate getNextAlarmDate(Habit habit) {
        ArrayList<DayOfWeek> dayList = new ArrayList<>();

        dayList.add(DayOfWeek.SUNDAY);
        dayList.add(DayOfWeek.MONDAY);
        dayList.add(DayOfWeek.TUESDAY);
        dayList.add(DayOfWeek.WEDNESDAY);
        dayList.add(DayOfWeek.THURSDAY);
        dayList.add(DayOfWeek.FRIDAY);
        dayList.add(DayOfWeek.SATURDAY);

        LocalDate d = LocalDate.now();
        LocalTime t = LocalTime.now();
        if (habit.getFrequency() == 0) { //daily
            int i = dayList.indexOf(d.getDayOfWeek());
            while (!habit.getDaysOfWeek().get(i)) {
                i++;
                if (i >= 7) {
                    i = 0;
                }
            }
            return d.with(TemporalAdjusters.nextOrSame(dayList.get(i)));
        } else if (habit.getFrequency() == 1) { //weekly
            if (d.compareTo(habit.getWeeklyStartDate()) < 0) {
                // if we haven't hit the start date yet
                return habit.getWeeklyStartDate();
            } else {
                LocalDate weekly = habit.getWeeklyStartDate();
                while (d.compareTo(weekly) > 0) {
                    // if current date is after the weekly date, add to the weeks by the interval and check again
                    weekly = weekly.plusWeeks(habit.getWeeklyInterval());
                }
                return weekly;
            }
        } else { //monthly
            if (habit.getDayOfMonth() == 1) {
                if ((d.compareTo(d.with(TemporalAdjusters.firstDayOfMonth())) == 0 && t.compareTo(habit.getReminderTime()) > 0) || d.compareTo(d.with(TemporalAdjusters.firstDayOfMonth())) > 0) {
                    // if it is today and the reminder time has passed
                    return d.with(TemporalAdjusters.firstDayOfNextMonth());
                } else {
                    // otherwise get the first day of the current month
                    return d.with(TemporalAdjusters.firstDayOfMonth());
                }
            } else if (habit.getDayOfMonth() == 31) {
                if (d.compareTo(d.with(TemporalAdjusters.lastDayOfMonth())) <= 0 && t.compareTo(habit.getReminderTime()) < 0) {
                    // if it is today or before and hasn't passed the reminder time
                    return d.with(TemporalAdjusters.lastDayOfMonth());
                } else {
                    // return the last day of next month
                    LocalDate dateNextMonth = d.with(TemporalAdjusters.firstDayOfNextMonth());
                    return dateNextMonth.with(TemporalAdjusters.lastDayOfMonth());
                }
            } else {
                if (d.getDayOfMonth() < habit.getDayOfMonth()) {
                    // if you haven't passed it yet
                    return d.withDayOfMonth(habit.getDayOfMonth());
                } else {
                    // if you've already passed it for this month
                    LocalDate dateNextMonth = d.with(TemporalAdjusters.firstDayOfNextMonth());
                    return dateNextMonth.withDayOfMonth(habit.getDayOfMonth());
                }
            }
        }
    }

    public int getNewNotificationID() {
        int newID = new Random().nextInt(Integer.MAX_VALUE) + 1;
        for (Habit h : habits) {
            if (h.getNotificationID() == newID) {
                newID = new Random().nextInt(Integer.MAX_VALUE) + 1;
            }
        }
        return newID;
    }
}
