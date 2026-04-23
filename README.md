# Furzefield Leisure Centre — Booking System

A console-based Java application for managing exercise class bookings at Furzefield Leisure Centre. Developed as part of the 7COM1025 module at the University of Hertfordshire.

## Features

- **Timetable browsing** — view lessons by day or exercise type
- **Booking management** — book, change, and cancel lessons with capacity and conflict checks
- **Attendance & reviews** — mark attendance and submit 1–5 star ratings
- **Reports** — attendance/rating report and income-by-exercise-type report
- **Business rule enforcement** — max 4 members per lesson, no double-booking the same time slot, no changes to attended/cancelled bookings

## Prerequisites

- Java 25+
- Maven 3.x (or use the included Maven Wrapper)

## Build & Run

**Build:**

```bash
./mvnw clean package
```

or on Windows

```bash
mvnw.cmd clean package
```

**Run from JAR:**

```bash
java -jar target/FurzefieldLeisureCentre-1.0-SNAPSHOT.jar
```

or

```bash
java -jar out/artifacts/FurzefieldLeisureCentre.jar
```

**Run tests:**

```bash
./mvnw test
```

or on Windows

```bash
mvnw.cmd test
```

## Project Structure

```
src/main/java/com/furzefield/
├── Main.java                  # CLI entry point
├── model/                     # Domain entities (Member, Lesson, Booking, Review)
├── service/                   # Business logic (BookingSystem, Timetable, ReportService)
├── enums/                     # Constants (ExerciseType, Day, TimeSlot, BookingStatus)
└── data/                      # Seed data (11 members, 48 lessons, initial bookings & reviews)

src/test/java/com/furzefield/
└── BookingSystemTest.java     # 21 JUnit 5 unit tests
```

## Menu Options

| Key | Action                          |
| --- | ------------------------------- |
| 1   | View timetable by day           |
| 2   | View timetable by exercise type |
| 3   | Book a lesson                   |
| 4   | Change a booking                |
| 5   | Cancel a booking                |
| 6   | Attend a lesson & submit review |
| 7   | View all bookings               |
| 8   | Attendance & ratings report     |
| 9   | Income by exercise type report  |
| 0   | Exit                            |

## Usage

When the application starts, it loads pre-seeded data (11 members, 48 lessons across 8 weekends, and existing bookings and reviews) and drops you into the main menu. At any prompt, enter `0` to go back to the main menu.

**Browsing the timetable**

Select option `1` to view lessons for a specific day, or option `2` to filter by exercise type. Each lesson shows its ID, day, time slot, exercise type, instructor, available spaces, and average rating.

**Booking a lesson**

1. Select option `3`
2. Choose a member from the list
3. Browse lessons by day or exercise type, or enter a lesson ID directly
4. The system will confirm the booking or explain why it was rejected (lesson full, time conflict, etc.)

**Changing or cancelling a booking**

Select option `4` or `5`, choose the member, then select which booking to change or cancel. Attended and cancelled bookings cannot be modified.

**Attending a lesson and leaving a review**

1. Select option `6`
2. Choose the member and their active booking
3. Enter a rating from 1 to 5 and an optional comment
4. The booking status updates to ATTENDED

**Viewing reports**

- Option `8` — Attendance & ratings report, filtered by month (month 1 = weekends 1–4, month 2 = weekends 5–8)
- Option `9` — Income report ranked by exercise type, showing total revenue and attendance counts

## Exercise Types & Pricing

| Exercise   | Price per Session |
| ---------- | ----------------- |
| Box Fit    | £15               |
| Yoga       | £12               |
| Body Blitz | £11               |
| Zumba      | £10               |
| Aquacise   | £8                |

## Business Rules

- Lessons have a maximum capacity of **4 members**
- Members cannot book two lessons in the **same time slot on the same weekend day**
- Attended and cancelled bookings **cannot be changed or cancelled**
- A member must have an **active booking** to attend a lesson
- A member **cannot attend the same lesson twice**
- Cancelled bookings are **retained** in the system to preserve booking ID history
- Review ratings must be between **1 and 5**
