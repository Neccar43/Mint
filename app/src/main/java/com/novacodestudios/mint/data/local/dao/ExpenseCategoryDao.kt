package com.novacodestudios.mint.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.novacodestudios.mint.data.local.entitiy.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {
    @Query("SELECT * FROM ExpenseCategory")
    fun getAll(): Flow<List<ExpenseCategory>>

    @Query("SELECT * FROM ExpenseCategory WHERE id = :id")
    fun getById(id: Int): Flow<ExpenseCategory>

    @Upsert
    suspend fun upsert(expenseCategory: ExpenseCategory)

    @Delete
    suspend fun delete(expenseCategory: ExpenseCategory)

    @Query("DELETE FROM ExpenseCategory")
    suspend fun deleteAll()
}