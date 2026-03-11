package com.furzefield.model;

/**
 *
 * @author Mavi
 */
public record Review(Member member, Lesson lesson, int rating, String comment) {

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