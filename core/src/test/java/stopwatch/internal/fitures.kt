package stopwatch.internal

import stopwatch.api.Metric
import stopwatch.internal.metric.PerformanceEvent

val metric1 = object : Metric {
    override var event = PerformanceEvent(name = "Metric_1", singleCapture = true)
    override val key = "Metric_1"
}

val metric2 = object : Metric {
    override var event = PerformanceEvent(name = "Metric_2", singleCapture = false)
    override val key = "Metric_2"
}