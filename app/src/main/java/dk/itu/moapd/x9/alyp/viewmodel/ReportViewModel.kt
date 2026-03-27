package dk.itu.moapd.x9.alyp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.x9.alyp.ui.ReportRepository
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "ReportViewModel"
class ReportViewModel : ViewModel() {
    private val reportRepository = ReportRepository.Companion.get()
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

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
        }
    }

    // clears all reports from current logged in user
    fun clearReports() {
        viewModelScope.launch {
            reportRepository.clearReports()
        }
    }
}