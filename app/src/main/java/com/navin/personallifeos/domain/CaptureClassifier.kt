package com.navin.personallifeos.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters


enum class CaptureKind {
    TASK,
    REMINDER,
    DIARY,
    IDEA,
    ACTIVITY,
}

data class CaptureSuggestion(
    val kind: CaptureKind,
    val originalText: String,
    val title: String,
    val dueAt: Long? = null,
    val reminderAt: Long? = null,
    val priority: Int = 0,
    val projectHint: String? = null,
    val durationMinutes: Int? = null,
)

object CaptureClassifier {
    private val time12Pattern = Regex("(?i)\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b")
    private val time24Pattern = Regex("\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b")
    private val bareAtTimePattern = Regex("(?i)\\b(?:at|@)\\s*(\\d{1,2})(?::(\\d{2}))?\\b")
    private val relativePattern = Regex("(?i)\\b(?:in|after)\\s+(\\d+)\\s*(minutes?|mins?|hours?|hrs?|days?|weeks?)\\b")
    private val fromNowPattern = Regex("(?i)\\b(\\d+)\\s*(minutes?|mins?|hours?|hrs?|days?|weeks?)\\s+from\\s+now\\b")
    private val numericDatePattern = Regex("\\b(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{2,4}))?\\b")
    private val namedMonthFirstPattern = Regex("(?i)\\b(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\\s+(\\d{1,2})(?:st|nd|rd|th)?(?:,?\\s+(\\d{4}))?\\b")
    private val namedDayFirstPattern = Regex("(?i)\\b(\\d{1,2})(?:st|nd|rd|th)?\\s+(jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:t(?:ember)?)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)(?:\\s+(\\d{4}))?\\b")
    private val weekdayPattern = Regex("(?i)\\b(?:(next)\\s+)?(monday|tuesday|wednesday|thursday|friday|saturday|sunday)\\b")
    private val durationPattern = Regex("(?i)\\b(?:for\\s+)?(\\d+(?:\\.\\d+)?)\\s*(minutes?|mins?|hours?|hrs?)\\b")

    fun classify(text: String, nowMillis: Long = System.currentTimeMillis()): CaptureSuggestion {
        val clean = text.trim()
        val lower = clean.lowercase()
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), zone)
        val temporal = parseTemporal(lower, now)

        val explicitReminder = hasAny(
            lower,
            "remind me",
            "remind ",
            "notify me",
            "alert me",
            "set an alarm",
            "alarm me",
            "don't let me forget",
            "dont let me forget",
            "remember to",
        )
        val ideaIntent = lower.startsWith("idea:") || lower.startsWith("idea ") ||
            lower.contains("what if") || lower.startsWith("concept:") ||
            lower.contains("maybe we could") || lower.contains("would be cool if")
        val diaryIntent = lower.startsWith("diary") || lower.startsWith("journal") ||
            lower.contains("save this to my journal") || lower.contains("save this in my journal") ||
            lower.contains("note this in my diary") || lower.contains("worth remembering") ||
            lower.contains("i felt ") || lower.contains("i feel ") || lower.contains("i'm feeling") ||
            lower.contains("im feeling") || lower.startsWith("today felt") || lower.startsWith("today was")
        val activityIntent = looksLikeCompletedActivity(lower)
        val timedAction = temporal?.explicitTime == true && looksLikeAction(lower)

        val kind = when {
            ideaIntent -> CaptureKind.IDEA
            diaryIntent -> CaptureKind.DIARY
            activityIntent -> CaptureKind.ACTIVITY
            explicitReminder || timedAction -> CaptureKind.REMINDER
            else -> CaptureKind.TASK
        }

        val reminderAt = if (kind == CaptureKind.REMINDER) {
            temporal?.toMillis(now, zone, defaultTime = LocalTime.of(9, 0))
        } else null
        val dueAt = when (kind) {
            CaptureKind.REMINDER -> reminderAt
            CaptureKind.TASK -> temporal?.toMillis(now, zone, defaultTime = LocalTime.of(18, 0))
            else -> null
        }

        return CaptureSuggestion(
            kind = kind,
            originalText = clean,
            title = titleFor(clean, kind),
            dueAt = dueAt,
            reminderAt = reminderAt,
            priority = parsePriority(lower),
            projectHint = parseProjectHint(clean),
            durationMinutes = if (kind == CaptureKind.ACTIVITY) parseDurationMinutes(lower) else null,
        )
    }

    private fun looksLikeCompletedActivity(lower: String): Boolean {
        val startsPast = Regex("^(worked|practiced|practised|studied|exercised|walked|ran|meditated|watched|read|spent|coded|edited|designed|wrote|recorded)\\b").containsMatchIn(lower)
        val explicitPast = lower.contains("i worked") || lower.contains("i practiced") || lower.contains("i studied") || lower.contains("i spent")
        return startsPast || explicitPast
    }

    private fun looksLikeAction(lower: String): Boolean {
        return Regex("(?i)\\b(work|call|send|finish|start|check|buy|pay|practice|practise|study|take|go|meet|watch|post|upload|review|write|read|do|book|renew|submit|publish|record|edit|design|meeting|appointment)\\b").containsMatchIn(lower)
    }

    private fun parsePriority(lower: String): Int = when {
        hasAny(lower, "urgent", "asap", "high priority", "important", "must do") -> 2
        hasAny(lower, "low priority", "whenever", "not urgent") -> -1
        else -> 0
    }

    private fun parseDurationMinutes(lower: String): Int? {
        val match = durationPattern.find(lower) ?: return null
        val amount = match.groupValues[1].toDoubleOrNull() ?: return null
        return when {
            match.groupValues[2].startsWith("hour") || match.groupValues[2].startsWith("hr") -> (amount * 60).toInt()
            else -> amount.toInt()
        }.takeIf { it > 0 }
    }

    private fun parseProjectHint(text: String): String? {
        val patterns = listOf(
            Regex("(?i)\\b(?:work(?:ed|ing)?\\s+on|continue\\s+|project\\s+)([A-Za-z0-9][A-Za-z0-9 _-]{1,40}?)(?=\\s+(?:today|tomorrow|tonight|at|by|in\\s+\\d|for\\s+\\d|next\\s+)|[,.!?]|$)"),
            Regex("(?i)\\bfor\\s+project\\s+([A-Za-z0-9][A-Za-z0-9 _-]{1,40}?)(?=\\s+(?:today|tomorrow|tonight|at|by|in\\s+\\d|for\\s+\\d|next\\s+)|[,.!?]|$)"),
        )
        return patterns.asSequence()
            .mapNotNull { it.find(text)?.groupValues?.getOrNull(1)?.trim() }
            .firstOrNull { hint -> hint.isNotBlank() && hint.none { it.isDigit() } }
    }

    private fun titleFor(text: String, kind: CaptureKind): String {
        val value = when (kind) {
            CaptureKind.IDEA -> text
                .replace(Regex("(?i)^(idea|concept)[:\\s-]*"), "")
                .trim()
            CaptureKind.DIARY -> text
                .replace(Regex("(?i)^(diary|journal)[:\\s-]*"), "")
                .replace(Regex("(?i)\\.?\\s*(save|put|keep)\\s+this\\s+(?:to|in)\\s+my\\s+(?:journal|diary)\\.?$"), "")
                .trim()
                .substringBefore(".")
            CaptureKind.ACTIVITY -> text
                .replace(durationPattern, "")
                .replace(Regex("(?i)\\bfor\\s*$"), "")
                .trim(' ', '.', ',', ';', ':', '-')
            CaptureKind.TASK, CaptureKind.REMINDER -> cleanActionTitle(text)
        }
        return value.cleanSpaces().trim(' ', '.', ',', ';', ':', '-').take(90)
            .ifBlank { text.trim().take(90) }
            .replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
    }

    private fun cleanActionTitle(text: String): String {
        var value = text
        value = value.replace(Regex("(?i)\\b(?:please\\s+)?(?:remind\\s+me|notify\\s+me|alert\\s+me)(?:\\s+to)?\\b"), "")
        value = value.replace(Regex("(?i)\\b(?:don't|dont)\\s+let\\s+me\\s+forget(?:\\s+to)?\\b"), "")
        value = value.replace(Regex("(?i)\\bremember\\s+to\\b"), "")
        value = value.replace(Regex("(?i)\\bset\\s+an\\s+alarm(?:\\s+to)?\\b"), "")
        value = value.replace(relativePattern, "")
        value = value.replace(fromNowPattern, "")
        value = value.replace(Regex("(?i)\\b(?:at|@)\\s*\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)\\b"), "")
        value = value.replace(Regex("(?i)\\b\\d{1,2}(?::\\d{2})?\\s*(?:am|pm)\\b"), "")
        value = value.replace(Regex("\\b(?:[01]?\\d|2[0-3]):[0-5]\\d\\b"), "")
        value = value.replace(Regex("(?i)\\b(?:at|@)\\s*\\d{1,2}(?::\\d{2})?\\b"), "")
        value = value.replace(Regex("(?i)\\bday\\s+after\\s+tomorrow\\b"), "")
        value = value.replace(Regex("(?i)\\b(?:today|tomorrow|tonight|this\\s+morning|this\\s+afternoon|this\\s+evening|morning|afternoon|evening|noon|midnight|next\\s+week)\\b"), "")
        value = value.replace(weekdayPattern, "")
        value = value.replace(namedMonthFirstPattern, "")
        value = value.replace(namedDayFirstPattern, "")
        value = value.replace(numericDatePattern, "")
        value = value.replace(Regex("(?i)^\\s*(?:to|on|by|at)\\s+"), "")
        value = value.replace(Regex("(?i)\\s+(?:on|by|at)\\s*$"), "")
        return value.cleanSpaces()
    }

    private fun parseTemporal(lower: String, now: LocalDateTime): TemporalMatch? {
        relativePattern.find(lower)?.let { match ->
            val amount = match.groupValues[1].toLongOrNull() ?: return@let
            val unit = match.groupValues[2]
            val exact = addRelative(now, amount, unit)
            return TemporalMatch(exactDateTime = exact, explicitDate = true, explicitTime = true)
        }
        fromNowPattern.find(lower)?.let { match ->
            val amount = match.groupValues[1].toLongOrNull() ?: return@let
            val unit = match.groupValues[2]
            val exact = addRelative(now, amount, unit)
            return TemporalMatch(exactDateTime = exact, explicitDate = true, explicitTime = true)
        }

        val date = parseDate(lower, now.toLocalDate())
        val time = parseTime(lower)
        if (date == null && time == null) return null
        return TemporalMatch(
            date = date?.first,
            time = time,
            explicitDate = date != null,
            explicitTime = time != null,
        )
    }

    private fun addRelative(now: LocalDateTime, amount: Long, unit: String): LocalDateTime = when {
        unit.startsWith("min") -> now.plusMinutes(amount)
        unit.startsWith("hour") || unit.startsWith("hr") -> now.plusHours(amount)
        unit.startsWith("week") -> now.plusWeeks(amount)
        else -> now.plusDays(amount)
    }

    private fun parseDate(text: String, today: LocalDate): Pair<LocalDate, String>? {
        when {
            text.contains("day after tomorrow") -> return today.plusDays(2) to "day after tomorrow"
            text.contains("tomorrow") -> return today.plusDays(1) to "tomorrow"
            text.contains("today") || text.contains("tonight") || text.contains("this morning") || text.contains("this afternoon") || text.contains("this evening") -> return today to "today"
            text.contains("next week") -> return today.plusWeeks(1) to "next week"
        }

        weekdayPattern.find(text)?.let { match ->
            val next = match.groupValues[1].isNotBlank()
            val target = weekday(match.groupValues[2]) ?: return@let
            val date = if (next) today.with(TemporalAdjusters.next(target)) else today.with(TemporalAdjusters.nextOrSame(target))
            return date to match.value
        }

        namedMonthFirstPattern.find(text)?.let { match ->
            val month = month(match.groupValues[1]) ?: return@let
            val day = match.groupValues[2].toIntOrNull() ?: return@let
            val year = match.groupValues[3].toIntOrNull()
            resolveDate(today, year, month, day)?.let { return it to match.value }
        }

        namedDayFirstPattern.find(text)?.let { match ->
            val day = match.groupValues[1].toIntOrNull() ?: return@let
            val month = month(match.groupValues[2]) ?: return@let
            val year = match.groupValues[3].toIntOrNull()
            resolveDate(today, year, month, day)?.let { return it to match.value }
        }

        numericDatePattern.find(text)?.let { match ->
            val day = match.groupValues[1].toIntOrNull() ?: return@let
            val monthNumber = match.groupValues[2].toIntOrNull() ?: return@let
            var year = match.groupValues[3].toIntOrNull()
            if (year != null && year < 100) year += 2000
            val month = runCatching { Month.of(monthNumber) }.getOrNull() ?: return@let
            resolveDate(today, year, month, day)?.let { return it to match.value }
        }
        return null
    }

    private fun resolveDate(today: LocalDate, explicitYear: Int?, month: Month, day: Int): LocalDate? {
        val initialYear = explicitYear ?: today.year
        var candidate = runCatching { LocalDate.of(initialYear, month, day) }.getOrNull() ?: return null
        if (explicitYear == null && candidate.isBefore(today)) {
            candidate = runCatching { LocalDate.of(today.year + 1, month, day) }.getOrNull() ?: candidate
        }
        return candidate
    }

    private fun parseTime(text: String): LocalTime? {
        parse12HourTime(text)?.let { return it }
        parse24HourTime(text)?.let { return it }
        when {
            text.contains("noon") -> return LocalTime.NOON
            text.contains("midnight") -> return LocalTime.MIDNIGHT
            text.contains("tonight") -> return LocalTime.of(20, 0)
            text.contains("morning") -> return LocalTime.of(9, 0)
            text.contains("afternoon") -> return LocalTime.of(15, 0)
            text.contains("evening") -> return LocalTime.of(19, 0)
        }
        bareAtTimePattern.find(text)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let
            val minute = match.groupValues[2].toIntOrNull() ?: 0
            if (hour in 0..23 && minute in 0..59) return LocalTime.of(hour, minute)
        }
        return null
    }

    private fun parse12HourTime(text: String): LocalTime? {
        val match = time12Pattern.find(text) ?: return null
        var hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull() ?: 0
        val meridiem = match.groupValues[3].lowercase()
        if (hour !in 1..12 || minute !in 0..59) return null
        if (meridiem == "pm" && hour != 12) hour += 12
        if (meridiem == "am" && hour == 12) hour = 0
        return LocalTime.of(hour, minute)
    }

    private fun parse24HourTime(text: String): LocalTime? {
        val match = time24Pattern.find(text) ?: return null
        return LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    }

    private fun month(value: String): Month? = when (value.lowercase().take(3)) {
        "jan" -> Month.JANUARY
        "feb" -> Month.FEBRUARY
        "mar" -> Month.MARCH
        "apr" -> Month.APRIL
        "may" -> Month.MAY
        "jun" -> Month.JUNE
        "jul" -> Month.JULY
        "aug" -> Month.AUGUST
        "sep" -> Month.SEPTEMBER
        "oct" -> Month.OCTOBER
        "nov" -> Month.NOVEMBER
        "dec" -> Month.DECEMBER
        else -> null
    }

    private fun weekday(value: String): DayOfWeek? = when (value.lowercase()) {
        "monday" -> DayOfWeek.MONDAY
        "tuesday" -> DayOfWeek.TUESDAY
        "wednesday" -> DayOfWeek.WEDNESDAY
        "thursday" -> DayOfWeek.THURSDAY
        "friday" -> DayOfWeek.FRIDAY
        "saturday" -> DayOfWeek.SATURDAY
        "sunday" -> DayOfWeek.SUNDAY
        else -> null
    }

    private fun hasAny(text: String, vararg values: String): Boolean = values.any(text::contains)

    private fun String.cleanSpaces(): String = replace(Regex("\\s+"), " ").trim()

    private data class TemporalMatch(
        val date: LocalDate? = null,
        val time: LocalTime? = null,
        val exactDateTime: LocalDateTime? = null,
        val explicitDate: Boolean = false,
        val explicitTime: Boolean = false,
    ) {
        fun toMillis(now: LocalDateTime, zone: ZoneId, defaultTime: LocalTime): Long? {
            exactDateTime?.let { return it.atZone(zone).toInstant().toEpochMilli() }
            val chosenDate = date ?: now.toLocalDate()
            val chosenTime = time ?: defaultTime
            var candidate = LocalDateTime.of(chosenDate, chosenTime)
            if (!explicitDate && candidate.isBefore(now)) candidate = candidate.plusDays(1)
            if (explicitDate && candidate.isBefore(now)) return null
            return candidate.atZone(zone).toInstant().toEpochMilli()
        }
    }
}
