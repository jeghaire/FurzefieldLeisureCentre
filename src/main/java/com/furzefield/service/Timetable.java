package com.furzefield.service;

import com.furzefield.model.Lesson;
import com.furzefield.enums.Day;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class Timetable {
    private final List<Lesson> lessons;

    public Timetable() {
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public List<Lesson> getAllLessons() {
        return lessons;
    }

    // Search by day (Saturday or Sunday)
    public List<Lesson> getLessonsByDay(Day day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) {
            if (l.getDay() == day) {
                result.add(l);
            }
        }
        return result;
    }

    // Search by exercise name
    public List<Lesson> getLessonsByExercise(String exerciseName) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) {
            if (l.getLessonType().getName().equalsIgnoreCase(exerciseName)) {
                result.add(l);
            }
        }
        return result;
    }

    // Search by weekend number
    public List<Lesson> getLessonsByWeekend(int weekendNumber) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) {
            if (l.getWeekendNumber() == weekendNumber) {
                result.add(l);
            }
        }
        return result;
    }
}
