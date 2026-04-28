package dk.itu.moapd.x9.alyp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.repository.ReportRepository
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    private val reportRepository by lazy { ReportRepository() }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    private val _reports = MutableStateFlow<List<Report>>(emptyList())

    val reports: StateFlow<List<Report>> = _reports

    /**
     * viewModelScope is a coroutine scoped to run on separate thread, can run asynchronous code. Non blocking while querying database.
     * Work that needs to be executed only if the viewmodel is active. automatically canceled if the viewmodel is cleared.
     */
    fun loadPublicReports() {
        viewModelScope.launch {
            _reports.value = reportRepository.getPublicReports()
        }
    }
    fun loadUserReports() {
        viewModelScope.launch {
            _reports.value = reportRepository.getUserReports(userId)
        }
    }
    fun addUserReport(report: Report) {
        viewModelScope.launch {
            reportRepository.addUserReport(userId, report)
            loadPublicReports()
        }
    }
    fun deleteUserReport(reportUid: String) {
        viewModelScope.launch {
            reportRepository.deleteUserReport(userId, reportUid)
            loadUserReports()
        }
    }

    fun upvoteReport(reportUid: String, onResult: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            val (success, newCount) = reportRepository.upvoteReport(userId, reportUid)
            onResult(success, newCount)
        }
    }

    fun hasUserVoted(reportUid: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(reportRepository.hasUserVoted(userId, reportUid))
        }
    }
}