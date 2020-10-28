package stopwatch.internal

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import stopwatch.api.*
import stopwatch.internal.metric.mutate
import stopwatch.internal.metric.reset

class MetricsTracerWithoutReporterTest {

    init {
        MetricsTracer.reporter = null
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
    fun `should not start tracing given a not registered metric`() {
        //Emulate
        val unregisteredMetricKey = "INVALID"

        //Invoke
        val actual = startTracing(unregisteredMetricKey)

        //Validate
        assertThat(actual).isEqualTo(TracerState.NotFound)
    }

    @Test
    fun `should not stop tracing given a not registered metric`() {
        //Emulate
        val unregisteredMetricKey = "INVALID"

        //Invoke
        val actual = stopTracing(unregisteredMetricKey)

        //Validate
        assertThat(actual).isEqualTo(TracerState.NotFound)
    }

    @Test
    fun `should start tracing given a registered metric`() {
        //Invoke
        val actual = startTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.InProgress)
    }

    @Test
    fun `should ignore start tracing given a started metric`() {
        //Emulate
        metric1.mutate(startTime = 123)

        //Invoke
        val actual = startTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Ignored)
    }

    @Test
    fun `should ignore start tracing given a pending metric`() {
        //Emulate
        metric1.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = startTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Pending)
    }

    @Test
    fun `should stop tracing given a started metric`() {
        //Emulate
        metric1.mutate(startTime = 123)

        //Invoke
        val actual = stopTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Pending)
    }

    @Test
    fun `should ignore stop tracing given a pending metric`() {
        //Emulate
        metric1.mutate(startTime = 123, endTime = 321)

        //Invoke
        val actual = stopTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Pending)
    }

    @Test
    fun `should ignore stop tracing given a non started metric`() {
        val actual = stopTracing(metric1.key)

        assertThat(actual).isEqualTo(TracerState.Ignored)
    }

    @Test
    fun `should abort tracing given a registered metric`() {
        //Invoke
        val actual = abortTracing(metric1.key)

        //Validate
        assertThat(actual).isEqualTo(TracerState.Aborted)
    }

    @Test
    fun `should ignore abort tracing given a non registered metric`() {
        val actual = abortTracing("INVALID")

        //Validate
        assertThat(actual).isEqualTo(TracerState.NotFound)
    }
}