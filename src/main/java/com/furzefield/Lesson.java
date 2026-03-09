package com.furzefield;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class Lesson {
    private final LessonType lessonType;
    private final int weekendNumber;
    private final String day;
    private final String timeSlot;
    private final List<Booking> bookings;

    public static final int MAX_CAPACITY = 4;

    public Lesson(LessonType lessonType, int weekendNumber, String day, String timeSlot) {
        this.lessonType = lessonType;
        this.weekendNumber = weekendNumber;
        this.day = day;
        this.timeSlot = timeSlot;
        this.bookings = new ArrayList<>();
    }

    public LessonType getLessonType() { return lessonType; }
    public int getWeekendNumber() { return weekendNumber; }
    public String getDay() { return day; }
    public String getTimeSlot() { return timeSlot; }
    public List<Booking> getBookings() { return bookings; }

    public boolean isFull() {
        return bookings.size() >= MAX_CAPACITY;
    }

    public int getAvailableSpaces() {
        return MAX_CAPACITY - bookings.size();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    @Override
    public String toString() {
        return "Weekend " + weekendNumber + " | " + day + " " + timeSlot
                + " | " + lessonType.getName()
                + " | Spaces: " + getAvailableSpaces() + "/" + MAX_CAPACITY
                + " | £" + lessonType.getPrice();
    }
}