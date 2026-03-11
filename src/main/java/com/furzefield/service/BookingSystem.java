package com.furzefield.service;

import com.furzefield.model.Booking;
import com.furzefield.model.Lesson;
import com.furzefield.model.Member;
import com.furzefield.model.Review;
import com.furzefield.enums.Day;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingSystem — Facade Pattern
 *
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

    // ── Singleton ─────────────────────────────────────────────────────────────────
    private static BookingSystem instance;

    private BookingSystem(Timetable timetable, List<Member> members) {
        this.timetable    = timetable;
        this.members      = members;
        this.allBookings  = new ArrayList<>();
        this.allReviews   = new ArrayList<>();
    }

    public static BookingSystem getInstance(Timetable timetable, List<Member> members) {
        if (instance == null) {
            instance = new BookingSystem(timetable, members);
        }
        return instance;
    }

    /** Used in JUnit tests to reset state between tests. */
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

        Booking booking = new Booking(member, lesson);
        lesson.addBooking(booking);
        member.addBooking(booking);
        allBookings.add(booking);
        return booking;
    }

    // ── CHANGE BOOKING ────────────────────────────────────────────────────────

    public Booking changeBooking(Member member, Lesson oldLesson, Lesson newLesson) {
        Booking existingBooking = findBooking(member, oldLesson);

        if (existingBooking == null) {
            throw new IllegalArgumentException(
                    "No booking found for " + member.getName()
                            + " in " + oldLesson.getExerciseType().getDisplayName());
        }

        // Temporarily remove old booking so conflict check works correctly
        oldLesson.removeBooking(existingBooking);
        member.removeBooking(existingBooking);
        allBookings.remove(existingBooking);

        try {
            Booking newBooking = bookLesson(member, newLesson);
            return newBooking;
        } catch (IllegalStateException e) {
            // Rollback
            oldLesson.addBooking(existingBooking);
            member.addBooking(existingBooking);
            allBookings.add(existingBooking);
            throw new IllegalStateException("Change failed — original booking restored. Reason: " + e.getMessage());
        }
    }

    // ── CANCEL BOOKING ────────────────────────────────────────────────────────

    public void cancelBooking(Member member, Lesson lesson) {
        Booking booking = findBooking(member, lesson);

        if (booking == null) {
            throw new IllegalArgumentException(
                    "No booking found for " + member.getName()
                            + " in " + lesson.getExerciseType().getDisplayName());
        }

        lesson.removeBooking(booking);
        member.removeBooking(booking);
        allBookings.remove(booking);
    }

    // ── REVIEWS ───────────────────────────────────────────────────────────────

    public Review submitReview(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5, got: " + rating);
        }

        // Check the member actually booked this lesson
        if (findBooking(member, lesson) == null) {
            throw new IllegalStateException(
                    member.getName() + " has no booking for this lesson and cannot review it.");
        }

        Review review = new Review(member, lesson, rating, comment);
        allReviews.add(review);
        lesson.addReview(review);
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

    // ── REPORT 1: Members per lesson + average rating ─────────────────────────

    public void printAttendanceReport() {
        System.out.println("\n========================================");
        System.out.println("  REPORT 1: Attendance & Ratings");
        System.out.println("========================================");

        for (Lesson lesson : timetable.getAllLessons()) {
            int memberCount = lesson.getBookings().size();

            // Calculate average rating for this lesson
            double total = 0;
            int reviewCount = 0;
            for (Review r : allReviews) {
                if (r.getLesson().equals(lesson)) {
                    total += r.getRating();
                    reviewCount++;
                }
            }

            String avgRating = (reviewCount > 0)
                    ? String.format("%.1f", total / reviewCount)
                    : "No reviews";

            System.out.println("Weekend " + lesson.getWeekendNumber()
                    + " | " + lesson.getDay()
                    + " " + lesson.getTimeSlot()
                    + " | " + lesson.getExerciseType().getDisplayName()
                    + " | Members: " + memberCount
                    + " | Avg Rating: " + avgRating);
        }
    }

    // ── REPORT 2: Highest income exercise type ────────────────────────────────

    public void printIncomeReport() {
        System.out.println("\n========================================");
        System.out.println("  REPORT 2: Income by Exercise Type");
        System.out.println("========================================");

        // Get all unique lesson type names
        List<String> exerciseNames = new ArrayList<>();
        for (Lesson l : timetable.getAllLessons()) {
            String name = l.getExerciseType().getDisplayName();
            if (!exerciseNames.contains(name)) {
                exerciseNames.add(name);
            }
        }

        String topExercise = "";
        double topIncome = 0;

        for (String name : exerciseNames) {
            double income = 0;
            for (Lesson l : timetable.getAllLessons()) {
                if (l.getExerciseType().getDisplayName().equals(name)) {
                    income += l.getBookings().size() * l.getExerciseType().getPrice();
                }
            }
            System.out.println(name + ": £" + String.format("%.2f", income));

            if (income > topIncome) {
                topIncome = income;
                topExercise = name;
            }
        }

        System.out.println("\nHighest income: " + topExercise
                + " (£" + String.format("%.2f", topIncome) + ")");
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
        return members;
    }
}