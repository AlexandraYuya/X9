package dk.itu.moapd.x9.alyp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.x9.alyp.databinding.FragmentReportListBinding
import kotlin.getValue

private const val TAG = "ReportListFragment"

class ReportListFragment : Fragment() {
    private var _binding: FragmentReportListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    val reportViewModel: ReportViewModel by activityViewModels()
    private lateinit var adapter: ReportListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total reports: ${reportViewModel.reports.value?.size}")
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = FragmentReportListBinding.inflate(inflater, container, false).also {
        _binding = it
        Log.d(TAG, "onCreateView() called")
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        // Creates a new instance of the report alert dialog and displays it
        adapter = ReportListAdapter(emptyList()) { report ->
            ReportDialogFragment
                .newInstance(report)
                .show(parentFragmentManager, "REPORT_DIALOG")
        }
        binding.reportListFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.reportListFragment.adapter = adapter

        reportViewModel.reports.observe(viewLifecycleOwner) { reports ->
            adapter.update(reports)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}