package dk.itu.moapd.x9.alyp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.alyp.databinding.FragmentReportListBinding
import dk.itu.moapd.x9.alyp.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * ReportListFragment is the default fragment shown.
 * Observes changes to the MutableLiveData List of reports for any changes.
 * Calls the update method in the custom adapter with the new reports
 */
class ReportListFragment : Fragment() {

    companion object {
        private const val TAG = "ReportListFragment"
        private const val ARG_ALLOW_DELETE = "allow_delete"
        fun newInstance(allowDelete: Boolean = false) = ReportListFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_ALLOW_DELETE, allowDelete) }
        }
    }
    private var _binding: FragmentReportListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    val reportViewModel: ReportViewModel by activityViewModels()
    private lateinit var adapter: ReportListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total reports: ${reportViewModel.reports.value.size}")
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

        adapter = ReportListAdapter(emptyList()) { report ->
            ReportDialogFragment
                .newInstance(report)
                .show(parentFragmentManager, "REPORT_DIALOG")
        }
        binding.reportListFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.reportListFragment.adapter = adapter

        // recieve all updates to the database, flow representds an async stream of data.
        // flow emits a sequence of values which 'collect' observes
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.reports.collect { report ->
                    adapter.update(report)
                }
            }
        }

        val allowDelete = arguments?.getBoolean(ARG_ALLOW_DELETE) == true
        if (allowDelete) {
            val swipeCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val report = adapter.getReportAt(viewHolder.adapterPosition)
                    reportViewModel.deleteUserReport(report.uid)
                    Toast.makeText(context, "Report successfully deleted", Toast.LENGTH_SHORT).show()
                }
            }
            ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.reportListFragment)
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