package com.exoforce.core.utils

object TimeUtils {
    fun formatDurationHuman(seconds: Int): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> {
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                if (remainingSeconds == 0) "${minutes}min"
                else "${minutes}min ${remainingSeconds}s"
            }
            else -> {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                if (minutes == 0) "${hours}h"
                else "${hours}h ${minutes}min"
            }
        }
    }

    fun formatDurationDigits(seconds: Int): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            "${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        } else {
            "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }
    }
}