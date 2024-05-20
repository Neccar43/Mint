package com.novacodestudios.mint.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.novacodestudios.mint.data.local.entitiy.IncomeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeCategoryDao {
     @Query("SELECT * FROM IncomeCategory")
     fun getAll(): Flow<List<IncomeCategory>>

     @Query("SELECT * FROM IncomeCategory WHERE id = :id")
     fun getById(id: Int): Flow<IncomeCategory>

     @Upsert
     suspend fun upsert(incomeCategory: IncomeCategory)

     @Delete
     suspend fun delete(incomeCategory: IncomeCategory)

     @Query("DELETE FROM IncomeCategory")
     suspend fun deleteAll()

}