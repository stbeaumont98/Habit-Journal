package org.stbeaumont.habitjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Habit implements Parcelable {

    public static final int SUN = 0;
    public static final int MON = 1;
    public static final int TUE = 2;
    public static final int WED = 3;
    public static final int THU = 4;
    public static final int FRI = 5;
    public static final int SAT = 6;

    public static final int DAILY = 0;
    public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;

    private String name;
    private int frequency; //daily, weekly, or monthly
    private ArrayList<Boolean> daysOfWeek = new ArrayList<>();
    private int weeklyInterval;
    private LocalDate weeklyStartDate;
    private int dayOfMonth;
    private boolean hasGoal;
    private int goal;
    private LocalTime reminderTime;
    private HashMap<LocalDate, Boolean> dateLog = new HashMap<>();
    private int notificationID = 0;

    public Habit() {
        name = "";
        frequency = 0;
        for (int i = 0; i < 7; i++) {
            daysOfWeek.add(true);
        }
        weeklyInterval = 0;
        weeklyStartDate = LocalDate.now();
        dayOfMonth = 0;
        hasGoal = false;
        goal = 0;
        reminderTime = LocalTime.of(12, 0);
    }

    public Habit(String name, int frequency, ArrayList<Boolean> daysOfWeek, int weeklyInterval, LocalDate weeklyStartDate, int dayOfMonth, boolean hasGoal, int goal, LocalTime reminderTime) {
        this.name = name;
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.weeklyInterval = weeklyInterval;
        this.weeklyStartDate = weeklyStartDate;
        this.dayOfMonth = dayOfMonth;
        this.hasGoal = hasGoal;
        this.goal = goal;
        this.reminderTime = reminderTime;
    }

    protected Habit(Parcel in) {
        name = in.readString();
        frequency = in.readInt();
        in.readList(daysOfWeek, null);
        weeklyInterval = in.readInt();
        weeklyStartDate = (LocalDate) in.readSerializable();
        dayOfMonth = in.readInt();
        hasGoal = in.readByte() != 0;
        goal = in.readInt();
        reminderTime = (LocalTime) in.readSerializable();
        in.readMap(dateLog, null);
        notificationID = in.readInt();
    }

    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getFrequency() {
        return frequency;
    }

    public ArrayList<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    public int getWeeklyInterval() {
        return weeklyInterval;
    }

    public LocalDate getWeeklyStartDate() {
        return weeklyStartDate;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean hasGoal() {
        return hasGoal;
    }

    public int getGoal() {
        return goal;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public int getNumberOfSuccesses() {
        int count = 0;
        for (Map.Entry<LocalDate, Boolean> entry : dateLog.entrySet()) {
            if (entry.getValue()) {
                count++;
            }
        }
        return count;
    }

    public Boolean checkLogOnDate(LocalDate date) throws NoLogForDateException {
        if (!dateLog.containsKey(date)) {
            throw new NoLogForDateException(this.name + "There is no log for this date.");
        }
        return dateLog.get(date);
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void logDate(LocalDate date, Boolean isCompleted) {
        dateLog.put(date, isCompleted);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.frequency);
        dest.writeList(this.daysOfWeek);
        dest.writeInt(this.weeklyInterval);
        dest.writeSerializable(this.weeklyStartDate);
        dest.writeInt(this.dayOfMonth);
        dest.writeByte((byte) (this.hasGoal ? 1 : 0));
        dest.writeInt(this.goal);
        dest.writeSerializable(this.reminderTime);
        dest.writeMap(this.dateLog);
        dest.writeInt(this.notificationID);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setDaysOfWeek(ArrayList<Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setWeeklyInterval(int weeklyInterval) {
        this.weeklyInterval = weeklyInterval;
    }

    public void setWeeklyStartDate(LocalDate weeklyStartDate) {
        this.weeklyStartDate = weeklyStartDate;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setHasGoal(boolean hasGoal) {
        this.hasGoal = hasGoal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public class NoLogForDateException extends Exception
    {
        public NoLogForDateException(String message)
        {
            super(message);
        }
    }
}
