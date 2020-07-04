package org.stbeaumont.habitjournal.model;

import org.threeten.bp.LocalDate;

public class Event {
    private String id;
    private String text;
    private LocalDate date;

    public Event(String id, String text, LocalDate date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }
}
