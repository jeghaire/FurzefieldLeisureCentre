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
        System.out.printf("%-5s | %-11s | %-10s | %-8s%n", "Rank", "Exercise", "Income", "Attended");
        System.out.println("-".repeat(47));

        ExerciseType[] types = ExerciseType.values();
        double[] incomes = new double[types.length];
        long[] attendances = new long[types.length];

        for (int i = 0; i < types.length; i++) {
            for (Lesson l : timetable.getAllLessons()) {
                if (l.getExerciseType() != types[i]) continue;
                incomes[i] += l.getTotalIncome();
                attendances[i] += l.getBookings().stream()
                        .filter(b -> b.getStatus() == BookingStatus.ATTENDED)
                        .count();
            }
        }

        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < types.length; i++) order.add(i);
        order.sort((a, b) -> Double.compare(incomes[b], incomes[a]));

        int rank = 1;
        for (int i = 0; i < order.size(); i++) {
            if (i > 0 && incomes[order.get(i)] < incomes[order.get(i - 1)]) rank = i + 1;
            System.out.printf("%-5d | %-11s | £%-9.2f | %-8d%n",
                    rank, types[order.get(i)].getDisplayName(),
                    incomes[order.get(i)], attendances[order.get(i)]);
        }

        System.out.println("-".repeat(47));
        if (!order.isEmpty()) {
            System.out.println("Highest: " + types[order.getFirst()].getDisplayName()
                    + " (£" + String.format("%.2f", incomes[order.getFirst()]) + ")");
        }
    }
}