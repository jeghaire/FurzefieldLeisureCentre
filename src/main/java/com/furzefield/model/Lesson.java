package com.furzefield.model;

import com.furzefield.enums.Day;
import com.furzefield.enums.ExerciseType;
import com.furzefield.enums.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class Lesson {

    public static final int MAX_CAPACITY = 4;

    private final ExerciseType exerciseType;
    private final int weekendNumber;
    private final Day day;
    private final TimeSlot timeSlot;
    private final List<Booking> bookings;

    public Lesson(ExerciseType exerciseType, int weekendNumber, Day day, TimeSlot timeSlot) {
        this.exerciseType  = exerciseType;
        this.weekendNumber = weekendNumber;
        this.day           = day;
        this.timeSlot      = timeSlot;
        this.bookings      = new ArrayList<>();
    }

    public ExerciseType getExerciseType()  { return exerciseType; }
    public int getWeekendNumber()          { return weekendNumber; }
    public Day getDay()                    { return day; }
    public TimeSlot getTimeSlot()          { return timeSlot; }
    public List<Booking> getBookings()     { return new ArrayList<>(bookings); }
    public double getPrice()               { return exerciseType.getPrice(); }

    public boolean isFull()                { return bookings.size() >= MAX_CAPACITY; }
    public int getAvailableSpaces()        { return MAX_CAPACITY - bookings.size(); }

    public void addBooking(Booking booking)    { bookings.add(booking); }
    public void removeBooking(Booking booking) { bookings.remove(booking); }

    @Override
    public String toString() {
        return String.format("Week %-2d | %-9s | %-9s | %-11s | £%5.2f | Spaces: %d/4",
                weekendNumber,
                day.getDisplayName(),
                timeSlot.getDisplayName(),
                exerciseType.getDisplayName(),
                exerciseType.getPrice(),
                getAvailableSpaces());
    }
}