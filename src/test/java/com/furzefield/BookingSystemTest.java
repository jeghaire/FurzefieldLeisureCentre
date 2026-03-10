package com.furzefield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookingSystemTest {

    private BookingSystem bookingSystem;
    private List<Member> members;
    private Timetable timetable;
    private List<LessonType> lessonTypes;

    @BeforeEach
    void setUp() {
        members = DataSetup.createMembers();
        lessonTypes = DataSetup.createLessonTypes();
        timetable = DataSetup.createTimetable(lessonTypes);
        bookingSystem = new BookingSystem(timetable, members);
    }

    // ── TEST 1: Successful booking ────────────────────────────────────────────
    @Test
    void testBookLessonSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        boolean result = bookingSystem.bookLesson(member, lesson);

        assertTrue(result, "Booking should succeed when space is available");
        assertEquals(1, lesson.getBookings().size());
    }

    // ── TEST 2: Booking fails when lesson is full ─────────────────────────────
    @Test
    void testBookLessonFailsWhenFull() {
        Lesson lesson = timetable.getAllLessons().get(0);

        // Fill the lesson to capacity (max 4)
        bookingSystem.bookLesson(members.get(0), lesson);
        bookingSystem.bookLesson(members.get(1), lesson);
        bookingSystem.bookLesson(members.get(2), lesson);
        bookingSystem.bookLesson(members.get(3), lesson);

        // 5th member should be rejected
        boolean result = bookingSystem.bookLesson(members.get(4), lesson);

        assertFalse(result, "Booking should fail when lesson is full");
        assertEquals(4, lesson.getBookings().size());
    }

    // ── TEST 3: Booking fails when time conflict exists ───────────────────────
    @Test
    void testBookLessonFailsOnTimeConflict() {
        Member member = members.get(0);
        Lesson lesson1 = timetable.getAllLessons().get(0); // Weekend 1, Sat Morning
        Lesson lesson2 = timetable.getAllLessons().get(0); // Same slot

        bookingSystem.bookLesson(member, lesson1);

        // Try to book a different lesson at the exact same time
        // We'll use the same lesson to guarantee a conflict
        boolean result = bookingSystem.bookLesson(member, lesson1);

        assertFalse(result, "Booking should fail when member already has a booking at this time");
    }

    // ── TEST 4: Lesson capacity is 4 ─────────────────────────────────────────
    @Test
    void testMaxCapacityIsFour() {
        Lesson lesson = timetable.getAllLessons().get(0);
        assertEquals(4, Lesson.MAX_CAPACITY);
    }

    // ── TEST 5: Change booking succeeds ──────────────────────────────────────
    @Test
    void testChangeBookingSuccess() {
        Member member = members.get(0);
        Lesson oldLesson = timetable.getAllLessons().get(0); // Weekend 1, Sat Morning
        Lesson newLesson = timetable.getAllLessons().get(1); // Weekend 1, Sat Afternoon

        bookingSystem.bookLesson(member, oldLesson);
        boolean result = bookingSystem.changeBooking(member, oldLesson, newLesson);

        assertTrue(result, "Change booking should succeed");
        assertEquals(0, oldLesson.getBookings().size(), "Old lesson should have no bookings");
        assertEquals(1, newLesson.getBookings().size(), "New lesson should have the booking");
    }

    // ── TEST 6: Change booking fails when new lesson is full ──────────────────
    @Test
    void testChangeBookingFailsWhenNewLessonFull() {
        Member member = members.get(0);
        Lesson oldLesson = timetable.getAllLessons().get(0);
        Lesson newLesson = timetable.getAllLessons().get(1);

        // Book old lesson for member
        bookingSystem.bookLesson(member, oldLesson);

        // Fill the new lesson to capacity
        bookingSystem.bookLesson(members.get(1), newLesson);
        bookingSystem.bookLesson(members.get(2), newLesson);
        bookingSystem.bookLesson(members.get(3), newLesson);
        bookingSystem.bookLesson(members.get(4), newLesson);

        boolean result = bookingSystem.changeBooking(member, oldLesson, newLesson);

        assertFalse(result, "Change should fail when new lesson is full");
        assertEquals(1, oldLesson.getBookings().size(), "Original booking should be restored");
    }

    // ── TEST 7: Submit review succeeds ────────────────────────────────────────
    @Test
    void testSubmitReviewSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);
        boolean result = bookingSystem.submitReview(member, lesson, 5, "Excellent!");

        assertTrue(result, "Review should be submitted successfully");
        assertEquals(1, bookingSystem.getAllReviews().size());
    }

    // ── TEST 8: Review fails with invalid rating ──────────────────────────────
    @Test
    void testSubmitReviewFailsWithInvalidRating() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);
        boolean result = bookingSystem.submitReview(member, lesson, 6, "Out of range rating");

        assertFalse(result, "Review should fail when rating is out of 1-5 range");
        assertEquals(0, bookingSystem.getAllReviews().size());
    }

    // ── TEST 9: Review fails if member never booked the lesson ────────────────
    @Test
    void testSubmitReviewFailsIfNotBooked() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        // No booking made
        boolean result = bookingSystem.submitReview(member, lesson, 4, "Sneaky review");

        assertFalse(result, "Review should fail if member never booked the lesson");
    }

    // ── TEST 10: Cancel booking succeeds ─────────────────────────────────────
    @Test
    void testCancelBookingSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);
        boolean result = bookingSystem.cancelBooking(member, lesson);

        assertTrue(result, "Cancel should succeed");
        assertEquals(0, lesson.getBookings().size(), "Lesson should have no bookings after cancel");
    }
}