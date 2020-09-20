package org.stbeaumont.habitjournal.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.stbeaumont.habitjournal.controller.DataStorage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class NotificationAlarm {

    private Context context;
    private ArrayList<Habit> habits;
    private int position;

    public NotificationAlarm(Context context, int pos) {
        this.context = context;
        DataStorage data = new DataStorage(context);
        this.habits = data.loadData();
        this.position = pos;
        if (habits.get(pos).getNotificationID() == 0) {
            habits.get(pos).setNotificationID(getNewNotificationID());
        }
    }

    public void scheduleNextNotification(LocalDate startDate, LocalTime startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        LocalDate nextAlarm = getNextAlarmDate(habits.get(position), startDate, startTime);

        calendar.set(nextAlarm.getYear(), nextAlarm.getMonthValue() - 1, nextAlarm.getDayOfMonth(), habits.get(position).getReminderTime().getHour(), habits.get(position).getReminderTime().getMinute(), 0);

        Intent intent = new Intent(context, NotificationReceiver.class);

        intent.putExtra("pos", position);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, habits.get(position).getNotificationID(), intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }

    public static LocalDate getNextAlarmDate(Habit habit, LocalDate startDate, LocalTime startTime) {
        ArrayList<DayOfWeek> dayList = new ArrayList<>();

        dayList.add(DayOfWeek.SUNDAY);
        dayList.add(DayOfWeek.MONDAY);
        dayList.add(DayOfWeek.TUESDAY);
        dayList.add(DayOfWeek.WEDNESDAY);
        dayList.add(DayOfWeek.THURSDAY);
        dayList.add(DayOfWeek.FRIDAY);
        dayList.add(DayOfWeek.SATURDAY);

        if (habit.getFrequency() == 0) { //daily
            int i = dayList.indexOf(startDate.getDayOfWeek());
            while (!habit.getDaysOfWeek().get(i)) {
                i++;
                if (i >= 7) {
                    i = 0;
                }
            }
            if (startTime.compareTo(habit.getReminderTime()) < 0)
                return startDate.with(TemporalAdjusters.nextOrSame(dayList.get(i)));
            else
                return startDate.with(TemporalAdjusters.next(dayList.get(i)));
        } else if (habit.getFrequency() == 1) { //weekly
            if (startDate.compareTo(habit.getWeeklyStartDate()) < 0) {
                // if we haven't hit the start date yet
                return habit.getWeeklyStartDate();
            } else {
                LocalDate weekly = habit.getWeeklyStartDate();
                while (startDate.compareTo(weekly) > 0) {
                    // if current date is after the weekly date, add to the weeks by the interval and check again
                    weekly = weekly.plusWeeks(habit.getWeeklyInterval());
                }
                return weekly;
            }
        } else { //monthly
            if (habit.getDayOfMonth() == 1) {
                if ((startDate.compareTo(startDate.with(TemporalAdjusters.firstDayOfMonth())) == 0 && startTime.compareTo(habit.getReminderTime()) > 0) || startDate.compareTo(startDate.with(TemporalAdjusters.firstDayOfMonth())) > 0) {
                    // if it is today and the reminder time has passed
                    return startDate.with(TemporalAdjusters.firstDayOfNextMonth());
                } else {
                    // otherwise get the first day of the current month
                    return startDate.with(TemporalAdjusters.firstDayOfMonth());
                }
            } else if (habit.getDayOfMonth() == 31) {
                if (startDate.compareTo(startDate.with(TemporalAdjusters.lastDayOfMonth())) <= 0 && startTime.compareTo(habit.getReminderTime()) < 0) {
                    // if it is today or before and hasn't passed the reminder time
                    return startDate.with(TemporalAdjusters.lastDayOfMonth());
                } else {
                    // return the last day of next month
                    LocalDate dateNextMonth = startDate.with(TemporalAdjusters.firstDayOfNextMonth());
                    return dateNextMonth.with(TemporalAdjusters.lastDayOfMonth());
                }
            } else {
                if (startDate.getDayOfMonth() < habit.getDayOfMonth()) {
                    // if you haven't passed it yet
                    return startDate.withDayOfMonth(habit.getDayOfMonth());
                } else {
                    // if you've already passed it for this month
                    LocalDate dateNextMonth = startDate.with(TemporalAdjusters.firstDayOfNextMonth());
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
