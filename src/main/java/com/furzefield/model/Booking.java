package com.furzefield.model;

/**
 *
 * @author Mavi
 */
public record Booking(Member member, Lesson lesson) {
    @Override
    public String toString() {
        return member.getName() + " booked " + lesson.getExerciseType().getDisplayName()
                + " (" + lesson.getDay() + " " + lesson.getTimeSlot() + ")";
    }
}