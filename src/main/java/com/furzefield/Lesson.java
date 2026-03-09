package com.furzefield;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class Lesson {
    private final LessonType lessonType;
    private final String day;
    private final String timeSlot;
    private final List<Booking> bookings;

    public static final int MAX_CAPACITY = 4;

    public Lesson(LessonType lessonType, String day, String timeSlot) {
        this.lessonType = lessonType;
        this.day = day;
        this.timeSlot = timeSlot;
        this.bookings = new ArrayList<>();
    }

    public LessonType getLessonType() { return lessonType; }
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
        return lessonType.getName() + " | " + day + " " + timeSlot
                + " | Spaces: " + getAvailableSpaces() + "/" + MAX_CAPACITY
                + " | £" + lessonType.getPrice();
    }
}