package com.exoforce.core.media

/**
 * BeepPlayer for generating countdown beep sounds using hertz frequencies.
 * Similar to classic timer/repeater apps.
 */
expect class BeepPlayer() {
    /**
     * Play a short beep at the specified frequency (in Hz).
     * @param frequency The frequency in Hz (e.g., 800, 1000, 1200)
     * @param durationMs The duration of the beep in milliseconds
     */
    fun playBeep(frequency: Int, durationMs: Long)

    /**
     * Stop any currently playing beep.
     */
    fun stop()

    /**
     * Release resources.
     */
    fun release()
}

/**
 * Standard beep frequencies and durations for countdown timers.
 */
object BeepConstants {
    const val FREQUENCY_NORMAL = 800 // Hz - for countdown 3, 2
    const val FREQUENCY_FINAL = 1200  // Hz - for countdown 1, 0
    const val DURATION_SHORT = 100L   // ms - for 3, 2, 1
    const val DURATION_LONG = 300L    // ms - for final beep (0)
}
