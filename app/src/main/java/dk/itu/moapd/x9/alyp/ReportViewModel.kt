package dk.itu.moapd.x9.alyp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.Date
import java.util.UUID

class ReportViewModel() : ViewModel() {
    val reports: MutableLiveData<List<Report>> by lazy {
        MutableLiveData<List<Report>>()
    }

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
        reports.value = initial
    }

    fun addReport(report: Report) {
        val current = reports.value.orEmpty()
        reports.value = current + report
    }
}