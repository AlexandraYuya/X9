package dk.itu.moapd.x9.alyp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import coil.load
import dk.itu.moapd.x9.alyp.R
import dk.itu.moapd.x9.alyp.databinding.FragmentReportDialogBinding
import dk.itu.moapd.x9.alyp.model.Report
import java.text.DateFormat

/**
 * ReportDialogFragment displays the full details of a clicked report in an alert dialog that pops up when a report is clicked.
 * Builder determines the content of the dialog.
 * Uses a factory method newInstance to pass the Report object safely via a Bundle, since fragments must have an empty constructor.
 */
class ReportDialogFragment : DialogFragment() {
    companion object {
        /**
         * The key used to store and retrieve the Report object from the fragment's Bundle.
         */
        private const val REPORT_DIALOG = "report_dialog"

        /**
         * Factory method for creating a ReportDialogFragment with a Report object attached.
         * @param report The report to display in the dialog.
         */
        fun newInstance(report: Report): ReportDialogFragment {
            return ReportDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(REPORT_DIALOG, report)
                }
            }
        }
    }

    /**
     * Formats a Unix timestamp in milliseconds to a readable date string.
     * @param date Unix timestamp in milliseconds.
     * @return Formatted date string.
     */
    private fun toDate(date: Long) : String {
        return DateFormat.getInstance().format(date)
    }

    /**
     * Builds and returns the alert dialog with the report's image and details
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val report: Report = requireArguments().getSerializable(REPORT_DIALOG) as Report
        val view = FragmentReportDialogBinding.inflate(layoutInflater) // one off inflation of the view, don't need persistent binding since we don't need to manage fragment's lifecycle.

        // uses Coil image library to load the image from Firebase Storage download URL.
        if (report.imageUrl.isNotEmpty()) {
            view.dialogImage.visibility = View.VISIBLE // hidden by default only not hidden if an image exists, not all reports have images.
            view.dialogImage.load(report.imageUrl)
        }

        view.dialogDetails.text = buildString {
            append(getString(R.string.dialog_location_format, report.location))
            append(getString(R.string.dialog_date_format, toDate(report.createdAt)))
            append(getString(R.string.dialog_type_format, report.type))
            append(getString(R.string.dialog_severity_format, report.severity))
            append(getString(R.string.dialog_user_format, report.user))
            append(getString(R.string.dialog_description_format, report.description))
        }

        return activity?.let {
            AlertDialog.Builder(requireContext())
            .setTitle(report.title)
            .setView(view.root)
            .setPositiveButton(R.string.dialog_OK_btn, null)
            .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}