package dk.itu.moapd.x9.alyp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.ui.ReportRepository
import dk.itu.moapd.x9.alyp.model.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "ReportViewModel"
class ReportViewModel : ViewModel() {
    private val reportRepository by lazy { ReportRepository() }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    init {
        loadPublicReports()
    }
    val userId = auth.currentUser?.uid

    // coroutine scopes to run on separate thread, can run asynchronous code. Non blocking while querying database.
    // work that needs to be executed only if the viewmodel is active. automatically canceled if the viewmodel is cleared.
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

    // clears all reports from current logged in user
    fun clearUserReports(reportUid: String) {
        viewModelScope.launch {
            reportRepository.clearUserReports(userId, reportUid)
            loadUserReports()
        }
    }

    fun upvoteReport(reportUid: String, onResult: (success: Boolean, newCount: Int) -> Unit) {
        val uid = userId ?: return
        reportRepository.upvoteReport(reportUid, uid, onResult)
    }

    suspend fun hasUserVoted(reportUid: String): Boolean {
        val uid = userId ?: return false
        return reportRepository.hasUserVoted(reportUid, uid)
    }
}