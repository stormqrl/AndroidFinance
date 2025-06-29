package com.example.androidfinance.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androidfinance.R
import com.example.androidfinance.data.database.DatabaseProvider
import com.example.androidfinance.data.entity.RecordType
import com.example.androidfinance.data.repository.RecordRepositoryImpl
import com.example.androidfinance.databinding.FragmentRecordFormBinding
import com.example.androidfinance.presentation.viewmodel.RecordFormViewModel
import com.example.androidfinance.presentation.viewmodel.RecordFormViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecordFormFragment : Fragment() {

    private var _binding: FragmentRecordFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecordFormViewModel by viewModels {
        val database = DatabaseProvider.getDatabase(requireContext())
        val repository = RecordRepositoryImpl(database.recordDao())
        RecordFormViewModelFactory(repository)
    }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupDatePicker()
        setupObservers()
        setupClickListeners()

        // Load record if editing
        val recordId = arguments?.getLong("recordId", -1L) ?: -1L
        if (recordId != -1L) {
            viewModel.loadRecord(recordId)
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Set initial date
        binding.etDate.setText(dateFormatter.format(Date()))
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.description.collect { description ->
                if (binding.etDescription.text.toString() != description) {
                    binding.etDescription.setText(description)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.amount.collect { amount ->
                if (binding.etAmount.text.toString() != amount) {
                    binding.etAmount.setText(amount)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.selectedDate.collect { date ->
                binding.etDate.setText(dateFormatter.format(date))
            }
        }

        lifecycleScope.launch {
            viewModel.selectedCategory.collect { category ->
                if (binding.actCategory.text.toString() != category) {
                    binding.actCategory.setText(category, false)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.selectedType.collect { type ->
                when (type) {
                    RecordType.INCOME -> binding.rbIncome.isChecked = true
                    RecordType.EXPENSE -> binding.rbExpense.isChecked = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                binding.switchFavorite.isChecked = isFavorite
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.btnSave.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.validationErrors.collect { errors ->
                if (errors.isNotEmpty()) {
                    binding.tvValidationErrors.text = errors.joinToString("\n")
                    binding.tvValidationErrors.visibility = View.VISIBLE
                } else {
                    binding.tvValidationErrors.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveResult.collect { result ->
                when (result) {
                    is RecordFormViewModel.SaveResult.Success -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    is RecordFormViewModel.SaveResult.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.etDescription.setOnFocusChangeListener { _, _ ->
            viewModel.setDescription(binding.etDescription.text.toString())
        }

        binding.etAmount.setOnFocusChangeListener { _, _ ->
            viewModel.setAmount(binding.etAmount.text.toString())
        }

        binding.actCategory.setOnItemClickListener { _, _, _, _ ->
            viewModel.setCategory(binding.actCategory.text.toString())
        }

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.rb_income -> RecordType.INCOME
                else -> RecordType.EXPENSE
            }
            viewModel.setType(type)
        }

        binding.switchFavorite.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFavorite(isChecked)
        }

        binding.btnSave.setOnClickListener {
            // Update fields before saving
            viewModel.setDescription(binding.etDescription.text.toString())
            viewModel.setAmount(binding.etAmount.text.toString())
            viewModel.setCategory(binding.actCategory.text.toString())

            viewModel.saveRecord()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.selectedDate.value
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val newDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }.time
                viewModel.setDate(newDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
