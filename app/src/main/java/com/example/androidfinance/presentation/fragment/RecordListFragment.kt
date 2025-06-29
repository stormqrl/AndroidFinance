package com.example.androidfinance.presentation.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfinance.R
import com.example.androidfinance.data.database.DatabaseProvider
import com.example.androidfinance.data.entity.RecordType
import com.example.androidfinance.data.repository.RecordRepositoryImpl
import com.example.androidfinance.databinding.FragmentRecordListBinding
import com.example.androidfinance.domain.model.Record
import com.example.androidfinance.presentation.adapter.RecordAdapter
import com.example.androidfinance.presentation.viewmodel.RecordListViewModel
import com.example.androidfinance.presentation.viewmodel.RecordListViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class RecordListFragment : Fragment() {

    private var _binding: FragmentRecordListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecordListViewModel by viewModels {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = RecordRepositoryImpl(database.recordDao())
        RecordListViewModelFactory(repository)
    }

    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupFilters()
        setupSpinners()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Recarregar dados quando voltamos para a tela
        // Isso garante que os filtros sejam atualizados após adicionar/editar registros
        viewModel.refreshData()
    }

    private fun setupRecyclerView() {
        recordAdapter = RecordAdapter(
            onEditClick = { record ->
                val bundle = Bundle().apply {
                    putLong("recordId", record.id)
                }
                findNavController().navigate(R.id.action_recordListFragment_to_recordFormFragment, bundle)
            },
            onDeleteClick = { record ->
                showDeleteConfirmationDialog(record)
            },
            onFavoriteClick = { record, isFavorite ->
                viewModel.toggleFavorite(record.id, isFavorite)
            }
        )

        binding.rvRecords.apply {
            adapter = recordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.setSearchQuery(it) }
                return true
            }
        })
    }

    private fun setupFilters() {
        binding.chipFavorites.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleFavorites(isChecked)
        }

        binding.chipIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipExpense.isChecked = false
                viewModel.setSelectedType(RecordType.INCOME)
            } else if (!binding.chipExpense.isChecked) {
                viewModel.setSelectedType(null)
            }
        }

        binding.chipExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.chipIncome.isChecked = false
                viewModel.setSelectedType(RecordType.EXPENSE)
            } else if (!binding.chipIncome.isChecked) {
                viewModel.setSelectedType(null)
            }
        }
        // Setup date filters
        setupDateFilters()
    }

    private fun setupDateFilters() {
        binding.etStartDate.setOnClickListener {
            showDatePicker(true) // true for start date
        }

        binding.etEndDate.setOnClickListener {
            showDatePicker(false) // false for end date
        }
    }

    private fun setupSpinners() {
        // Category spinner - observação dinâmica das categorias
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                updateCategorySpinner(categories)
            }
        }

        // Sort spinner (estático, não precisa de atualização dinâmica)
        setupSortSpinner()
    }

    private fun updateCategorySpinner(categories: List<String>) {
        val categoryList = mutableListOf<String>().apply {
            add("Todas as categorias")
            addAll(categories)
        }

        val currentSelection = binding.spinnerCategory.selectedItemPosition
        val currentSelectedCategory = if (currentSelection > 0 && currentSelection < categoryList.size) {
            categoryList[currentSelection]
        } else null

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryList
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Manter a seleção atual se a categoria ainda existir
        currentSelectedCategory?.let { selectedCategory ->
            val newPosition = categoryList.indexOf(selectedCategory)
            if (newPosition >= 0) {
                binding.spinnerCategory.setSelection(newPosition)
            }
        }

        binding.spinnerCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = if (position == 0) null else categoryList[position]
                viewModel.setSelectedCategory(selectedCategory)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = listOf(
            getString(R.string.sort_date_desc),
            getString(R.string.sort_amount_asc),
            getString(R.string.sort_amount_desc)
        )

        val sortAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortOptions
        )
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSort.adapter = sortAdapter

        binding.spinnerSort.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sortOrder = when (position) {
                    1 -> RecordListViewModel.SortOrder.AMOUNT_ASC
                    2 -> RecordListViewModel.SortOrder.AMOUNT_DESC
                    else -> RecordListViewModel.SortOrder.DATE_DESC
                }
                viewModel.setSortOrder(sortOrder)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.records.collect { records ->
                recordAdapter.submitList(records)
                binding.tvNoRecords.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE

                // Atualizar resumo financeiro
                updateFinancialSummary(records)
            }
        }

        // Observar mudanças nas datas de filtro
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.startDate.collect { date ->
                binding.etStartDate.setText(
                    date?.let { java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: ""
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.endDate.collect { date ->
                binding.etEndDate.setText(
                    date?.let { java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: ""
                )
            }
        }
    }

    private fun updateFinancialSummary(records: List<Record>) {
        val totalIncome = records.filter { it.type == RecordType.INCOME }.sumOf { it.amount }
        val totalExpense = records.filter { it.type == RecordType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        binding.tvTotalIncome.text = currencyFormat.format(totalIncome)
        binding.tvTotalExpense.text = currencyFormat.format(totalExpense)
        binding.tvBalance.text = currencyFormat.format(balance)

        val balanceColor = when {
            balance > 0 -> android.graphics.Color.parseColor("#4CAF50") // Verde
            balance < 0 -> android.graphics.Color.parseColor("#F44336") // Vermelho
            else -> androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.darker_gray) // Cinza
        }
        binding.tvBalance.setTextColor(balanceColor)
    }

    private fun setupClickListeners() {
        binding.fabAddRecord.setOnClickListener {
            findNavController().navigate(R.id.action_recordListFragment_to_recordFormFragment)
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            clearFilterUI()
        }
    }

    private fun clearFilterUI() {
        binding.searchView.setQuery("", false)
        binding.chipFavorites.isChecked = false
        binding.chipIncome.isChecked = false
        binding.chipExpense.isChecked = false
        binding.spinnerCategory.setSelection(0)
        binding.spinnerSort.setSelection(0)
        binding.etStartDate.setText("")
        binding.etEndDate.setText("")
    }

    private fun showDeleteConfirmationDialog(record: Record) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja excluir este registro?")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteRecord(record.id)
                Toast.makeText(requireContext(), getString(R.string.record_deleted), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, if (isStartDate) 0 else 23, if (isStartDate) 0 else 59, if (isStartDate) 0 else 59)
                }.time

                if (isStartDate) {
                    viewModel.setStartDate(selectedDate)
                } else {
                    viewModel.setEndDate(selectedDate)
                }
            },
            year,
            month,
            day
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
