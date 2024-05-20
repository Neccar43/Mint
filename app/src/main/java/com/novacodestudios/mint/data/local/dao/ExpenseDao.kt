package com.novacodestudios.mint.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.local.entitiy.ExpenseAndCategory
import kotlinx.coroutines.flow.Flow
@Dao
interface ExpenseDao {
    @Upsert
    suspend fun upsert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM Expense WHERE id=:id")
    suspend fun deleteById(id:Int)

    @Query("SELECT * FROM Expense")
    fun getExpenses(): Flow<List<Expense>>

    @Transaction
    @Query("SELECT * FROM Expense")
    fun getExpensesAndCategories(): Flow<List<ExpenseAndCategory>>

    @Transaction
    @Query("SELECT * FROM Expense WHERE id=:id")
    fun getExpenseAndCategoryById(id: Int): Flow<ExpenseAndCategory>

    @Query("SELECT * FROM Expense WHERE id = :id")
    fun getExpenseById(id: Int): Flow<Expense>

    @Query("DELETE FROM Expense")
    suspend fun deleteAll()

}