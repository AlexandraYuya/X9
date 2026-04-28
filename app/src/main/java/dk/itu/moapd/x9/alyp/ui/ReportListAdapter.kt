package dk.itu.moapd.x9.alyp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.alyp.databinding.ListItemReportBinding
import dk.itu.moapd.x9.alyp.model.Report
import java.text.DateFormat

/**
 * ReportHolder represents one visible row, and listens for click event on the report
 */
class ReportHolder(
    private val binding: ListItemReportBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(report: Report, onItemClick: (Report) -> Unit) {
        binding.reportTitle.text = report.title
        binding.reportDate.text = toDate(report.createdAt)
        binding.reportAuthor.text = report.user

        binding.confirmedBadge.visibility =
            if(report.isConfirmed) {
                View.VISIBLE
            }else {
                View.GONE
            }

        binding.root.setOnClickListener {
            onItemClick(report)
        }
    }
    fun toDate(date: Long) : String {
        return DateFormat.getInstance().format(date)
    }
}

/**
 * ReportListAdapter represents the full list of Report objects shown by the RecyclerView,
 *  inflates one row layout and returns a ReportHolder.
 *  Registers what report at what position was clicked.
 *  Registers how many reports there are.
 *  Updates report list when new reports are added.
 */
class ReportListAdapter(private var reports: List<Report>, private val onItemClick: (Report) -> Unit) : RecyclerView.Adapter<ReportHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemReportBinding.inflate(inflater, parent, false)
        return ReportHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportHolder, position: Int) {
        val report = reports[position]
        holder.bind(report, onItemClick)
    }

    override fun getItemCount() = reports.size

    fun getReportAt(position: Int): Report = reports[position]

    fun update(newReport: List<Report>) {
        reports = newReport
        notifyDataSetChanged()
    }
}