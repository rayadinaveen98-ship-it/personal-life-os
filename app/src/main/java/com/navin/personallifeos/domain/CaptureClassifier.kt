package com.navin.personallifeos.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


enum class CaptureKind {
    TASK,
    REMINDER,
    DIARY,
    IDEA,
}

data class CaptureSuggestion(
    val kind: CaptureKind,
    val originalText: String,
    val title: String,
    val reminderAt: Long? = null,
)

object CaptureClassifier {
    private val timePattern = Regex("(?i)\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)\\b")
    private val time24Pattern = Regex("\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b")

    fun classify(text: String, nowMillis: Long = System.currentTimeMillis()): CaptureSuggestion {
        val clean = text.trim()
        val lower = clean.lowercase()
        val reminderAt = parseReminderTime(lower, nowMillis)

        val kind = when {
            lower.contains("remind me") || lower.startsWith("remind ") || reminderAt != null -> CaptureKind.REMINDER
            lower.startsWith("idea:") || lower.contains("what if") || lower.startsWith("idea ") -> CaptureKind.IDEA
            lower.startsWith("diary") || lower.startsWith("journal") ||
                lower.contains("i felt") || lower.contains("i feel") || lower.contains("worth remembering") -> CaptureKind.DIARY
            else -> CaptureKind.TASK
        }

        return CaptureSuggestion(
            kind = kind,
            originalText = clean,
            title = titleFor(clean, kind),
            reminderAt = if (kind == CaptureKind.REMINDER) reminderAt else null,
        )
    }

    private fun titleFor(text: String, kind: CaptureKind): String {
        val stripped = when (kind) {
            CaptureKind.REMINDER -> text
                .replace(Regex("(?i)^remind me\\s+(to\\s+)?"), "")
                .replace(Regex("(?i)^remind\\s+(to\\s+)?"), "")
            CaptureKind.IDEA -> text.replace(Regex("(?i)^idea:\\s*"), "")
            CaptureKind.DIARY -> text.replace(Regex("(?i)^(diary|journal)[:\\s-]*"), "")
            CaptureKind.TASK -> text
        }.trim()

        return stripped.take(90).ifBlank { text.take(90) }
    }

    private fun parseReminderTime(lower: String, nowMillis: Long): Long? {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(nowMillis), zone)
        val date = when {
            lower.contains("tomorrow") -> now.toLocalDate().plusDays(1)
            lower.contains("today") -> now.toLocalDate()
            else -> now.toLocalDate()
        }

        val time = parse12HourTime(lower) ?: parse24HourTime(lower) ?: when {
            lower.contains("morning") -> LocalTime.of(9, 0)
            lower.contains("afternoon") -> LocalTime.of(15, 0)
            lower.contains("evening") -> LocalTime.of(19, 0)
            lower.contains("tonight") -> LocalTime.of(20, 0)
            lower.contains("tomorrow") -> LocalTime.of(9, 0)
            else -> null
        } ?: return null

        var candidate = LocalDateTime.of(date, time)
        if (!lower.contains("tomorrow") && candidate.isBefore(now)) {
            candidate = candidate.plusDays(1)
        }
        return candidate.atZone(zone).toInstant().toEpochMilli()
    }

    private fun parse12HourTime(text: String): LocalTime? {
        val match = timePattern.find(text) ?: return null
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
}
