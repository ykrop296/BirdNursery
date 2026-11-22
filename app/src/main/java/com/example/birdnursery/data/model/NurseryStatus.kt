package com.example.birdnursery.data.model

fun feedingLevel(lastFeedingTime: Long?, feedingIntervalHours: Int, now: Long): Float {
    if (lastFeedingTime == null) return 0f
    val intervalMillis = feedingIntervalHours.coerceAtLeast(1) * 3_600_000L
    val remaining = intervalMillis - (now - lastFeedingTime).coerceAtLeast(0L)
    return (remaining.toFloat() / intervalMillis).coerceIn(0f, 1f)
}

fun hasHatched(expectedHatchDate: Long?, now: Long): Boolean {
    return expectedHatchDate != null && now >= expectedHatchDate
}

fun formatRemainingTime(millis: Long): String {
    val totalMinutes = (millis.coerceAtLeast(0L) + 59_999L) / 60_000L
    val days = totalMinutes / 1_440L
    val hours = (totalMinutes % 1_440L) / 60L
    val minutes = totalMinutes % 60L
    return when {
        days > 0 -> "$days д $hours ч"
        hours > 0 -> "$hours ч $minutes мин"
        minutes > 0 -> "$minutes мин"
        else -> "меньше минуты"
    }
}
