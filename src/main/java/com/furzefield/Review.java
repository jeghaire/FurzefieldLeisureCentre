package com.furzefield;

/**
 *
 * @author Mavi
 */
public class Review {
    private final Member member;
    private final Lesson lesson;
    private final int rating;
    private final String comment;

    public Review(Member member, Lesson lesson, int rating, String comment) {
        this.member = member;
        this.lesson = lesson;
        this.rating = rating;
        this.comment = comment;
    }

    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return member.getName() + " rated " + lesson.getLessonType().getName()
                + " " + rating + "/5 - \"" + comment + "\"";
    }
}