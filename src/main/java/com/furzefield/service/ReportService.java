package com.furzefield.service;

import com.furzefield.enums.BookingStatus;
import com.furzefield.enums.ExerciseType;
import com.furzefield.model.Lesson;

/**
 *
 * @author Mavi
 */
public class ReportService {

    private final Timetable timetable;

    public ReportService(Timetable timetable) {
        this.timetable = timetable;
    }

    public void printAttendanceReport(int month) {
        int firstWeekend = (month == 1) ? 1 : 5;
        int lastWeekend  = (month == 1) ? 4 : 8;

        System.out.println("\n══ Attendance & Rating Report — Month " + month + " (Weekends " + firstWeekend + "–" + lastWeekend + ") ══");
        System.out.printf("%-6s | %-11s | %-9s | %-9s | %-8s | %-10s%n",
                "ID", "Exercise", "Day", "Time", "Attended", "Avg Rating");
        System.out.println("-".repeat(70));

        for (Lesson lesson : timetable.getAllLessons()) {
            if (lesson.getWeekendNumber() < firstWeekend || lesson.getWeekendNumber() > lastWeekend) {
                continue;
            }

            long attended = lesson.getBookings().stream()
                    .filter(b -> b.getStatus() == BookingStatus.ATTENDED)
                    .count();
            String avgRating = lesson.getReviews().isEmpty()
                    ? "No reviews"
                    : String.format("%.1f", lesson.getAverageRating());

            System.out.printf("%-6s | %-11s | %-9s | %-9s | %-8d | %-10s%n",
                    lesson.getLessonId(),
                    lesson.getExerciseType().getDisplayName(),
                    lesson.getDay().getDisplayName(),
                    lesson.getTimeSlot().getDisplayName(),
                    attended,
                    avgRating);
        }
    }

    public void printIncomeReport() {
        System.out.println("\n══ Income Report ══");
        System.out.printf("%-5s | %-11s | %-10s%n", "Rank", "Exercise", "Income");
        System.out.println("-".repeat(35));

        ExerciseType topExercise = null;
        double topIncome = 0;

        int rank = 1;
        for (ExerciseType type : ExerciseType.values()) {
            double income = timetable.getAllLessons().stream()
                    .filter(l -> l.getExerciseType() == type)
                    .mapToDouble(Lesson::getTotalIncome)
                    .sum();

            System.out.printf("%-5d | %-11s | £%-9.2f%n",
                    rank++, type.getDisplayName(), income);

            if (income > topIncome) {
                topIncome = income;
                topExercise = type;
            }
        }

        System.out.println("-".repeat(35));
        if (topExercise != null) {
            System.out.println("Highest: " + topExercise.getDisplayName()
                    + " (£" + String.format("%.2f", topIncome) + ")");
        }
    }
}