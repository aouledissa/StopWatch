package stopwatch.api

interface StopWatchEvent {
    val name: String
    val duration: Long?
}