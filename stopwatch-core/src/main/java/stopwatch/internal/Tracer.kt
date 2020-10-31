package stopwatch.internal

import stopwatch.api.TracerState

internal interface Tracer {
    fun startTracing(key: String): TracerState
    fun stopTracing(key: String): TracerState
    fun abortTracing(key: String): TracerState
}