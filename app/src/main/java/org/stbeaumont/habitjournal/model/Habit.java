package org.stbeaumont.habitjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    private int dayOfMonth;
    private boolean hasGoal;
    private int goal;
    private long reminderTime;
    private HashMap<LocalDate, Boolean> dateLog = new HashMap<>();

    public Habit(String name, int frequency, ArrayList<Boolean> daysOfWeek, int weeklyInterval, int dayOfMonth, boolean hasGoal, int goal, long reminderTime) {
        this.name = name;
        this.frequency = frequency;
        this.daysOfWeek = daysOfWeek;
        this.weeklyInterval = weeklyInterval;
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
        dayOfMonth = in.readInt();
        hasGoal = in.readByte() != 0;
        goal = in.readInt();
        reminderTime = in.readLong();
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

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean hasGoal() {
        return hasGoal;
    }

    public int getGoal() {
        return goal;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public Boolean checkLogOnDate(LocalDate date) {
        return dateLog.get(date);
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
        dest.writeInt(this.dayOfMonth);
        dest.writeByte((byte) (this.hasGoal ? 1 : 0));
        dest.writeInt(this.goal);
        dest.writeLong(this.reminderTime);
    }
}
