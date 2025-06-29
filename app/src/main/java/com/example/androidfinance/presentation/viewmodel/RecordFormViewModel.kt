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

class RecordFormViewModel(private val repository: RecordRepository) : ViewModel() {

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow(RecordType.EXPENSE)
    val selectedType: StateFlow<RecordType> = _selectedType.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult: SharedFlow<SaveResult> = _saveResult.asSharedFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())

    private var editingRecordId: Long? = null

    init {
        loadCategories()
    }

    fun setDescription(description: String) {
        _description.value = description
        clearValidationErrors()
    }

    fun setAmount(amount: String) {
        _amount.value = amount
        clearValidationErrors()
    }

    fun setDate(date: Date) {
        _selectedDate.value = date
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        clearValidationErrors()
    }

    fun setType(type: RecordType) {
        _selectedType.value = type
    }

    fun setFavorite(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val record = repository.getRecordById(recordId)
                if (record != null) {
                    editingRecordId = record.id
                    _description.value = record.description
                    _amount.value = record.amount.toString()
                    _selectedDate.value = record.date
                    _selectedCategory.value = record.category
                    _selectedType.value = record.type
                    _isFavorite.value = record.isFavorite
                }
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Error("Erro ao carregar registro"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveRecord() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val amountValue = _amount.value.toDouble()
                val record = Record(
                    id = editingRecordId ?: 0,
                    description = _description.value,
                    amount = amountValue,
                    date = _selectedDate.value,
                    category = _selectedCategory.value,
                    type = _selectedType.value,
                    isFavorite = _isFavorite.value
                )

                if (editingRecordId == null) {
                    repository.insertRecord(record)
                    _saveResult.emit(SaveResult.Success("Registro criado com sucesso"))
                } else {
                    repository.updateRecord(record)
                    _saveResult.emit(SaveResult.Success("Registro atualizado com sucesso"))
                }

                clearForm()
            } catch (e: NumberFormatException) {
                _saveResult.emit(SaveResult.Error("Valor inválido"))
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Error("Erro ao salvar registro"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateFields(): Boolean {
        val errors = mutableListOf<String>()

        if (_description.value.isBlank()) {
            errors.add("Descrição é obrigatória")
        }

        if (_amount.value.isBlank()) {
            errors.add("Valor é obrigatório")
        } else {
            try {
                val amount = _amount.value.toDouble()
                if (amount <= 0) {
                    errors.add("Valor deve ser maior que zero")
                }
            } catch (e: NumberFormatException) {
                errors.add("Valor deve ser um número válido")
            }
        }

        if (_selectedCategory.value.isBlank()) {
            errors.add("Categoria é obrigatória")
        }

        _validationErrors.value = errors
        return errors.isEmpty()
    }

    private fun clearValidationErrors() {
        if (_validationErrors.value.isNotEmpty()) {
            _validationErrors.value = emptyList()
        }
    }

    private fun clearForm() {
        editingRecordId = null
        _description.value = ""
        _amount.value = ""
        _selectedDate.value = Date()
        _selectedCategory.value = ""
        _selectedType.value = RecordType.EXPENSE
        _isFavorite.value = false
        _validationErrors.value = emptyList()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    sealed class SaveResult {
        data class Success(val message: String) : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}

class RecordFormViewModelFactory(
    private val repository: RecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordFormViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
