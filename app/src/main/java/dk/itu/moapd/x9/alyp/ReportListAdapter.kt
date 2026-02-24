package dk.itu.moapd.x9.alyp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.alyp.databinding.ListItemReportBinding

class ReportHolder(
    private val binding: ListItemReportBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(report: Report, onItemClick: (Report) -> Unit) {
        binding.reportTitle.text = report.title
        binding.reportDate.text = report.date.toString()

        binding.root.setOnClickListener {
            onItemClick(report)
        }
    }
}

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

    fun update(newReports: List<Report>) {
        reports = newReports
        notifyDataSetChanged()
    }
}