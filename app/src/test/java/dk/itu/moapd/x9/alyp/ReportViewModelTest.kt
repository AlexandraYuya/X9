package dk.itu.moapd.x9.alyp

import org.junit.Assert.*
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import dk.itu.moapd.x9.alyp.model.Report
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import org.junit.Rule
import java.util.Date
import java.util.UUID

class ReportViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun reports_hasInitialReport() {
        val viewModel = ReportViewModel()

        val reports = viewModel.reports.value
        assertNotNull(reports)
        assertEquals(1, reports?.size)
        assertEquals("Report #0", reports?.first()?.title)
    }

    @Test
    fun addReport_appendsToExistingList() {
        val viewModel = ReportViewModel()
        val before = viewModel.reports.value

        val report = Report(
            id = UUID.randomUUID(),
            title = "Test report",
            location = "ITU",
            date = Date(),
            type = "Apocalypse",
            description = "Demo test for my unit test case",
            severity = "Major"
        )

        viewModel.addReport(report)

        val after = viewModel.reports.value
        assertEquals(before?.size?.plus(1), after?.size)
        assertEquals("Test report", after?.last()?.title)
    }

    @Test
    fun addReport_keepsInsertionOrder() {
        val viewModel = ReportViewModel()

        val first = Report(
            UUID.randomUUID(), "First", "ITU", Date(), "Type1", "Desc1", "Minor"
        )
        val second = Report(
            UUID.randomUUID(), "Second", "ITU", Date(), "Type2", "Desc2", "Major"
        )

        viewModel.addReport(first)
        viewModel.addReport(second)

        val reports = viewModel.reports.value
        assertEquals("Report #0", reports?.get(0)?.title)
        assertEquals("First", reports?.get(1)?.title)
        assertEquals("Second", reports?.get(2)?.title)
    }
}