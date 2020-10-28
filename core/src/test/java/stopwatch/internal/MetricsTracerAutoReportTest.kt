package stopwatch.internal

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import stopwatch.api.ReportingStrategy
import stopwatch.api.registerMetrics
import stopwatch.api.resetTracer
import stopwatch.api.setReportingStrategy
import stopwatch.internal.metric.mutate

class MetricsTracerAutoReportTest {

    init {
        MetricsTracer.reporter = null //reset the reporter state
    }

    @Before
    fun setup() {
        resetTracer()
    }

    @Test
    fun `should report asap when a reporter is setup and there is pending metric`() {
        //Emulate
        val reporter: ReportingStrategy = mockk()
        metric1.mutate(123, 321)

        every { reporter(any()) } returns Unit

        //Invoke
        registerMetrics(metric1)

        setReportingStrategy(reporter)

        //Validate
        verify(exactly = 1) { reporter(any()) }
    }

    @Test
    fun `should report asap when a reporter is setup and there is pending metrics`() {
        //Emulate
        val reporter: ReportingStrategy = mockk()
        metric1.mutate(123, 321)
        metric2.mutate(123, 123123)

        every { reporter(any()) } returns Unit

        //Invoke
        registerMetrics(metric1, metric2)

        setReportingStrategy(reporter)

        //Validate
        verify(exactly = 2) { reporter(any()) }
    }

    @Test
    fun `should not report anything given no pending metrics`() {
        //Emulate
        val reporter: ReportingStrategy = mockk()
        metric1.mutate(123)
        metric2.mutate(123)

        every { reporter(any()) } returns Unit

        //Invoke
        registerMetrics(metric1, metric2)

        setReportingStrategy(reporter)

        //Validate
        verify(exactly = 0) { reporter(any()) }
    }
}