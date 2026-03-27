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
    private val reportRepository = ReportRepository.get()
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    init {
        loadPublicReports()
    }
    fun loadPublicReports() {
        viewModelScope.launch {
            reportRepository.getPublicReports().collect {
                _reports.value = it
            }
        }
    }
    fun loadUserReports() {
        viewModelScope.launch {
            reportRepository.getUserReports().collect {
                _reports.value = it
            }
        }
    }
    fun addUserReport(report: Report) {
        viewModelScope.launch {
            reportRepository.addUserReport(report)
        }
    }

    // clears all reports from current logged in user
    fun clearUserReports() {
        viewModelScope.launch {
            reportRepository.clearUserReports()
        }
    }
}