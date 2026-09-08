package com.dicoding.asclepius.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
