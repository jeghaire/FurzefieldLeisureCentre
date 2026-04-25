package com.furzefield.data;

import com.furzefield.enums.Day;
import com.furzefield.enums.ExerciseType;
import com.furzefield.enums.TimeSlot;
import com.furzefield.model.*;
import com.furzefield.service.BookingSystem;
import com.furzefield.service.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class DataSetup {

    public static List<Member> createMembers() {
        List<Member> members = new ArrayList<>();
        members.add(new Member(1, "Alice Johnson", "alice@flc.com"));
        members.add(new Member(2, "Bob Smith", "bob@flc.com"));
        members.add(new Member(3, "Carol White", "carol@flc.com"));
        members.add(new Member(4, "David Brown", "david@flc.com"));
        members.add(new Member(5, "Emma Davis", "emma@flc.com"));
        members.add(new Member(6, "Frank Wilson", "frank@flc.com"));
        members.add(new Member(7, "Grace Taylor", "grace@flc.com"));
        members.add(new Member(8, "Harry Moore", "harry@flc.com"));
        members.add(new Member(9, "Isla Martin", "isla@flc.com"));
        members.add(new Member(10, "Jack Thompson", "jack@flc.com"));
        members.add(new Member(11, "Praise Jomavi", "praise.jomavi@flc.com"));
        return members;
    }

    public static Timetable createTimetable() {
        Timetable timetable = new Timetable();

        ExerciseType[][] schedule = {
                { ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.AQUACISE },
                { ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.YOGA },
                { ExerciseType.ZUMBA, ExerciseType.AQUACISE, ExerciseType.BOX_FIT },
                { ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.ZUMBA },
                { ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ },
                { ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.AQUACISE },
                { ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.YOGA },
                { ExerciseType.ZUMBA, ExerciseType.AQUACISE, ExerciseType.BOX_FIT },
                { ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.ZUMBA },
                { ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ },
                { ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.AQUACISE },
                { ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ, ExerciseType.YOGA },
                { ExerciseType.ZUMBA, ExerciseType.AQUACISE, ExerciseType.BOX_FIT },
                { ExerciseType.BODY_BLITZ, ExerciseType.YOGA, ExerciseType.ZUMBA },
                { ExerciseType.AQUACISE, ExerciseType.BOX_FIT, ExerciseType.BODY_BLITZ },
                { ExerciseType.YOGA, ExerciseType.ZUMBA, ExerciseType.AQUACISE },
        };

        Day[] days = { Day.SATURDAY, Day.SUNDAY };
        TimeSlot[] slots = { TimeSlot.MORNING, TimeSlot.AFTERNOON, TimeSlot.EVENING };
        String[] dayCode = { "S", "U" };
        String[] slotCode = { "M", "A", "E" };

        int index = 0;
        for (int weekend = 1; weekend <= 8; weekend++) {
            for (int d = 0; d < days.length; d++) {
                ExerciseType[] daySchedule = schedule[index++];
                for (int s = 0; s < slots.length; s++) {
                    String lessonId = "W" + weekend + dayCode[d] + slotCode[s];
                    timetable.addLesson(
                            new Lesson(lessonId, daySchedule[s], weekend, days[d], slots[s]));
                }
            }
        }
        return timetable;
    }

    public static void createSeedBookings(BookingSystem bookingSystem,
            List<Member> members,
            Timetable timetable) {
        List<Lesson> lessons = timetable.getAllLessons();

        bookingSystem.bookLesson(members.get(0), lessons.get(0));
        bookingSystem.bookLesson(members.get(1), lessons.get(0));
        bookingSystem.bookLesson(members.get(2), lessons.get(0));
        bookingSystem.bookLesson(members.get(3), lessons.get(1));
        bookingSystem.bookLesson(members.get(4), lessons.get(1));
        bookingSystem.bookLesson(members.get(5), lessons.get(2));
        bookingSystem.bookLesson(members.get(6), lessons.get(2));
        bookingSystem.bookLesson(members.get(7), lessons.get(2));
        bookingSystem.bookLesson(members.get(8), lessons.get(3));
        bookingSystem.bookLesson(members.get(9), lessons.get(3));
        bookingSystem.bookLesson(members.get(0), lessons.get(3));
        bookingSystem.bookLesson(members.get(1), lessons.get(4));
        bookingSystem.bookLesson(members.get(2), lessons.get(4));
        bookingSystem.bookLesson(members.get(3), lessons.get(6));
        bookingSystem.bookLesson(members.get(4), lessons.get(6));
        bookingSystem.bookLesson(members.get(5), lessons.get(6));
        bookingSystem.bookLesson(members.get(6), lessons.get(6));
        bookingSystem.bookLesson(members.get(7), lessons.get(7));
        bookingSystem.bookLesson(members.get(8), lessons.get(7));
        bookingSystem.bookLesson(members.get(9), lessons.get(9));
        bookingSystem.bookLesson(members.get(0), lessons.get(9));
        bookingSystem.bookLesson(members.get(1), lessons.get(9));
    }

    public static void createSeedReviews(BookingSystem bookingSystem,
            List<Member> members,
            Timetable timetable) {
        List<Lesson> lessons = timetable.getAllLessons();

        bookingSystem.attendLesson(members.get(0), lessons.get(0), 5, "Amazing Yoga session!");
        bookingSystem.attendLesson(members.get(1), lessons.get(0), 4, "Really enjoyed it.");
        bookingSystem.attendLesson(members.get(2), lessons.get(0), 3, "It was okay.");
        bookingSystem.attendLesson(members.get(3), lessons.get(1), 5, "Best Zumba ever!");
        bookingSystem.attendLesson(members.get(4), lessons.get(1), 4, "Great energy in class.");
        bookingSystem.attendLesson(members.get(5), lessons.get(2), 2, "Too slow for me.");
        bookingSystem.attendLesson(members.get(6), lessons.get(2), 4, "Good workout.");
        bookingSystem.attendLesson(members.get(7), lessons.get(2), 5, "Loved the Aquacise class!");
        bookingSystem.attendLesson(members.get(8), lessons.get(3), 5, "Box Fit was intense!");
        bookingSystem.attendLesson(members.get(9), lessons.get(3), 4, "Really challenging, loved it.");
        bookingSystem.attendLesson(members.get(0), lessons.get(3), 3, "Good but very tough.");
        bookingSystem.attendLesson(members.get(1), lessons.get(4), 4, "Body Blitz was great.");
        bookingSystem.attendLesson(members.get(2), lessons.get(4), 5, "Fantastic class!");
        bookingSystem.attendLesson(members.get(3), lessons.get(6), 4, "Zumba is always fun.");
        bookingSystem.attendLesson(members.get(4), lessons.get(6), 5, "Instructor was brilliant.");
        bookingSystem.attendLesson(members.get(5), lessons.get(6), 3, "Decent session.");
        bookingSystem.attendLesson(members.get(6), lessons.get(6), 4, "Will come back.");
        bookingSystem.attendLesson(members.get(7), lessons.get(7), 2, "A bit too easy.");
        bookingSystem.attendLesson(members.get(8), lessons.get(7), 5, "Perfect pace for me.");
        bookingSystem.attendLesson(members.get(9), lessons.get(9), 4, "Body Blitz well structured.");
        bookingSystem.attendLesson(members.get(0), lessons.get(9), 5, "Incredible session!");
    }
}