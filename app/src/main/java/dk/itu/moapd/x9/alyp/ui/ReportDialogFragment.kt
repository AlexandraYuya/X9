package dk.itu.moapd.x9.alyp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.FragmentReportDialogBinding
import dk.itu.moapd.x9.alyp.databinding.FragmentReportFormBinding
import dk.itu.moapd.x9.alyp.model.Report
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat

/**
 * ReportDialogFragment represents an alert dialog that pops up when a report is clicked.
 * Builder determines the content of the dialog.
 */
class ReportDialogFragment : DialogFragment() {

    private val reportViewModel: ReportViewModel by activityViewModels()
    fun toDate(date: Long) : String {
        return DateFormat.getInstance().format(date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val report: Report = requireArguments().getSerializable(REPORT_DIALOG) as Report
        val view = FragmentReportDialogBinding.inflate(layoutInflater) // one off inflation of the view, don't need persistent binding since we don't need to manage fragment's lifecycle.

        // Load upvote count and check if user already voted
        lifecycleScope.launch {
            view.upvoteCount.text = "${report.upvoteCount} confirmations"
            val alreadyVoted = reportViewModel.hasUserVoted(report.uid)
            view.upvoteButton.isEnabled = !alreadyVoted
            if (alreadyVoted) view.upvoteButton.text = "Already confirmed"
        }

        view.upvoteButton.setOnClickListener {
            reportViewModel.upvoteReport(report.uid) { success, newCount ->
                if (success) {
                    view.upvoteCount.text = "$newCount confirmations"
                    view.upvoteButton.isEnabled = false
                    view.upvoteButton.text = "Already confirmed"
                }
            }
        }

        if (report.imageUrl.isNotEmpty()) {
            view.dialogImage.visibility = View.VISIBLE // hidden by default only not hidden if an image exists, not all reports have images
            view.dialogImage.load(report.imageUrl)
        }

        view.dialogDetails.text =
            getString(R.string.dialog_location_format, report.location) +
                    getString(R.string.dialog_date_format, toDate(report.createdAt)) +
                    getString(R.string.dialog_type_format, report.type) +
                    getString(R.string.dialog_severity_format, report.severity) +
                    getString(R.string.dialog_user_format, report.user) +
                    getString(R.string.dialog_description_format, report.description)

        return activity?.let {
            AlertDialog.Builder(requireContext())
            .setTitle(report.title)
            .setView(view.root)
            .setPositiveButton("OK", null)
            .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    companion object {
        private const val REPORT_DIALOG = "report_dialog"
        fun newInstance(report: Report): ReportDialogFragment {
            return ReportDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(REPORT_DIALOG, report)
                }
            }
        }
    }
}