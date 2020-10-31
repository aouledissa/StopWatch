package stopwatch.api

import stopwatch.internal.MetricsTracer
import stopwatch.internal.metricsBag

typealias ReportingStrategy = (StopWatchEvent) -> Unit

fun registerMetrics(vararg metric: Metric) {
    metricsBag.addAll(metric)
}

fun startTracing(key: String) =
        MetricsTracer.startTracing(key)

fun stopTracing(key: String) =
        MetricsTracer.stopTracing(key)

fun abortTracing(key: String) =
        MetricsTracer.abortTracing(key)

fun setReportingStrategy(strategy: ReportingStrategy) {
    if (MetricsTracer.reporter == null) {
        MetricsTracer.reporter = strategy
    }
}

fun resetTracer() {
    metricsBag.clear()
}