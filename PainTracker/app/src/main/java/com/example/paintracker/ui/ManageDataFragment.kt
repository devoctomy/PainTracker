package com.example.paintracker.ui

import PainEntryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paintracker.data.PainEntry
import com.example.paintracker.databinding.FragmentManageDataBinding
import com.example.paintracker.interfaces.IConfigService
import com.example.paintracker.interfaces.IDataManagerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageDataFragment : Fragment() {
    @Inject lateinit var configService: IConfigService
    @Inject lateinit var dataManagerService: IDataManagerService

    private var _binding: FragmentManageDataBinding? = null
    private lateinit var painEntries: List<PainEntry>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentManageDataBinding.inflate(inflater, container, false)
        painEntries = dataManagerService.listAllPainEntries(configService.getCurrent().painCategories)

        val adapter = PainEntryAdapter(painEntries) { selectedEntry ->
            // Handle selection, e.g., show a Toast or navigate to a detail page
            Toast.makeText(requireContext(), "Selected: ${selectedEntry.date}", Toast.LENGTH_SHORT).show()
        }

        binding.painEntriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.painEntriesRecyclerView.adapter = adapter
        binding.painEntriesRecyclerView.setHasFixedSize(true) // Improves performance for fixed-size lists


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}