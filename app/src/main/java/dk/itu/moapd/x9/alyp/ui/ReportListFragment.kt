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
 * ReportListFragment displays a scrollable list of incident reports using a [RecyclerView].
 *
 * Observes reports for realtime updates and passes them to ReportListAdapter.
 * Clicking a report opens a ReportDialogFragment with full details.
 *
 * When instantiated with newInstance and ARG_ALLOW_DELETE set to true (used in ProfileActivity), swipe-to-delete is enabled allowing users to remove their own reports.
 */
class ReportListFragment : Fragment() {
    companion object {
        private const val TAG = "ReportListFragment"
        // The key used to pass the allowDelete flag via a Bundle.
        private const val ARG_ALLOW_DELETE = "allow_delete"
        /**
         * Factory method for creating a ReportListFragment.
         * @param allowDelete If true, enables swipe to delete on the list. Defaults to false.
         */
        fun newInstance(allowDelete: Boolean = false) = ReportListFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_ALLOW_DELETE, allowDelete) }
        }
    }
    private var _binding: FragmentReportListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val reportViewModel: ReportViewModel by activityViewModels()
    private lateinit var adapter: ReportListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Sets up the RecyclerView with ReportListAdapter, observes reports for updates, and attaches swipe to delete via ItemTouchHelper if ARG_ALLOW_DELETE is true.
     */
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

        // Receive all updates to the database, flow represents an async stream of data.
        // Flow emits a sequence of values which 'collect' observes
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}