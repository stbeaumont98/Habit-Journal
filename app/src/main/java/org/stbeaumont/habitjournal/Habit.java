package org.stbeaumont.habitjournal;

import java.util.Date;
import java.util.HashMap;

public class Habit {
    private String name;
    private HashMap<Date, Boolean> dateLog = new HashMap<>();

    public Habit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Boolean getCompleteOnDate(Date date) {
        return dateLog.get(date);
    }

    public void putCompleteOnDate(Date date, Boolean isCompleted) {
        dateLog.put(date, isCompleted);
    }

}
