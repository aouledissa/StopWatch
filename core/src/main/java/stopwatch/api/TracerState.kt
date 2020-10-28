package stopwatch.api

sealed class TracerState {
    object Ignored : TracerState()
    object Pending : TracerState()
    object InProgress : TracerState()
    object Released : TracerState()
    object Aborted : TracerState()
    object NotFound : TracerState()
}