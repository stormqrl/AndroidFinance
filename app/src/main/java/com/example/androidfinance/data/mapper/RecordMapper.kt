package com.example.androidfinance.data.mapper

import com.example.androidfinance.data.entity.RecordEntity
import com.example.androidfinance.domain.model.Record

fun RecordEntity.toDomainModel(): Record {
    return Record(
        id = id,
        description = description,
        amount = amount,
        date = date,
        category = category,
        type = type,
        isFavorite = isFavorite
    )
}

fun Record.toEntity(): RecordEntity {
    return RecordEntity(
        id = id,
        description = description,
        amount = amount,
        date = date,
        category = category,
        type = type,
        isFavorite = isFavorite
    )
}

fun List<RecordEntity>.toDomainModels(): List<Record> {
    return map { it.toDomainModel() }
}
