package dk.itu.moapd.x9.alyp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment

class ReportDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val report: Report = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(ARG_REPORT, Report::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getSerializable(ARG_REPORT) as Report
        }

        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(report.title)
            builder.setMessage(
                "Location: ${report.location}\n" +
                "Date: ${report.date}\n" +
                "Type: ${report.type}\n" +
                "Severity: ${report.severity}\n" +
                report.description
            )
                .setPositiveButton("OK", null)
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    companion object {
        private const val ARG_REPORT = "arg_report"
        fun newInstance(report: Report): ReportDialogFragment {
            return ReportDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_REPORT, report)
                }
            }
        }
    }
}