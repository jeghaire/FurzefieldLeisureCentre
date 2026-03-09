package com.furzefield;

import java.util.ArrayList;
import java.util.List;

public class DataSetup {

    public static List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        members.add(new Member(1, "Alice Johnson"));
        members.add(new Member(2, "Bob Smith"));
        members.add(new Member(3, "Carol White"));
        members.add(new Member(4, "David Brown"));
        members.add(new Member(5, "Emma Davis"));
        members.add(new Member(6, "Frank Wilson"));
        members.add(new Member(7, "Grace Taylor"));
        members.add(new Member(8, "Harry Moore"));
        members.add(new Member(9, "Isla Martin"));
        members.add(new Member(10, "Jack Thompson"));
        return members;
    }

    public static List<LessonType> createLessonTypes() {
        List<LessonType> types = new ArrayList<>();
        types.add(new LessonType("Yoga", 12.00));
        types.add(new LessonType("Zumba", 10.00));
        types.add(new LessonType("Aquacise", 8.00));
        types.add(new LessonType("Box Fit", 15.00));
        types.add(new LessonType("Body Blitz", 11.00));
        return types;
    }

    public static Timetable createTimetable(List<LessonType> types) {
        Timetable timetable = new Timetable();

        // Convenience variables
        LessonType yoga     = types.get(0);
        LessonType zumba    = types.get(1);
        LessonType aquacise = types.get(2);
        LessonType boxFit   = types.get(3);
        LessonType bodyBlitz = types.get(4);

        String[] days  = {"Saturday", "Sunday"};
        String[] slots = {"Morning", "Afternoon", "Evening"};

        // 8 weekends × 2 days × 3 slots = 48 lessons
        // Each day: Morning, Afternoon, Evening rotate through the 5 lesson types

        LessonType[][] schedule = {
                // Weekend 1
                {yoga, zumba, aquacise},       // Saturday
                {boxFit, bodyBlitz, yoga},     // Sunday
                // Weekend 2
                {zumba, aquacise, boxFit},     // Saturday
                {bodyBlitz, yoga, zumba},      // Sunday
                // Weekend 3
                {aquacise, boxFit, bodyBlitz}, // Saturday
                {yoga, zumba, aquacise},       // Sunday
                // Weekend 4
                {boxFit, bodyBlitz, yoga},     // Saturday
                {zumba, aquacise, boxFit},     // Sunday
                // Weekend 5
                {bodyBlitz, yoga, zumba},      // Saturday
                {aquacise, boxFit, bodyBlitz}, // Sunday
                // Weekend 6
                {yoga, zumba, aquacise},       // Saturday
                {boxFit, bodyBlitz, yoga},     // Sunday
                // Weekend 7
                {zumba, aquacise, boxFit},     // Saturday
                {bodyBlitz, yoga, zumba},      // Sunday
                // Weekend 8
                {aquacise, boxFit, bodyBlitz}, // Saturday
                {yoga, zumba, aquacise},       // Sunday
        };

        int scheduleIndex = 0;
        for (int weekend = 1; weekend <= 8; weekend++) {
            for (String day : days) {
                LessonType[] daySchedule = schedule[scheduleIndex++];
                for (int s = 0; s < slots.length; s++) {
                    timetable.addLesson(new Lesson(daySchedule[s], weekend, day, slots[s]));
                }
            }
        }

        return timetable;
    }
}
