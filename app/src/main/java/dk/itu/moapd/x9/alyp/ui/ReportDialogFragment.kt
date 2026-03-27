package dk.itu.moapd.x9.alyp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.model.Report
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
                        getString(R.string.dialog_location_format, report.location) +
                        getString(R.string.dialog_date_format, toDate(report.createdAt)) +
                        getString(R.string.dialog_type_format, report.type) +
                        getString(R.string.dialog_severity_format, report.severity) +
                        getString(R.string.dialog_user_format, report.user) +
                        getString(R.string.dialog_description_format, report.description)
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