package dk.itu.moapd.x9.alyp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.Date
import java.util.UUID

private const val TAG = "ReportViewModel"

class ReportViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val reportList = arrayListOf<Report>(
        Report(UUID.randomUUID(), "demo report", "ITU", Date(), "stuDying", "lllorem ipsum", "")
    )

    val getReportList: List<Report>
        get() = reportList

    fun setReportList(report: Report) {
        reportList.add(report)
    }
}