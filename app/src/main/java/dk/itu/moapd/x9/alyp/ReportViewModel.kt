package dk.itu.moapd.x9.alyp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "ReportViewModel"
class ReportViewModel : ViewModel() {
    private val reportRepository = ReportRepository.get()
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports
//    val reports: MutableLiveData<List<Report>> by lazy {
//        MutableLiveData<List<Report>>()
//    }

    init {
        loadReports()
    }
    fun loadReports() {
        viewModelScope.launch {
            reportRepository.getReports().collect {
                _reports.value = it
            }
        }
    }
    fun addReport(report: Report) {
        viewModelScope.launch {
            reportRepository.addReport(report)
//            _reports.value = reportRepository.getReports()
        }
    }

    fun clearReports() {
        viewModelScope.launch {
            reportRepository.clearReports()
//            _reports.value = emptyList()
        }
    }
}