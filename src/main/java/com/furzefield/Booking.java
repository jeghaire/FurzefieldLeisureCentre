package com.furzefield;

/**
 *
 * @author Mavi
 */
public class Booking {
    private final Member member;
    private final Lesson lesson;

    public Booking(Member member, Lesson lesson) {
        this.member = member;
        this.lesson = lesson;
    }

    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }

    @Override
    public String toString() {
        return member.getName() + " → " + lesson.getLessonType().getName()
                + " (" + lesson.getDay() + " " + lesson.getTimeSlot() + ")";
    }
}