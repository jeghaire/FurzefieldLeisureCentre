package com.furzefield.model;

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

    public String getRatingLabel() {
        return switch (rating) {
            case 1 -> "Very Dissatisfied";
            case 2 -> "Dissatisfied";
            case 3 -> "Ok";
            case 4 -> "Satisfied";
            case 5 -> "Very Satisfied";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return member.getName() + " rated " + lesson.getExerciseType().getDisplayName()
                + " " + rating + "/5 (" + getRatingLabel() + ")"
                + " - \"" + comment + "\"";
    }
}