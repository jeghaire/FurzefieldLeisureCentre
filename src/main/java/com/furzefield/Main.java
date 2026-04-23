package com.furzefield;

import com.furzefield.data.DataSetup;
import com.furzefield.enums.BookingStatus;
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
        System.out.println("FurzeField Leisure Centre System");
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
                case 6 -> attendLesson();
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
        System.out.println("  6. Attend lesson & submit a review");
        System.out.println("  7. View all bookings");
        System.out.println("  8. Report: Attendance & ratings");
        System.out.println("  9. Report: Income by exercise");
        System.out.println("  0. Exit");
        System.out.println("──────────────────────────────────────");
    }

    // ── OPTION 1: View timetable by day ──────────────────────────────────────

    private static void viewTimetableByDay() {
        System.out.println("1. Saturday");
        System.out.println("2. Sunday");
        while (true) {
            int choice = readIntCanCancel("Select day: ");
            if (choice == 0) return;
            if (choice == 1 || choice == 2) {
                Day day = (choice == 1) ? Day.SATURDAY : Day.SUNDAY;
                bookingSystem.viewTimetableByDay(day);
                return;
            }
            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }

    // ── OPTION 2: View timetable by exercise ─────────────────────────────────

    private static void viewTimetableByExercise() {
        ExerciseType[] types = ExerciseType.values();
        System.out.println("\n── Select Exercise ──");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName());
        }
        while (true) {
            int choice = readIntCanCancel("Select exercise: ");
            if (choice == 0) return;
            if (choice >= 1 && choice <= types.length) {
                bookingSystem.viewTimetableByExercise(types[choice - 1]);
                return;
            }
            System.out.println("Invalid choice. Please enter a number between 1 and " + types.length + ".");
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

        System.out.println("\nSelect the booking you want to CHANGE FROM:");
        Lesson oldLesson = selectMemberLesson(member);
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

        Lesson lesson = selectMemberLesson(member);
        if (lesson == null) return;

        try {
            bookingSystem.cancelBooking(member, lesson);
            System.out.println("Booking cancelled successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Cancel failed: " + e.getMessage());
        }
    }

    // ── OPTION 6: Attend a lesson and submit a review ─────────────────────────────────────────────

    private static void attendLesson() {
        Member member = selectMember();
        if (member == null) return;

        Lesson lesson = selectMemberLesson(member);
        if (lesson == null) return;

        int rating;
        while (true) {
            rating = readIntCanCancel("Enter rating (1-5): ");
            if (rating == 0) return;
            if (rating >= 1 && rating <= 5) break;
            System.out.println("Rating must be between 1 and 5.");
        }
        String comment = readStringWithCancel("Enter comment: ");
        if (comment == null) return;

        try {
            Review review = bookingSystem.attendLesson(member, lesson, rating, comment);
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
        System.out.printf("%-6s | %-20s | %-11s | %-9s | %-8s | %-9s | %-9s%n",
                "ID", "Member", "Exercise", "Lesson ID", "Day", "Time", "Status");
        System.out.println("-".repeat(83));
        for (Booking b : bookings) {
            Lesson l = b.getLesson();
            System.out.printf("%-6s | %-20s | %-11s | %-9s | %-8s | %-9s | %-9s%n",
                    b.getBookingId(),
                    b.getMember().getName(),
                    l.getExerciseType().getDisplayName(),
                    l.getLessonId(),
                    l.getDay().getDisplayName(),
                    l.getTimeSlot().getDisplayName(),
                    b.getStatus());
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private static Member selectMember() {
        System.out.println("\n── Select Member ──");
        for (Member m : members) {
            System.out.println(m.getId() + ". " + m.getName());
        }
        while (true) {
            int id = readIntCanCancel("Enter member number: ");
            if (id == 0) return null;
            for (Member m : members) {
                if (m.getId() == id) return m;
            }
            System.out.println("Member not found. Try again.");
        }
    }

    private static Lesson selectMemberLesson(Member member) {
        List<Booking> bookings = new ArrayList<>();
        for (Booking b : member.getBookings()) {
            if (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.CHANGED) {
                bookings.add(b);
            }
        }

        if (bookings.isEmpty()) {
            System.out.println(member.getName() + " has no active bookings.");
            return null;
        }

        System.out.println("\n── " + member.getName() + "'s Bookings ──");
        for (int i = 0; i < bookings.size(); i++) {
            System.out.printf("%2d. %s%n", (i + 1), bookings.get(i));
        }

        while (true) {
            int index = readIntCanCancel("Select booking number: ");
            if (index == 0) return null;
            if (index >= 1 && index <= bookings.size()) {
                return bookings.get(index - 1).getLesson();
            }
            System.out.println("Invalid selection. Please enter a number between 1 and " + bookings.size() + ".");
        }
    }

    private static Lesson selectLesson() {
        System.out.println("\n── Select Lesson ──");
        while (true) {
            System.out.println("Search by: 1. Day   2. Exercise name   3. Enter lesson ID directly");
            int choice = readIntCanCancel("Choice: ");
            if (choice == 0) return null;

            List<Lesson> lessons = new ArrayList<>();

            if (choice == 1) {
                System.out.println("1. Saturday  2. Sunday");
                while (true) {
                    int day = readIntCanCancel("Select: ");
                    if (day == 0) return null;
                    if (day == 1 || day == 2) {
                        Day dayEnum = (day == 1) ? Day.SATURDAY : Day.SUNDAY;
                        lessons = bookingSystem.getTimetable().getLessonsByDay(dayEnum);
                        break;
                    }
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else if (choice == 2) {
                ExerciseType[] types = ExerciseType.values();
                for (int i = 0; i < types.length; i++) {
                    System.out.println((i + 1) + ". " + types[i].getDisplayName());
                }
                while (true) {
                    int pick = readIntCanCancel("Select exercise: ");
                    if (pick == 0) return null;
                    if (pick >= 1 && pick <= types.length) {
                        lessons = bookingSystem.getTimetable().getLessonsByExercise(types[pick - 1]);
                        break;
                    }
                    System.out.println("Invalid choice. Please enter a number between 1 and " + types.length + ".");
                }
            } else if (choice == 3) {
                while (true) {
                    String lessonId = readStringWithCancel("Enter lesson ID (e.g. W1SM): ");
                    if (lessonId == null) return null;
                    Lesson lesson = bookingSystem.getTimetable().findById(lessonId);
                    if (lesson != null) return lesson;
                    System.out.println("Lesson not found: " + lessonId + ". Try again.");
                }
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                continue;
            }

            if (lessons.isEmpty()) {
                System.out.println("No lessons found.");
                continue;
            }

            for (int i = 0; i < lessons.size(); i++) {
                System.out.printf("%2d. %s%n", (i + 1), lessons.get(i));
            }

            while (true) {
                int index = readIntCanCancel("Select lesson number: ");
                if (index == 0) return null;
                if (index >= 1 && index <= lessons.size()) {
                    return lessons.get(index - 1);
                }
                System.out.println("Invalid selection. Please enter a number between 1 and " + lessons.size() + ".");
            }
        }
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