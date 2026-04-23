package com.furzefield.service;

import com.furzefield.enums.BookingStatus;
import com.furzefield.enums.ExerciseType;
import com.furzefield.model.Booking;
import com.furzefield.model.Lesson;
import com.furzefield.model.Member;
import com.furzefield.model.Review;
import com.furzefield.enums.Day;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingSystem — Facade Pattern
 * This class acts as the single entry point for all system operations.
 * The console menu (Main.java) interacts only with BookingSystem,
 * which internally coordinates Member, Lesson, Booking, Review,
 * and Timetable — hiding all complexity from the UI layer.
 *
 * @author Mavi
 */
public class BookingSystem {
    private final Timetable timetable;
    private final List<Member> members;
    private final List<Booking> allBookings;
    private final List<Review> allReviews;
    private final ReportService reportService;
    private int bookingCounter = 1;

    // ── Singleton ─────────────────────────────────────────────────────────────────
    private static BookingSystem instance;

    private BookingSystem(Timetable timetable, List<Member> members) {
        this.timetable = timetable;
        this.members = members;
        this.reportService = new ReportService(timetable);
        this.allBookings = new ArrayList<>();
        this.allReviews = new ArrayList<>();
    }

    public static BookingSystem getInstance(Timetable timetable, List<Member> members) {
        if (instance == null) {
            instance = new BookingSystem(timetable, members);
        }
        return instance;
    }

    /**
     * Used in JUnit tests to reset state between tests.
     */
    public static void resetInstance() {
        instance = null;
    }

    // ── BOOKING ──────────────────────────────────────────────────────────────

    public Booking bookLesson(Member member, Lesson lesson) {
        if (lesson.isFull()) {
            throw new IllegalStateException(
                    "Lesson is full: " + lesson.getExerciseType().getDisplayName()
                            + " on " + lesson.getDay() + " " + lesson.getTimeSlot()
                            + " (Weekend " + lesson.getWeekendNumber() + ").");
        }
        if (member.hasTimeConflict(lesson.getDay(), lesson.getTimeSlot(), lesson.getWeekendNumber())) {
            throw new IllegalStateException(
                    member.getName() + " already has a booking at this time: "
                            + lesson.getDay() + " " + lesson.getTimeSlot()
                            + " (Weekend " + lesson.getWeekendNumber() + ").");
        }

        String bookingId = String.format("B%03d", bookingCounter++);
        Booking booking = new Booking(bookingId, member, lesson);
        lesson.addBooking(booking);
        member.addBooking(booking);
        allBookings.add(booking);
        return booking;
    }

    // ── CHANGE BOOKING ────────────────────────────────────────────────────────

    public Booking changeBooking(Member member, Lesson oldLesson, Lesson newLesson) {
        Booking existingBooking = findBookingOrThrow(member, oldLesson);

        if (existingBooking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change a cancelled booking.");
        }

        if (newLesson.isFull()) {
            throw new IllegalStateException("New lesson is full.");
        }

        // Remove from old lesson but keep the booking object
        oldLesson.removeBooking(existingBooking);
        member.removeBooking(existingBooking);

        // Check time conflict on new lesson
        if (member.hasTimeConflict(newLesson.getDay(), newLesson.getTimeSlot(), newLesson.getWeekendNumber())) {
            // Rollback
            oldLesson.addBooking(existingBooking);
            member.addBooking(existingBooking);
            throw new IllegalStateException("Time conflict with new lesson.");
        }

        // Update lesson reference in-place — same booking ID kept
        existingBooking.changeLesson(newLesson);
        existingBooking.markChanged();

        newLesson.addBooking(existingBooking);
        member.addBooking(existingBooking);

        return existingBooking;
    }

    // ── CANCEL BOOKING ────────────────────────────────────────────────────────

    public void cancelBooking(Member member, Lesson lesson) {
        Booking booking = findBookingOrThrow(member, lesson);

        if (booking.getStatus() == BookingStatus.ATTENDED) {
            throw new IllegalStateException("Cannot cancel a booking that has already been attended.");
        }

        booking.markCancelled();
        lesson.removeBooking(booking);
        member.removeBooking(booking);
        // Booking is kept in allBookings with CANCELLED status so its ID is never reused
    }

    // ── REVIEWS ───────────────────────────────────────────────────────────────

    public Review attendLesson(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Booking booking = findBookingOrThrow(member, lesson);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot attend a cancelled lesson.");
        }
        if (booking.getStatus() == BookingStatus.ATTENDED) {
            throw new IllegalStateException("Member has already attended this lesson.");
        }

        booking.markAttended();
        Review review = new Review(member, lesson, rating, comment);
        lesson.addReview(review);
        allReviews.add(review);
        return review;
    }

    // ── TIMETABLE VIEWS ───────────────────────────────────────────────────────

    public void viewTimetableByDay(Day day) {
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

    public void viewTimetableByExercise(ExerciseType exerciseType) {
        System.out.println("\n── Timetable for " + exerciseType.getDisplayName() + " ──");
        List<Lesson> lessons = timetable.getLessonsByExercise(exerciseType);
        if (lessons.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        for (Lesson l : lessons) {
            System.out.println(l);
        }
    }

    // ── REPORT 1: Members per lesson + average rating ─────────────────────────

    public void printAttendanceReport(int month) { reportService.printAttendanceReport(month); }

    // ── REPORT 2: Highest income exercise type ────────────────────────────────

    public void printIncomeReport() { reportService.printIncomeReport();}

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Booking findBooking(Member member, Lesson lesson) {
        for (Booking b : allBookings) {
            if (b.getMember().equals(member) && b.getLesson().equals(lesson)) {
                return b;
            }
        }
        return null;
    }

    private Booking findBookingOrThrow(Member member, Lesson lesson) {
        Booking booking = findBooking(member, lesson);
        if (booking == null) {
            throw new IllegalArgumentException(
                    "No booking found for " + member.getName()
                            + " in " + lesson.getExerciseType().getDisplayName());
        }
        return booking;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(allBookings);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(allReviews);
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }
}