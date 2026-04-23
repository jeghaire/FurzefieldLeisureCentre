package com.furzefield.service;

import com.furzefield.enums.BookingStatus;
import com.furzefield.enums.ExerciseType;
import com.furzefield.model.Lesson;
import java.util.ArrayList;
import java.util.List;

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
        int lastWeekend = (month == 1) ? 4 : 8;

        System.out.println("\n──── Attendance & Rating Report — Month " + month + " (Weekends " + firstWeekend + "–"
                + lastWeekend + ") ────");
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
        System.out.println("\n──── Income Report ────");
        System.out.printf("%-5s | %-11s | %-10s%n", "Rank", "Exercise", "Income");
        System.out.println("-".repeat(35));

        List<double[]> rows = new ArrayList<>();
        List<ExerciseType> types = new ArrayList<>();
        for (ExerciseType type : ExerciseType.values()) {
            double income = timetable.getAllLessons().stream()
                    .filter(l -> l.getExerciseType() == type)
                    .mapToDouble(Lesson::getTotalIncome)
                    .sum();
            rows.add(new double[] { income });
            types.add(type);
        }

        // Sort descending by income
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < types.size(); i++)
            order.add(i);
        order.sort((a, b) -> Double.compare(rows.get(b)[0], rows.get(a)[0]));

        int rank = 1;
        for (int i = 0; i < order.size(); i++) {
            if (i > 0 && rows.get(order.get(i))[0] < rows.get(order.get(i - 1))[0]) {
                rank = i + 1;
            }
            System.out.printf("%-5d | %-11s | £%-9.2f%n",
                    rank, types.get(order.get(i)).getDisplayName(), rows.get(order.get(i))[0]);
        }

        System.out.println("-".repeat(35));
        if (!order.isEmpty()) {
            double topIncome = rows.get(order.getFirst())[0];
            System.out.println("Highest: " + types.get(order.getFirst()).getDisplayName()
                    + " (£" + String.format("%.2f", topIncome) + ")");
        }
    }
}