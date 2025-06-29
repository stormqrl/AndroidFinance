package com.example.androidfinance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "financial_records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val amount: Double,
    val date: Date,
    val category: String,
    val type: RecordType,
    val isFavorite: Boolean = false
)
