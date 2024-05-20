package com.novacodestudios.mint.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.novacodestudios.mint.data.local.entitiy.CategoryWithIncomes
import com.novacodestudios.mint.data.local.entitiy.Income
import com.novacodestudios.mint.data.local.entitiy.IncomeAndCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Upsert
    suspend fun upsert(income: Income)

    @Delete
    suspend fun delete(income: Income)

    @Query("DELETE FROM Income WHERE id=:id")
    suspend fun deleteById(id:Int)

    @Query("SELECT * FROM Income")
    fun getIncomes(): Flow<List<Income>>

    @Transaction
    @Query("SELECT * FROM Income")
    fun getIncomesAndCategories(): Flow<List<IncomeAndCategory>>

    @Transaction
    @Query("SELECT * FROM Income WHERE id=:id")
    fun getIncomeAndCategory(id: Int): Flow<IncomeAndCategory>

    @Query("SELECT * FROM Income WHERE id = :id")
    fun getIncomeById(id: Int): Flow<Income>

    @Query("DELETE FROM Income")
    suspend fun deleteAll()
}