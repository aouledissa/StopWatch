package stopwatch.internal.metric

import stopwatch.api.StopWatchEvent

data class PerformanceEvent(
        override val name: String,
        val singleCapture: Boolean,
        val startTime: Long? = null,
        val endTime: Long? = null
) : StopWatchEvent {
    override val duration: Long? = getDuration()
    internal fun getDuration(): Long? {
        return if (startTime != null && endTime != null)
            endTime - startTime
        else
            null
    }
}