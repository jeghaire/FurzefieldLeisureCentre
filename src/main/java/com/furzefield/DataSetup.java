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

        LessonType yoga      = types.get(0);
        LessonType zumba     = types.get(1);
        LessonType aquacise  = types.get(2);
        LessonType boxFit    = types.get(3);
        LessonType bodyBlitz = types.get(4);

        LessonType[][] schedule = {
                {yoga, zumba, aquacise},
                {boxFit, bodyBlitz, yoga},
                {zumba, aquacise, boxFit},
                {bodyBlitz, yoga, zumba},
                {aquacise, boxFit, bodyBlitz},
                {yoga, zumba, aquacise},
                {boxFit, bodyBlitz, yoga},
                {zumba, aquacise, boxFit},
                {bodyBlitz, yoga, zumba},
                {aquacise, boxFit, bodyBlitz},
                {yoga, zumba, aquacise},
                {boxFit, bodyBlitz, yoga},
                {zumba, aquacise, boxFit},
                {bodyBlitz, yoga, zumba},
                {aquacise, boxFit, bodyBlitz},
                {yoga, zumba, aquacise},
        };

        Day[] days = {Day.SATURDAY, Day.SUNDAY};
        TimeSlot[] slots = {TimeSlot.MORNING, TimeSlot.AFTERNOON, TimeSlot.EVENING};

        int scheduleIndex = 0;
        for (int weekend = 1; weekend <= 8; weekend++) {
            for (Day day : days) {
                LessonType[] daySchedule = schedule[scheduleIndex++];
                for (int s = 0; s < slots.length; s++) {
                    timetable.addLesson(new Lesson(daySchedule[s], weekend, day, slots[s]));
                }
            }
        }

        return timetable;
    }

    public static void createSeedBookings(BookingSystem bookingSystem,
                                          List<Member> members,
                                          Timetable timetable) {
        List<Lesson> lessons = timetable.getAllLessons();

        // Weekend 1 - Saturday Morning (Yoga) - lesson index 0
        bookingSystem.bookLesson(members.get(0), lessons.get(0));  // Alice - Yoga
        bookingSystem.bookLesson(members.get(1), lessons.get(0));  // Bob - Yoga
        bookingSystem.bookLesson(members.get(2), lessons.get(0));  // Carol - Yoga

        // Weekend 1 - Saturday Afternoon (Zumba) - lesson index 1
        bookingSystem.bookLesson(members.get(3), lessons.get(1));  // David - Zumba
        bookingSystem.bookLesson(members.get(4), lessons.get(1));  // Emma - Zumba

        // Weekend 1 - Saturday Evening (Aquacise) - lesson index 2
        bookingSystem.bookLesson(members.get(5), lessons.get(2));  // Frank - Aquacise
        bookingSystem.bookLesson(members.get(6), lessons.get(2));  // Grace - Aquacise
        bookingSystem.bookLesson(members.get(7), lessons.get(2));  // Harry - Aquacise

        // Weekend 1 - Sunday Morning (Box Fit) - lesson index 3
        bookingSystem.bookLesson(members.get(8), lessons.get(3));  // Isla - Box Fit
        bookingSystem.bookLesson(members.get(9), lessons.get(3));  // Jack - Box Fit
        bookingSystem.bookLesson(members.get(0), lessons.get(3));  // Alice - Box Fit

        // Weekend 1 - Sunday Afternoon (Body Blitz) - lesson index 4
        bookingSystem.bookLesson(members.get(1), lessons.get(4));  // Bob - Body Blitz
        bookingSystem.bookLesson(members.get(2), lessons.get(4));  // Carol - Body Blitz

        // Weekend 2 - Saturday Morning (Zumba) - lesson index 6
        bookingSystem.bookLesson(members.get(3), lessons.get(6));  // David - Zumba
        bookingSystem.bookLesson(members.get(4), lessons.get(6));  // Emma - Zumba
        bookingSystem.bookLesson(members.get(5), lessons.get(6));  // Frank - Zumba
        bookingSystem.bookLesson(members.get(6), lessons.get(6));  // Grace - Zumba

        // Weekend 2 - Saturday Afternoon (Aquacise) - lesson index 7
        bookingSystem.bookLesson(members.get(7), lessons.get(7));  // Harry - Aquacise
        bookingSystem.bookLesson(members.get(8), lessons.get(7));  // Isla - Aquacise

        // Weekend 2 - Sunday Morning (Body Blitz) - lesson index 9
        bookingSystem.bookLesson(members.get(9), lessons.get(9));  // Jack - Body Blitz
        bookingSystem.bookLesson(members.get(0), lessons.get(9));  // Alice - Body Blitz
        bookingSystem.bookLesson(members.get(1), lessons.get(9));  // Bob - Body Blitz
    }

    public static void createSeedReviews(BookingSystem bookingSystem,
                                         List<Member> members,
                                         Timetable timetable) {
        List<Lesson> lessons = timetable.getAllLessons();

        // At least 20 reviews required
        bookingSystem.submitReview(members.get(0), lessons.get(0), 5, "Amazing Yoga session!");
        bookingSystem.submitReview(members.get(1), lessons.get(0), 4, "Really enjoyed it.");
        bookingSystem.submitReview(members.get(2), lessons.get(0), 3, "It was okay.");
        bookingSystem.submitReview(members.get(3), lessons.get(1), 5, "Best Zumba ever!");
        bookingSystem.submitReview(members.get(4), lessons.get(1), 4, "Great energy in class.");
        bookingSystem.submitReview(members.get(5), lessons.get(2), 2, "Too slow for me.");
        bookingSystem.submitReview(members.get(6), lessons.get(2), 4, "Good workout.");
        bookingSystem.submitReview(members.get(7), lessons.get(2), 5, "Loved the Aquacise class!");
        bookingSystem.submitReview(members.get(8), lessons.get(3), 5, "Box Fit was intense!");
        bookingSystem.submitReview(members.get(9), lessons.get(3), 4, "Really challenging, loved it.");
        bookingSystem.submitReview(members.get(0), lessons.get(3), 3, "Good but very tough.");
        bookingSystem.submitReview(members.get(1), lessons.get(4), 4, "Body Blitz was great.");
        bookingSystem.submitReview(members.get(2), lessons.get(4), 5, "Fantastic class!");
        bookingSystem.submitReview(members.get(3), lessons.get(6), 4, "Zumba is always fun.");
        bookingSystem.submitReview(members.get(4), lessons.get(6), 5, "Instructor was brilliant.");
        bookingSystem.submitReview(members.get(5), lessons.get(6), 3, "Decent session.");
        bookingSystem.submitReview(members.get(6), lessons.get(6), 4, "Will come back.");
        bookingSystem.submitReview(members.get(7), lessons.get(7), 2, "A bit too easy.");
        bookingSystem.submitReview(members.get(8), lessons.get(7), 5, "Perfect pace for me.");
        bookingSystem.submitReview(members.get(9), lessons.get(9), 4, "Body Blitz well structured.");
        bookingSystem.submitReview(members.get(0), lessons.get(9), 5, "Incredible session!");
    }
}
