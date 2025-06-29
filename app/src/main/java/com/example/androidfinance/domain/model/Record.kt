package com.example.androidfinance.domain.model

import com.example.androidfinance.data.entity.RecordType
import java.util.Date

data class Record(
    val id: Long = 0,
    val description: String,
    val amount: Double,
    val date: Date,
    val category: String,
    val type: RecordType,
    val isFavorite: Boolean = false
)
