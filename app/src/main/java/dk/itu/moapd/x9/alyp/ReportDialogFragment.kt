package dk.itu.moapd.x9.alyp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.text.DateFormat

/**
 * ReportDialogFragment represents an alert dialog that pops up when a report is clicked.
 * Builder determines the content of the dialog.
 */
class ReportDialogFragment : DialogFragment() {
    fun toDate(date: Long) : String {
        return DateFormat.getInstance().format(date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val report: Report = requireArguments().getSerializable(REPORT_DIALOG) as Report

        return activity?.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(report.title)
            builder.setMessage(
                "Location: ${report.location}\n" +
                "Date: ${toDate(report.createdAt)}\n" +
                "Type: ${report.type}\n" +
                "Severity: ${report.severity}\n" +
                "User: ${report.user}\n" +
                "Description: ${report.description}"
            ).setPositiveButton("OK", null)
            builder.create()
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