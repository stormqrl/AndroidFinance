package com.example.androidfinance.data.repository

import com.example.androidfinance.data.dao.RecordDao
import com.example.androidfinance.data.mapper.toDomainModel
import com.example.androidfinance.data.mapper.toDomainModels
import com.example.androidfinance.data.mapper.toEntity
import com.example.androidfinance.domain.model.Record
import com.example.androidfinance.data.entity.RecordType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class RecordRepositoryImpl(private val recordDao: RecordDao) : RecordRepository {

    override fun getAllRecords(): Flow<List<Record>> {
        return recordDao.getAllRecords().map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getRecordById(id: Long): Record? {
        return recordDao.getRecordById(id)?.toDomainModel()
    }

    override fun searchRecords(search: String): Flow<List<Record>> {
        return recordDao.searchRecords(search).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsByCategory(category: String): Flow<List<Record>> {
        return recordDao.getRecordsByCategory(category).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsByType(type: RecordType): Flow<List<Record>> {
        return recordDao.getRecordsByType(type).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getFavoriteRecords(): Flow<List<Record>> {
        return recordDao.getFavoriteRecords().map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsByAmountRange(minAmount: Double, maxAmount: Double): Flow<List<Record>> {
        return recordDao.getRecordsByAmountRange(minAmount, maxAmount).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsByDateRange(startDate: Date, endDate: Date): Flow<List<Record>> {
        return recordDao.getRecordsByDateRange(startDate, endDate).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsOrderedByAmountAsc(): Flow<List<Record>> {
        return recordDao.getRecordsOrderedByAmountAsc().map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getRecordsOrderedByAmountDesc(): Flow<List<Record>> {
        return recordDao.getRecordsOrderedByAmountDesc().map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getAllCategories(): List<String> {
        return recordDao.getAllCategories()
    }

    override suspend fun insertRecord(record: Record): Long {
        return recordDao.insertRecord(record.toEntity())
    }

    override suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(record.toEntity())
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.deleteRecord(record.toEntity())
    }

    override suspend fun deleteRecordById(id: Long) {
        recordDao.deleteRecordById(id)
    }

    override suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) {
        recordDao.updateFavoriteStatus(id, isFavorite)
    }
}
