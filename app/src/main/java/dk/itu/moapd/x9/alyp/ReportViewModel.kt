package dk.itu.moapd.x9.alyp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.Date
import java.util.UUID

class ReportViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> = _reports

    init {
        val initial = List(1) { i ->
            Report(
                id = UUID.randomUUID(),
                title = "Report #$i",
                location = "ITU",
                date = Date(),
                type = "stuDying",
                description = "lorem ipsum",
                severity = "moderate"
            )
        }
        _reports.value = initial
    }

    fun addReport(report: Report) {
        val current = _reports.value.orEmpty()
        _reports.value = current + report
    }
}