package com.example.birdnursery.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NurseryStatusTest {
    @Test
    fun feedingLevel_withoutFeeding_isEmpty() {
        assertEquals(0f, feedingLevel(null, 4, 1_000L))
    }

    @Test
    fun feedingLevel_decreasesAndStopsAtZero() {
        val interval = 4L * 3_600_000L
        assertEquals(1f, feedingLevel(100L, 4, 100L))
        assertEquals(0.5f, feedingLevel(100L, 4, 100L + interval / 2))
        assertEquals(0f, feedingLevel(100L, 4, 100L + interval * 2))
    }

    @Test
    fun hasHatched_onlyAtOrAfterExpectedDate() {
        assertFalse(hasHatched(null, 2_000L))
        assertFalse(hasHatched(2_000L, 1_999L))
        assertTrue(hasHatched(2_000L, 2_000L))
    }

    @Test
    fun formatRemainingTime_usesReadableUnits() {
        assertEquals("1 д 2 ч", formatRemainingTime(26L * 3_600_000L))
        assertEquals("2 ч 5 мин", formatRemainingTime(125L * 60_000L))
        assertEquals("меньше минуты", formatRemainingTime(0L))
    }
}
