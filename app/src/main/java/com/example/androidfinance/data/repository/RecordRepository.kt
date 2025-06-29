package com.example.androidfinance.data.repository

import com.example.androidfinance.domain.model.Record
import com.example.androidfinance.data.entity.RecordType
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RecordRepository {
    fun getAllRecords(): Flow<List<Record>>
    suspend fun getRecordById(id: Long): Record?
    fun searchRecords(search: String): Flow<List<Record>>
    fun getRecordsByCategory(category: String): Flow<List<Record>>
    fun getRecordsByType(type: RecordType): Flow<List<Record>>
    fun getFavoriteRecords(): Flow<List<Record>>
    fun getRecordsByAmountRange(minAmount: Double, maxAmount: Double): Flow<List<Record>>
    fun getRecordsByDateRange(startDate: Date, endDate: Date): Flow<List<Record>>
    fun getRecordsOrderedByAmountAsc(): Flow<List<Record>>
    fun getRecordsOrderedByAmountDesc(): Flow<List<Record>>
    suspend fun getAllCategories(): List<String>
    suspend fun insertRecord(record: Record): Long
    suspend fun updateRecord(record: Record)
    suspend fun deleteRecord(record: Record)
    suspend fun deleteRecordById(id: Long)
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}
