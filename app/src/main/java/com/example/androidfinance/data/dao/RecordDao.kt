package com.example.androidfinance.data.dao

import androidx.room.*
import com.example.androidfinance.data.entity.RecordEntity
import com.example.androidfinance.data.entity.RecordType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RecordDao {

    @Query("SELECT * FROM financial_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE id = :id")
    suspend fun getRecordById(id: Long): RecordEntity?

    @Query("SELECT * FROM financial_records WHERE description LIKE '%' || :search || '%' ORDER BY date DESC")
    fun searchRecords(search: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE category = :category ORDER BY date DESC")
    fun getRecordsByCategory(category: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE type = :type ORDER BY date DESC")
    fun getRecordsByType(type: RecordType): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteRecords(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE amount BETWEEN :minAmount AND :maxAmount ORDER BY date DESC")
    fun getRecordsByAmountRange(minAmount: Double, maxAmount: Double): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getRecordsByDateRange(startDate: Date, endDate: Date): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records ORDER BY amount ASC")
    fun getRecordsOrderedByAmountAsc(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM financial_records ORDER BY amount DESC")
    fun getRecordsOrderedByAmountDesc(): Flow<List<RecordEntity>>

    @Query("SELECT DISTINCT category FROM financial_records ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    @Insert
    suspend fun insertRecord(record: RecordEntity): Long

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Delete
    suspend fun deleteRecord(record: RecordEntity)

    @Query("DELETE FROM financial_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("UPDATE financial_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}
