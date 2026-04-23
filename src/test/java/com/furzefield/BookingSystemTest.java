package com.furzefield;

import com.furzefield.data.DataSetup;
import com.furzefield.enums.BookingStatus;
import com.furzefield.enums.Day;
import com.furzefield.enums.ExerciseType;
import com.furzefield.model.Booking;
import com.furzefield.model.Lesson;
import com.furzefield.model.Member;
import com.furzefield.model.Review;
import com.furzefield.service.BookingSystem;
import com.furzefield.service.Timetable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
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

    // ── 1. Booking ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("1a. Book a lesson successfully")
    void testBookLessonSuccess() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        Booking booking = bookingSystem.bookLesson(member, lesson);

        assertNotNull(booking, "Booking should be created");
        assertEquals(1, lesson.getBookings().size());
    }

    @Test
    @DisplayName("1b. Booking fails when lesson is full")
    void testBookLessonFailsWhenFull() {
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(members.get(0), lesson);
        bookingSystem.bookLesson(members.get(1), lesson);
        bookingSystem.bookLesson(members.get(2), lesson);
        bookingSystem.bookLesson(members.get(3), lesson);

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.bookLesson(members.get(4), lesson),
                "Should throw when lesson is full");
    }

    @Test
    @DisplayName("1c. Booking fails when time conflict exists")
    void testBookLessonFailsOnTimeConflict() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.bookLesson(member, lesson),
                "Should throw on time conflict");
    }

    @Test
    @DisplayName("1d. Max capacity is 4")
    void testMaxCapacityIsFour() {
        assertEquals(4, Lesson.MAX_CAPACITY);
    }

    // ── 2. Change booking ─────────────────────────────────────────────────────

    @Test
    @DisplayName("2a. Change booking succeeds")
    void testChangeBookingSuccess() {
        Member member = members.getFirst();
        Lesson oldLesson = timetable.getAllLessons().get(0);
        Lesson newLesson = timetable.getAllLessons().get(1);

        String originalId = bookingSystem.bookLesson(member, oldLesson).getBookingId();
        Booking changed = bookingSystem.changeBooking(member, oldLesson, newLesson);

        assertNotNull(changed);
        assertEquals(originalId, changed.getBookingId(), "Booking ID should be preserved on change");
        assertEquals(0, oldLesson.getBookings().size());
        assertEquals(1, newLesson.getBookings().size());
        assertEquals(BookingStatus.CHANGED, changed.getStatus());
    }

    @Test
    @DisplayName("2b. Change booking fails when new lesson is full")
    void testChangeBookingFailsWhenNewLessonFull() {
        Member member = members.getFirst();
        Lesson oldLesson = timetable.getAllLessons().get(0);
        Lesson newLesson = timetable.getAllLessons().get(1);

        bookingSystem.bookLesson(member, oldLesson);
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

    // ── 3. Cancel booking ─────────────────────────────────────────────────────

    @Test
    @DisplayName("3a. Cancel booking succeeds")
    void testCancelBookingSuccess() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);
        bookingSystem.cancelBooking(member, lesson);

        assertEquals(0, lesson.getBookings().size());
    }

// ── 4. Attend lesson & reviews ────────────────────────────────────────────

    @Test
    @DisplayName("4a. Attend lesson and submit review successfully")
    void testAttendLessonSuccess() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);
        Review review = bookingSystem.attendLesson(member, lesson, 5, "Excellent!");

        assertNotNull(review);
        assertEquals(1, bookingSystem.getAllReviews().size());
        assertEquals(BookingStatus.ATTENDED,
                bookingSystem.getAllBookings().getFirst().getStatus());
    }

    @Test
    @DisplayName("4b. Review fails with invalid rating")
    void testAttendLessonFailsWithInvalidRating() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);

        assertThrows(IllegalArgumentException.class,
                () -> bookingSystem.attendLesson(member, lesson, 6, "Bad rating"));
    }

    @Test
    @DisplayName("4c. Cannot attend lesson without a booking")
    void testAttendLessonFailsIfNotBooked() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        assertThrows(IllegalArgumentException.class,
                () -> bookingSystem.attendLesson(member, lesson, 4, "Never booked"));
    }

    @Test
    @DisplayName("4d. Cannot attend the same lesson twice")
    void testAttendLessonFailsIfAlreadyAttended() {
        Member member = members.getFirst();
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);
        bookingSystem.attendLesson(member, lesson, 5, "Great!");

        assertThrows(IllegalStateException.class,
                () -> bookingSystem.attendLesson(member, lesson, 4, "Again"));
    }

    // ── 5. Average rating ─────────────────────────────────────────────────────

    @Test
    @DisplayName("5a. Average rating calculates correctly")
    void testAverageRating() {
        Member member = members.get(0);
        Member member2 = members.get(1);
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);
        bookingSystem.bookLesson(member2, lesson);
        bookingSystem.attendLesson(member, lesson, 4, "Good");
        bookingSystem.attendLesson(member2, lesson, 2, "Not great");

        assertEquals(3.0, lesson.getAverageRating(), 0.001,
                "Average of 4 and 2 should be 3.0");
    }

    @Test
    @DisplayName("5b. No reviews returns 0.0 average")
    void testAverageRatingNoReviews() {
        Lesson lesson = timetable.getAllLessons().getFirst();
        assertEquals(0.0, lesson.getAverageRating(), 0.001);
    }

    // ── 6. Timetable lookups ──────────────────────────────────────────────────

    @Test
    @DisplayName("6a. Filter timetable by day returns correct lessons")
    void testGetLessonsByDay() {
        List<Lesson> satLessons = timetable.getLessonsByDay(Day.SATURDAY);
        assertTrue(satLessons.stream().allMatch(l -> l.getDay() == Day.SATURDAY),
                "All returned lessons should be on Saturday");
    }

    @Test
    @DisplayName("6b. Filter timetable by exercise type")
    void testGetLessonsByExercise() {
        List<Lesson> yogaLessons = timetable.getLessonsByExercise(ExerciseType.YOGA);
        assertTrue(yogaLessons.stream().allMatch(l -> l.getExerciseType() == ExerciseType.YOGA),
                "All returned lessons should be Yoga");
    }

    // ── 7. Income ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("7a. Income calculation is correct")
    void testTotalIncome() {
        Member member = members.get(0);
        Member member2 = members.get(1);
        Lesson lesson = timetable.getAllLessons().getFirst();

        bookingSystem.bookLesson(member, lesson);
        bookingSystem.bookLesson(member2, lesson);
        bookingSystem.attendLesson(member, lesson, 5, "Great!");
        bookingSystem.attendLesson(member2, lesson, 4, "Good!");

        // Yoga price = £12.00, 2 members → £24.00
        assertEquals(24.00, lesson.getTotalIncome(), 0.001);
    }

    // ── 8. Data integrity ─────────────────────────────────────────────────────

    @Test
    @DisplayName("8a. Full data load meets brief requirements")
    void testFullDataLoad() {
        DataSetup.createSeedBookings(bookingSystem, members, timetable);
        DataSetup.createSeedReviews(bookingSystem, members, timetable);

        assertTrue(members.size() >= 10,
                "Should have at least 10 members");

        long totalReviews = timetable.getAllLessons().stream()
                .mapToLong(l -> l.getReviews().size())
                .sum();
        assertTrue(totalReviews >= 20,
                "Should have at least 20 reviews, got: " + totalReviews);

        assertTrue(timetable.getAllLessons().size() >= 48,
                "Should have at least 48 lessons");
    }
}