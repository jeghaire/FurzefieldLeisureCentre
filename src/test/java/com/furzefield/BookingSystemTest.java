package com.furzefield;

import com.furzefield.data.DataSetup;
import com.furzefield.model.Booking;
import com.furzefield.model.Lesson;
import com.furzefield.model.Member;
import com.furzefield.model.Review;
import com.furzefield.service.BookingSystem;
import com.furzefield.service.Timetable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BookingSystemTest {

    private BookingSystem bookingSystem;
    private List<Member> members;
    private Timetable timetable;

    @BeforeEach
    void setUp() {
        BookingSystem.resetInstance();
        members = DataSetup.createMembers();
        timetable = DataSetup.createTimetable();
        bookingSystem = BookingSystem.getInstance(timetable, members);
    }

    // ── TEST 1: Successful booking ────────────────────────────────────────────
    @Test
    void testBookLessonSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        Booking booking = bookingSystem.bookLesson(member, lesson);

        assertNotNull(booking, "Booking should be created");
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

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.bookLesson(members.get(4), lesson),
                "Should throw when lesson is full");
    }

    // ── TEST 3: Booking fails when time conflict exists ───────────────────────
    @Test
    void testBookLessonFailsOnTimeConflict() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0); // Weekend 1, Sat Morning

        bookingSystem.bookLesson(member, lesson);

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.bookLesson(member, lesson),
                "Should throw on time conflict");
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

        Booking original = bookingSystem.bookLesson(member, oldLesson);
        Booking changed = bookingSystem.changeBooking(member, oldLesson, newLesson);

        assertNotNull(changed);
        assertEquals(0, oldLesson.getBookings().size());
        assertEquals(1, newLesson.getBookings().size());
    }

    // ── TEST 6: Change booking fails when new lesson is full ──────────────────
    @Test
    void testChangeBookingFailsWhenNewLessonFull() {
        Member member = members.get(0);
        Lesson oldLesson = timetable.getAllLessons().get(0);
        Lesson newLesson = timetable.getAllLessons().get(1);

        // Book old lesson for member
        bookingSystem.bookLesson(member, oldLesson);

        // Fill the new lesson to capacity with different members
        bookingSystem.bookLesson(members.get(1), newLesson);
        bookingSystem.bookLesson(members.get(2), newLesson);
        bookingSystem.bookLesson(members.get(3), newLesson);
        bookingSystem.bookLesson(members.get(4), newLesson);

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.changeBooking(member, oldLesson, newLesson),
                "Should throw when new lesson is full");

        assertEquals(1, oldLesson.getBookings().size(),
                "Original booking should be restored after failed change");
    }

    // ── TEST 7: Submit review succeeds ────────────────────────────────────────
    @Test
    void testSubmitReviewSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);
        Review review = bookingSystem.submitReview(member, lesson, 5, "Excellent!");

        assertNotNull(review);
        assertEquals(1, bookingSystem.getAllReviews().size());
    }

    // ── TEST 8: Review fails with invalid rating ──────────────────────────────
    @Test
    void testSubmitReviewFailsWithInvalidRating() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);

        assertThrows(IllegalArgumentException.class,
                () -> bookingSystem.submitReview(member, lesson, 6, "Bad rating"));
    }

    // ── TEST 9: Review fails if member never booked the lesson ────────────────
    @Test
    void testSubmitReviewFailsIfNotBooked() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.submitReview(member, lesson, 4, "Never attended"));
    }

    // ── TEST 10: Cancel booking succeeds ─────────────────────────────────────
    @Test
    void testCancelBookingSuccess() {
        Member member = members.get(0);
        Lesson lesson = timetable.getAllLessons().get(0);

        bookingSystem.bookLesson(member, lesson);
        bookingSystem.cancelBooking(member, lesson);

        assertEquals(0, lesson.getBookings().size());
    }
}