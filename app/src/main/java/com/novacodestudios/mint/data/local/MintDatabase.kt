package com.novacodestudios.mint.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.novacodestudios.mint.data.local.dao.ExpenseCategoryDao
import com.novacodestudios.mint.data.local.dao.ExpenseDao
import com.novacodestudios.mint.data.local.dao.IncomeCategoryDao
import com.novacodestudios.mint.data.local.dao.IncomeDao
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.local.entitiy.ExpenseCategory
import com.novacodestudios.mint.data.local.entitiy.Income
import com.novacodestudios.mint.data.local.entitiy.IncomeCategory

@Database(
    entities = [Expense::class, Income::class, IncomeCategory::class, ExpenseCategory::class],
    version = 2,
    exportSchema = false
)
abstract class MintDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun incomeCategoryDao(): IncomeCategoryDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao

}