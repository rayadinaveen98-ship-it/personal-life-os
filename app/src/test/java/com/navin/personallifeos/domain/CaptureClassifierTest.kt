package com.navin.personallifeos.domain

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CaptureClassifierTest {
    private val zone = ZoneId.systemDefault()
    private val fixedNow = LocalDateTime.of(2026, 9, 5, 8, 0)
        .atZone(zone)
        .toInstant()
        .toEpochMilli()

    @Test
    fun reminderPhrase_isClassifiedTimedAndCleaned() {
        val result = CaptureClassifier.classify(
            "Tomorrow remind me at 10 AM to practice Blender",
            fixedNow,
        )
        assertEquals(CaptureKind.REMINDER, result.kind)
        assertEquals("Practice Blender", result.title)
        val time = result.reminderAt!!.asLocal()
        assertEquals(2026, time.year)
        assertEquals(9, time.monthValue)
        assertEquals(6, time.dayOfMonth)
        assertEquals(10, time.hour)
    }

    @Test
    fun relativeReminder_understandsMinutesFromNow() {
        val result = CaptureClassifier.classify("Remind me in 45 minutes to check the build", fixedNow)
        assertEquals(CaptureKind.REMINDER, result.kind)
        assertEquals(fixedNow + 45 * 60_000L, result.reminderAt)
    }

    @Test
    fun weekdayWithoutClock_isTaskWithDueDateNotAlarm() {
        val result = CaptureClassifier.classify("Finish the parser by Monday", fixedNow)
        assertEquals(CaptureKind.TASK, result.kind)
        assertNotNull(result.dueAt)
        assertNull(result.reminderAt)
        val due = result.dueAt!!.asLocal()
        assertEquals(DayOfWeek.MONDAY, due.dayOfWeek)
        assertEquals(18, due.hour)
    }

    @Test
    fun timedAction_canBecomeReminderWithoutMagicWords() {
        val result = CaptureClassifier.classify("Tomorrow at 9 AM work on CINEMA", fixedNow)
        assertEquals(CaptureKind.REMINDER, result.kind)
        assertEquals("Work on CINEMA", result.title)
        assertEquals("CINEMA", result.projectHint)
    }

    @Test
    fun namedDateAndEveningTime_areParsed() {
        val result = CaptureClassifier.classify("Remind me September 8 at 7 PM to publish the video", fixedNow)
        val time = result.reminderAt!!.asLocal()
        assertEquals(8, time.dayOfMonth)
        assertEquals(19, time.hour)
    }

    @Test
    fun ideaPhrase_isClassifiedAsIdea() {
        val result = CaptureClassifier.classify("Idea: make weekly review visual", fixedNow)
        assertEquals(CaptureKind.IDEA, result.kind)
        assertEquals("Make weekly review visual", result.title)
    }

    @Test
    fun diaryPhrase_winsEvenWhenItMentionsToday() {
        val result = CaptureClassifier.classify("Today felt productive. Save this to my journal.", fixedNow)
        assertEquals(CaptureKind.DIARY, result.kind)
        assertNull(result.reminderAt)
        assertNull(result.dueAt)
    }

    @Test
    fun completedWork_isCapturedAsActivityWithDurationAndProjectHint() {
        val result = CaptureClassifier.classify("Worked on Blender for 45 minutes", fixedNow)
        assertEquals(CaptureKind.ACTIVITY, result.kind)
        assertEquals(45, result.durationMinutes)
        assertEquals("Blender", result.projectHint)
        assertEquals("Worked on Blender", result.title)
    }

    @Test
    fun urgentLanguage_setsPriority() {
        val result = CaptureClassifier.classify("Urgent: finish the release checklist", fixedNow)
        assertEquals(CaptureKind.TASK, result.kind)
        assertEquals(2, result.priority)
    }

    @Test
    fun plainAction_isClassifiedAsTask() {
        val result = CaptureClassifier.classify("Finish the parser", fixedNow)
        assertEquals(CaptureKind.TASK, result.kind)
        assertNull(result.dueAt)
    }

    private fun Long.asLocal(): LocalDateTime = LocalDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(this),
        zone,
    )
}
