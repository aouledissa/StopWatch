package stopwatch.api

import stopwatch.internal.metric.PerformanceEvent

interface Metric {
    var event: PerformanceEvent
    val key: String
}