package com.furzefield;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class BookingSystem {
    private final Timetable timetable;
    private final List<Member> members;
    private final List<Booking> allBookings;
    private final List<Review> allReviews;

    public BookingSystem(Timetable timetable, List<Member> members) {
        this.timetable = timetable;
        this.members = members;
        this.allBookings = new ArrayList<>();
        this.allReviews = new ArrayList<>();
    }

    // ── BOOKING ──────────────────────────────────────────────────────────────

    public boolean bookLesson(Member member, Lesson lesson) {
        // Check 1: is the lesson full?
        if (lesson.isFull()) {
            System.out.println("Sorry, " + lesson.getLessonType().getName()
                    + " on " + lesson.getDay() + " " + lesson.getTimeSlot()
                    + " is full.");
            return false;
        }

        // Check 2: does the member already have a booking at this time?
        if (hasTimeConflict(member, lesson)) {
            System.out.println("Sorry, " + member.getName()
                    + " already has a booking on " + lesson.getDay()
                    + " " + lesson.getTimeSlot()
                    + " (Weekend " + lesson.getWeekendNumber() + ").");
            return false;
        }

        // All checks passed — create the booking
        Booking booking = new Booking(member, lesson);
        lesson.addBooking(booking);
        allBookings.add(booking);
        System.out.println("Booked: " + booking);
        return true;
    }

    // ── TIME CONFLICT CHECK ───────────────────────────────────────────────────

    private boolean hasTimeConflict(Member member, Lesson newLesson) {
        for (Booking existing : allBookings) {
            if (existing.getMember().equals(member)) {
                Lesson booked = existing.getLesson();
                if (booked.getWeekendNumber() == newLesson.getWeekendNumber()
                        && booked.getDay().equalsIgnoreCase(newLesson.getDay())
                        && booked.getTimeSlot().equalsIgnoreCase(newLesson.getTimeSlot())) {
                    return true;
                }
            }
        }
        return false;
    }

    // ── CHANGE BOOKING ────────────────────────────────────────────────────────

    public boolean changeBooking(Member member, Lesson oldLesson, Lesson newLesson) {
        // Find the existing booking
        Booking existingBooking = findBooking(member, oldLesson);

        if (existingBooking == null) {
            System.out.println("No booking found for " + member.getName()
                    + " in " + oldLesson.getLessonType().getName()
                    + " on " + oldLesson.getDay() + " " + oldLesson.getTimeSlot());
            return false;
        }

        // Temporarily remove the old booking so conflict check works correctly
        oldLesson.removeBooking(existingBooking);
        allBookings.remove(existingBooking);

        // Try to book the new lesson
        boolean success = bookLesson(member, newLesson);

        if (!success) {
            // New booking failed — restore the old one
            oldLesson.addBooking(existingBooking);
            allBookings.add(existingBooking);
            System.out.println("Change failed. Original booking restored.");
        } else {
            System.out.println("Booking changed successfully for " + member.getName());
        }

        return success;
    }

    // ── CANCEL BOOKING ────────────────────────────────────────────────────────

    public boolean cancelBooking(Member member, Lesson lesson) {
        Booking booking = findBooking(member, lesson);

        if (booking == null) {
            System.out.println("No booking found to cancel.");
            return false;
        }

        lesson.removeBooking(booking);
        allBookings.remove(booking);
        System.out.println("Booking cancelled: " + booking);
        return true;
    }

    // ── REVIEWS ───────────────────────────────────────────────────────────────

    public boolean submitReview(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return false;
        }

        // Check the member actually booked this lesson
        if (findBooking(member, lesson) == null) {
            System.out.println(member.getName()
                    + " has no booking for this lesson and cannot review it.");
            return false;
        }

        Review review = new Review(member, lesson, rating, comment);
        allReviews.add(review);
        System.out.println("Review submitted: " + review);
        return true;
    }

    // ── TIMETABLE VIEWS ───────────────────────────────────────────────────────

    public void viewTimetableByDay(String day) {
        System.out.println("\n── Timetable for " + day + " ──");
        List<Lesson> lessons = timetable.getLessonsByDay(day);
        if (lessons.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        for (Lesson l : lessons) {
            System.out.println(l);
        }
    }

    public void viewTimetableByExercise(String exerciseName) {
        System.out.println("\n── Timetable for " + exerciseName + " ──");
        List<Lesson> lessons = timetable.getLessonsByExercise(exerciseName);
        if (lessons.isEmpty()) {
            System.out.println("No lessons found for: " + exerciseName);
            return;
        }
        for (Lesson l : lessons) {
            System.out.println(l);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    public Booking findBooking(Member member, Lesson lesson) {
        for (Booking b : allBookings) {
            if (b.getMember().equals(member) && b.getLesson().equals(lesson)) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> getAllBookings() { return allBookings; }
    public List<Review> getAllReviews() { return allReviews; }
    public Timetable getTimetable() { return timetable; }
    public List<Member> getMembers() { return members; }
}