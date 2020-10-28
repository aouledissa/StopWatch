package stopwatch.internal.metric

import stopwatch.api.Metric

internal fun Metric.mutate(startTime: Long? = null, endTime: Long? = null): Metric {
    startTime?.let {
        event = event.copy(startTime = startTime)
    }

    endTime?.let {
        event = event.copy(endTime = endTime)
    }

    return this
}

internal fun Metric.reset(): Metric {
    event = event.copy(startTime = null, endTime = null)
    return this
}

internal fun Metric.isPending(): Boolean = event.startTime != null && event.endTime != null

internal fun Metric.inProgress(): Boolean = event.startTime != null

internal fun Metric.getDuration() = event.getDuration()