package stopwatch.internal

import stopwatch.api.Metric
import stopwatch.api.ReportingStrategy
import stopwatch.api.TracerState
import stopwatch.internal.metric.*

internal val metricsBag: MutableList<Metric> by lazy { mutableListOf<Metric>() }

internal fun MutableList<Metric>.getPendingMetrics() =
        filter { it.getDuration() != null && it.getDuration() != 0L }

internal object MetricsTracer : Tracer {

    internal var reporter: ReportingStrategy? = null
        set(value) {
            field = value
            metricsBag.getPendingMetrics().map {
                it.report()
            }
        }

    override fun startTracing(key: String): TracerState {
        val currentTime = System.currentTimeMillis()
        return findMetric(key)?.let {
            when {
                it.isPending() -> it.report()
                it.inProgress() -> TracerState.Ignored
                else -> {
                    it.mutate(startTime = currentTime)
                    TracerState.InProgress
                }
            }
        } ?: TracerState.NotFound
    }

    override fun stopTracing(key: String): TracerState {
        val currentTime = System.currentTimeMillis()
        return findMetric(key)?.let {
            when {
                it.isPending() -> it.report()
                it.inProgress() -> it.mutate(endTime = currentTime).report()
                else -> TracerState.Ignored
            }
        } ?: TracerState.NotFound
    }

    override fun abortTracing(key: String): TracerState {
        return findMetric(key)?.reset()?.let {
            TracerState.Aborted
        } ?: TracerState.NotFound
    }

    private fun findMetric(key: String): Metric? {
        return metricsBag.find { it.key == key }
    }

    internal fun Metric.report(): TracerState {
        return reporter?.let { doReport ->
            doReport(this.event)
            reset()
            if (event.singleCapture)
                metricsBag.remove(this)

            TracerState.Released
        } ?: TracerState.Pending
    }
}