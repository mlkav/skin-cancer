package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: PredictionEntity)

    @Query("SELECT * FROM prediction_history ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<PredictionEntity>>

    @Query("DELETE FROM prediction_history WHERE id = :id")
    suspend fun delete(id: Int)
}
