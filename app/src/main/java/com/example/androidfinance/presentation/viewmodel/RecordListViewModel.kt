package com.example.androidfinance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androidfinance.data.entity.RecordType
import com.example.androidfinance.data.repository.RecordRepository
import com.example.androidfinance.domain.model.Record
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class RecordListViewModel(private val repository: RecordRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _selectedCategory = MutableStateFlow<String?>(null)

    private val _selectedType = MutableStateFlow<RecordType?>(null)

    private val _isShowingFavorites = MutableStateFlow(false)

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)

    private val _minAmount = MutableStateFlow<Double?>(null)

    private val _maxAmount = MutableStateFlow<Double?>(null)

    private val _startDate = MutableStateFlow<Date?>(null)
    val startDate: StateFlow<Date?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Date?>(null)
    val endDate: StateFlow<Date?> = _endDate.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records.asStateFlow()

    init {
        loadCategories()
        loadAllRecords()
    }

    private fun loadAllRecords() {
        viewModelScope.launch {
            repository.getAllRecords().collect { recordList ->
                _records.value = recordList
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val allCategories = repository.getAllCategories()
            _categories.value = allCategories
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun setSelectedType(type: RecordType?) {
        _selectedType.value = type
        applyFilters()
    }

    fun toggleFavorites(showFavorites: Boolean) {
        _isShowingFavorites.value = showFavorites
        applyFilters()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        applyFilters()
    }

    fun setStartDate(date: Date?) {
        _startDate.value = date
        applyFilters()
    }

    fun setEndDate(date: Date?) {
        _endDate.value = date
        applyFilters()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedType.value = null
        _isShowingFavorites.value = false
        _sortOrder.value = SortOrder.DATE_DESC
        _minAmount.value = null
        _maxAmount.value = null
        _startDate.value = null
        _endDate.value = null
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            repository.getAllRecords().collect { allRecords ->
                var filteredRecords = allRecords

                // Aplicar filtro de busca
                if (_searchQuery.value.isNotEmpty()) {
                    filteredRecords = filteredRecords.filter { record ->
                        record.description.contains(_searchQuery.value, ignoreCase = true) ||
                        record.category.contains(_searchQuery.value, ignoreCase = true)
                    }
                }

                // Aplicar filtro de categoria
                _selectedCategory.value?.let { category ->
                    filteredRecords = filteredRecords.filter { it.category == category }
                }

                // Aplicar filtro de tipo
                _selectedType.value?.let { type ->
                    filteredRecords = filteredRecords.filter { it.type == type }
                }

                // Aplicar filtro de favoritos
                if (_isShowingFavorites.value) {
                    filteredRecords = filteredRecords.filter { it.isFavorite }
                }

                // Aplicar filtro de data (lógica solicitada)
                val startDate = _startDate.value
                val endDate = _endDate.value

                when {
                    startDate != null && endDate != null -> {
                        // Ambas as datas definidas: filtrar registros entre as datas
                        filteredRecords = filteredRecords.filter { record ->
                            (record.date.after(startDate) || record.date == startDate) &&
                            (record.date.before(endDate) || record.date == endDate)
                        }
                    }
                    startDate != null -> {
                        // Apenas data de início: filtrar registros a partir desta data
                        filteredRecords = filteredRecords.filter { record ->
                            record.date.after(startDate) || record.date == startDate
                        }
                    }
                    endDate != null -> {
                        // Apenas data de fim: filtrar registros até esta data
                        filteredRecords = filteredRecords.filter { record ->
                            record.date.before(endDate) || record.date == endDate
                        }
                    }
                }

                // Aplicar filtro de valor
                val minAmount = _minAmount.value
                val maxAmount = _maxAmount.value
                if (minAmount != null && maxAmount != null) {
                    filteredRecords = filteredRecords.filter { record ->
                        record.amount >= minAmount && record.amount <= maxAmount
                    }
                }

                // Aplicar ordenação
                filteredRecords = when (_sortOrder.value) {
                    SortOrder.DATE_DESC -> filteredRecords.sortedByDescending { it.date }
                    SortOrder.AMOUNT_ASC -> filteredRecords.sortedBy { it.amount }
                    SortOrder.AMOUNT_DESC -> filteredRecords.sortedByDescending { it.amount }
                }

                _records.value = filteredRecords
            }
        }
    }

    fun toggleFavorite(recordId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(recordId, isFavorite)
        }
    }

    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteRecordById(recordId)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loadCategories()
        }
    }

    enum class SortOrder {
        DATE_DESC,
        AMOUNT_ASC,
        AMOUNT_DESC
    }
}

class RecordListViewModelFactory(
    private val repository: RecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
