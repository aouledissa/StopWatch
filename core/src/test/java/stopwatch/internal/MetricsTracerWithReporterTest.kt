package stopwatch.internal

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import stopwatch.api.*
import stopwatch.internal.metric.mutate
import stopwatch.internal.metric.reset

class MetricsTracerWithReporterTest {

    init {
        setReportingStrategy {
            println("${it.name} was tracked!!")
        }
    }

    @Before
    fun setup() {
        registerMetrics(metric1, metric2)
    }

    @After
    fun tearDown() {
        metric1.reset()
        metric2.reset()
        resetTracer()
    }

    @Test
    fun `should report when calling start tracing given a pending metric`() {
        //Emulate
        metric1.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = startTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Released)
    }

    @Test
    fun `should end tracing and report given a in progress metric`() {
        //Emulate
        metric1.mutate(startTime = 123)

        //Invoke
        val actual = stopTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Released)
    }

    @Test
    fun `should report given a pending metric`() {
        //Emulate
        metric1.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = stopTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Released)
    }

    @Test
    fun `should not trace again a single capture metric`() {
        //Emulate
        metric1.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = startTracing(metric1.key)
        val second = startTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Released)
        assertThat(second).isEqualTo(TracerState.NotFound)
    }

    @Test
    fun `should trace again a multiple capture metric`() {
        //Emulate
        metric2.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = startTracing(metric2.key)
        val second = startTracing(metric2.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Released)
        assertThat(second).isEqualTo(TracerState.InProgress)
    }
}