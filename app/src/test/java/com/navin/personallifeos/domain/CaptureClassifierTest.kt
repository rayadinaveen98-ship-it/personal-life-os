package com.navin.personallifeos.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CaptureClassifierTest {
    @Test
    fun reminderPhrase_isClassifiedAndTimed() {
        val result = CaptureClassifier.classify("Tomorrow remind me at 10 AM to practice Blender")
        assertEquals(CaptureKind.REMINDER, result.kind)
        assertNotNull(result.reminderAt)
    }

    @Test
    fun ideaPhrase_isClassifiedAsIdea() {
        assertEquals(CaptureKind.IDEA, CaptureClassifier.classify("Idea: make weekly review visual").kind)
    }

    @Test
    fun diaryPhrase_isClassifiedAsDiary() {
        assertEquals(CaptureKind.DIARY, CaptureClassifier.classify("Diary: I felt good after finishing the build").kind)
    }

    @Test
    fun plainAction_isClassifiedAsTask() {
        assertEquals(CaptureKind.TASK, CaptureClassifier.classify("Finish the parser").kind)
    }
}
