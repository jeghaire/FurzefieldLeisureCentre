package com.furzefield;

import com.furzefield.data.DataSetup;
import com.furzefield.enums.Day;
import com.furzefield.enums.ExerciseType;
import com.furzefield.model.Booking;
import com.furzefield.model.Lesson;
import com.furzefield.model.Member;
import com.furzefield.model.Review;
import com.furzefield.service.BookingSystem;
import com.furzefield.service.Timetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Mavi
 */
public class Main {
    private static BookingSystem bookingSystem;
    private static List<Member> members;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
    // Initialise all data
        members = DataSetup.createMembers();
        Timetable timetable = DataSetup.createTimetable();
        bookingSystem = BookingSystem.getInstance(timetable, members);

        // Seed bookings and reviews
        DataSetup.createSeedBookings(bookingSystem, members, timetable);
        DataSetup.createSeedReviews(bookingSystem, members, timetable);

        System.out.println("========================================");
        System.out.println("Furzefield Leisure Centre System");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> viewTimetableByDay();
                case 2 -> viewTimetableByExercise();
                case 3 -> bookLesson();
                case 4 -> changeBooking();
                case 5 -> cancelBooking();
                case 6 -> submitReview();
                case 7 -> viewAllBookings();
                case 8 -> bookingSystem.printAttendanceReport();
                case 9 -> bookingSystem.printIncomeReport();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

    // ── MENU ─────────────────────────────────────────────────────────────────

    private static void printMenu() {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("  MAIN MENU");
        System.out.println("──────────────────────────────────────");
        System.out.println("  1. View timetable by day");
        System.out.println("  2. View timetable by exercise");
        System.out.println("  3. Book a lesson");
        System.out.println("  4. Change a booking");
        System.out.println("  5. Cancel a booking");
        System.out.println("  6. Submit a review");
        System.out.println("  7. View all bookings");
        System.out.println("  8. Report: Attendance & Ratings");
        System.out.println("  9. Report: Income by Exercise");
        System.out.println("  0. Exit");
        System.out.println("──────────────────────────────────────");
    }

    // ── OPTION 1: View timetable by day ──────────────────────────────────────

    private static void viewTimetableByDay() {
        System.out.println("1. Saturday");
        System.out.println("2. Sunday");
        int choice = readIntCanCancel("Select day: ");
        if (choice == 0) return;
        if (choice != 1 && choice != 2) {
            System.out.println("Invalid choice. Please enter 1 or 2.");
            return;
        }
        Day day = (choice == 1) ? Day.SATURDAY : Day.SUNDAY;
        bookingSystem.viewTimetableByDay(day);
    }

    // ── OPTION 2: View timetable by exercise ─────────────────────────────────

    private static void viewTimetableByExercise() {
        String name = readStringWithCancel("Enter exercise name (Yoga / Zumba / Aquacise / Box Fit / Body Blitz): ");
        if (name == null) return;
        try {
            ExerciseType type = ExerciseType.fromDisplayName(name);
            bookingSystem.viewTimetableByExercise(type);
        } catch (IllegalArgumentException e) {
            System.out.println("Exercise not found: " + name);
        }
    }

    // ── OPTION 3: Book a lesson ───────────────────────────────────────────────

    private static void bookLesson() {
        Member member = selectMember();
        if (member == null) return;

        Lesson lesson = selectLesson();
        if (lesson == null) return;

        try {
            Booking booking = bookingSystem.bookLesson(member, lesson);
            System.out.println("Booked: " + booking);
        } catch (IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    // ── OPTION 4: Change a booking ────────────────────────────────────────────

    private static void changeBooking() {
        Member member = selectMember();
        if (member == null) return;

        System.out.println("\nSelect the lesson you want to CHANGE FROM:");
        Lesson oldLesson = selectLesson();
        if (oldLesson == null) return;

        System.out.println("\nSelect the lesson you want to CHANGE TO:");
        Lesson newLesson = selectLesson();
        if (newLesson == null) return;

        try {
            Booking newBooking = bookingSystem.changeBooking(member, oldLesson, newLesson);
            System.out.println("Booking changed successfully: " + newBooking);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Change failed: " + e.getMessage());
        }
    }

    // ── OPTION 5: Cancel a booking ────────────────────────────────────────────

    private static void cancelBooking() {
        Member member = selectMember();
        if (member == null) return;

        Lesson lesson = selectLesson();
        if (lesson == null) return;

        try {
            bookingSystem.cancelBooking(member, lesson);
            System.out.println("Booking cancelled successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Cancel failed: " + e.getMessage());
        }
    }

    // ── OPTION 6: Submit a review ─────────────────────────────────────────────

    private static void submitReview() {
        Member member = selectMember();
        if (member == null) return;

        Lesson lesson = selectLesson();
        if (lesson == null) return;

        int rating = readIntCanCancel("Enter rating (1-5): ");
        if (rating == 0) return;
        String comment = readStringWithCancel("Enter comment: ");
        if (comment == null) return;

        try {
            Review review = bookingSystem.submitReview(member, lesson, rating, comment);
            System.out.println("Review submitted: " + review);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Review failed: " + e.getMessage());
        }
    }

    // ── OPTION 7: View all bookings ───────────────────────────────────────────

    private static void viewAllBookings() {
        List<Booking> bookings = bookingSystem.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.println("\n── All Bookings ──");
        for (Booking b : bookings) {
            System.out.println(b);
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private static Member selectMember() {
        System.out.println("\n── Select Member ──");
        for (Member m : members) {
            System.out.println(m.getId() + ". " + m.getName());
        }
        int id = readIntCanCancel("Enter member number: ");
        if (id == 0) return null;
        for (Member m : members) {
            if (m.getId() == id) return m;
        }
        System.out.println("Member not found.");
        return null;
    }

    private static Lesson selectLesson() {
        System.out.println("\n── Select Lesson ──");
        System.out.println("Search by: 1. Day   2. Exercise name   3. Enter lesson ID directly");
        int choice = readIntCanCancel("Choice: ");
        if (choice == 0) return null;

        List<Lesson> lessons = new ArrayList<>();

        if (choice == 1) {
            System.out.println("1. Saturday  2. Sunday");
            int day = readIntCanCancel("Select: ");
            if (day == 0) return null;
            if (day != 1 && day != 2) {
                System.out.println("Invalid choice. Please enter 1 or 2.");
                return null;
            }
            Day dayEnum = (day == 1) ? Day.SATURDAY : Day.SUNDAY;
            lessons = bookingSystem.getTimetable().getLessonsByDay(dayEnum);
        } else if (choice == 2) {
            String name = readStringWithCancel("Enter exercise name: ");
            if (name == null) return null;
            try {
                ExerciseType type = ExerciseType.fromDisplayName(name);
                lessons = bookingSystem.getTimetable().getLessonsByExercise(type);
            } catch (IllegalArgumentException e) {
                System.out.println("Exercise not found: " + name);
                return null;
            }
        } else if (choice == 3) {
            String lessonId = readStringWithCancel("Enter lesson ID (e.g. W1SM): ");
            if (lessonId == null) return null;
            Lesson lesson = bookingSystem.getTimetable().findById(lessonId);
            if (lesson == null) {
                System.out.println("Lesson not found: " + lessonId);
                return null;
            }
            return lesson;
        }

        if (lessons.isEmpty()) {
            System.out.println("No lessons found.");
            return null;
        }

        for (int i = 0; i < lessons.size(); i++) {
            System.out.println((i + 1) + ". " + lessons.get(i));
        }

        int index = readIntCanCancel("Select lesson number: ");
        if (index == 0) return null;
        if (index < 1 || index > lessons.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return lessons.get(index - 1);
    }

    private static int readInt() {
        System.out.print("Enter choice: ");
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Please enter a number: ");
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // clear buffer
        return val;
    }

    private static int readIntCanCancel(String prompt) {
        System.out.print(prompt + " (0 to cancel): ");
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Please enter a number: ");
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private static String readStringWithCancel(String prompt) {
        System.out.print(prompt + " (or 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null;
        return input;
    }
}