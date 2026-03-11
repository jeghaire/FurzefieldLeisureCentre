package com.furzefield.model;

import com.furzefield.enums.Day;
import com.furzefield.enums.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private int id;
    private String name;
    private List<Booking> bookings;

    public Member(int id, String name) {
        this.id       = id;
        this.name     = name;
        this.bookings = new ArrayList<>();
    }

    public int getId()                     { return id; }
    public String getName()                { return name; }
    public List<Booking> getBookings()     { return new ArrayList<>(bookings); }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    /**
     * Returns true if this member already has a booking
     * in the same weekend, day and time slot.
     * Prevents double-booking the same slot.
     */
    public boolean hasTimeConflict(Day day, TimeSlot timeSlot, int weekendNumber) {
        for (Booking b : bookings) {
            Lesson lesson = b.getLesson();
            if (lesson.getWeekendNumber() == weekendNumber
                    && lesson.getDay() == day
                    && lesson.getTimeSlot() == timeSlot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Member #" + id + " - " + name;
    }
}