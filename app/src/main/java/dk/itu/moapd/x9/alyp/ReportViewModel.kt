package dk.itu.moapd.x9.alyp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "ReportViewModel"
class ReportViewModel : ViewModel() {
    val reports: MutableLiveData<List<Report>> by lazy {
        MutableLiveData<List<Report>>()
    }

    init {
        Log.d(TAG, "init starting")
//        viewModelScope.launch {
//            Log.d(TAG, "coroutine launched")
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
//            Log.d(TAG, "Loading reports finished")
//        }
    }
    suspend fun loadReports(): List<Report> {
        val result = mutableListOf<Report>()
        delay(1000)
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
        result += initial
        return result
    }
    fun addReport(report: Report) {
        val current = reports.value.orEmpty()
        reports.value = current + report
    }
}