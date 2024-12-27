package com.example.paintracker.ui

import PainEntryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paintracker.data.PainContext
import com.example.paintracker.data.PainEntry
import com.example.paintracker.databinding.FragmentManageDataBinding
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPainContext
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageDataFragment : Fragment() {
    // Dependency Injection
    @Inject lateinit var configService: IConfigService
    @Inject lateinit var dataManagerService: IDataManagerService
    @Inject lateinit var painContext: IPainContext

    private var _binding: FragmentManageDataBinding? = null
    private var painContextChangeListener: ((String, Any?, Any?) -> Unit)? = null
    private lateinit var painEntries: List<PainEntry>
    private lateinit var painEntryAdapter: PainEntryAdapter

    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding is only valid between onCreateView and onDestroyView.")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentManageDataBinding.inflate(inflater, container, false)
        painEntries = dataManagerService.listAllPainEntries(configService.getCurrent().painCategories)

        painEntryAdapter = PainEntryAdapter(painEntries) { selectedEntry ->
            if(painContext.selectedDate != selectedEntry.date)
            {
                painContext.selectedDate = selectedEntry.date
            }
            // Handle selection, e.g., show a Toast or navigate to a detail page
            //Toast.makeText(requireContext(), "Selected: ${selectedEntry.date}", Toast.LENGTH_SHORT).show()
        }

        binding.painEntriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.painEntriesRecyclerView.adapter = painEntryAdapter
        binding.painEntriesRecyclerView.setHasFixedSize(true) // Improves performance for fixed-size lists

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) {
            _binding = FragmentManageDataBinding.bind(view) // Re-link the binding
        }

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener = { propertyName, oldValue, newValue ->
            if (propertyName == "selectedDate") {
                updateSelectedDate()
            }
        }
        painContext.addChangeListener(painContextChangeListener!!)
    }


    override fun onResume() {
        super.onResume()

        updateSelectedDate()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener?.let { listener ->
            painContext.removeChangeListener(listener)
        }

        _binding = null
        painContextChangeListener = null
    }

    private fun updateSelectedDate() {
        val targetIndex = painEntries.indexOfFirst { it.date == painContext.selectedDate }

        if (targetIndex != -1) {
            binding.painEntriesRecyclerView.post {
                binding.painEntriesRecyclerView.scrollToPosition(targetIndex)
                painEntryAdapter.selectItem(targetIndex)
            }
        } else {
            //Toast.makeText(requireContext(), "No entry found for the given date.", Toast.LENGTH_SHORT).show()
        }
    }
}