package com.furzefield.model;

import com.furzefield.enums.BookingStatus;

/**
 *
 * @author Mavi
 */

public class Booking {
    private final String bookingId;
    private final Member member;
    private Lesson lesson;
    private BookingStatus status;

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member   = member;
        this.lesson   = lesson;
        this.status   = BookingStatus.BOOKED;
    }

    public String getBookingId()     { return bookingId; }
    public Member getMember()        { return member; }
    public Lesson getLesson()        { return lesson; }
    public BookingStatus getStatus() { return status; }

    public void markAttended()  { this.status = BookingStatus.ATTENDED; }
    public void markCancelled() { this.status = BookingStatus.CANCELLED; }
    public void markChanged() { this.status = BookingStatus.CHANGED; }

    public void changeLesson(Lesson newLesson) {
        this.lesson = newLesson;
    }

    @Override
    public String toString() {
        return "[" + bookingId + "] " + member.getName()
                + " → " + lesson.getExerciseType().getDisplayName()
                + " (" + lesson.getLessonId() + " | " + lesson.getDay()
                + " " + lesson.getTimeSlot() + ") [" + status + "]";
    }
}