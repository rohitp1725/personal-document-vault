package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reminder {

    private String reminderId;
    private String title;
    private LocalDate reminderDate;
    private String description;
    private boolean isDone;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Reminder(String title, LocalDate reminderDate, String description) {
        this.reminderId = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.title = title;
        this.reminderDate = reminderDate;
        this.description = description;
        this.isDone = false;
    }

    public Reminder(String reminderId, String title, LocalDate reminderDate,
                    String description, boolean isDone) {
        this.reminderId = reminderId;
        this.title = title;
        this.reminderDate = reminderDate;
        this.description = description;
        this.isDone = isDone;
    }

    public String toFileString() {
        return reminderId + "|" + title + "|" + reminderDate.format(FMT) + "|"
                + description + "|" + (isDone ? "1" : "0");
    }

    public static Reminder fromFileString(String line) {
        String[] p = line.split("\\|", 5);
        if (p.length < 5) return null;
        return new Reminder(p[0], p[1], LocalDate.parse(p[2], FMT), p[3], p[4].equals("1"));
    }

    public boolean isDueToday() { return reminderDate.equals(LocalDate.now()); }
    public boolean isUpcoming(int days) {
        return !isDone && reminderDate.isAfter(LocalDate.now().minusDays(1))
                && reminderDate.isBefore(LocalDate.now().plusDays(days));
    }

    public String getReminderId() { return reminderId; }
    public String getTitle() { return title; }
    public LocalDate getReminderDate() { return reminderDate; }
    public String getDescription() { return description; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }

    @Override
    public String toString() {
        return "[" + reminderId + "] " + title + " | Due: " + reminderDate.format(FMT)
                + " | " + description + (isDone ? " [DONE]" : "");
    }
}
