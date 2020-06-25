package org.stbeaumont.habitjournal.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Habit {
    private String name;
    private String frequency; //daily, weekly, or monthly
    private HashMap<String, Boolean> daysOfWeek;
    private HashMap<Date, Boolean> dateLog = new HashMap<>();


    public String getName() {
        return name;
    }

    public Boolean checkLogOnDate(Date date) {
        return dateLog.get(date);
    }

    public void logDate(Date date, Boolean isCompleted) {
        dateLog.put(date, isCompleted);
    }

}
