package dk.itu.moapd.x9.alyp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.alyp.databinding.ListItemReportBinding

class ReportHolder(
    val binding: ListItemReportBinding
) : RecyclerView.ViewHolder(binding.root) {

}

class ReportListAdapter(private var reports: List<Report>) : RecyclerView.Adapter<ReportHolder>() {

    fun update(newReports: List<Report>) {
        reports = newReports
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemReportBinding.inflate(inflater, parent, false)
        return ReportHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportHolder, position: Int) {
        val crime = reports[position]
        holder.apply {
            binding.reportTitle.text = crime.title
            binding.reportType.text = crime.type
            binding.reportSeverity.text = crime.severity
            binding.reportDate.text = crime.date.toString()
        }
    }

    override fun getItemCount() = reports.size
}