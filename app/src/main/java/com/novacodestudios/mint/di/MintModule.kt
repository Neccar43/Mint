package com.novacodestudios.mint.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.novacodestudios.mint.data.local.MintDatabase
import com.novacodestudios.mint.data.local.dao.ExpenseCategoryDao
import com.novacodestudios.mint.data.local.dao.ExpenseDao
import com.novacodestudios.mint.data.local.dao.IncomeCategoryDao
import com.novacodestudios.mint.data.local.dao.IncomeDao
import com.novacodestudios.mint.data.repository.MintRepository
import com.novacodestudios.mint.util.Const.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MintModule {

    @Singleton
    @Provides
    fun injectDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MintDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun injectRepository(
        expenseDao: ExpenseDao,
        incomeDao: IncomeDao,
        expenseCategoryDao: ExpenseCategoryDao,
        incomeCategoryDao: IncomeCategoryDao
    ) = MintRepository(
        expenseDao,
        incomeDao,
        expenseCategoryDao,
        incomeCategoryDao
    )

    @Singleton
    @Provides
    fun injectExpenseDao(database: MintDatabase) = database.expenseDao()

    @Singleton
    @Provides
    fun injectIncomeDao(database: MintDatabase) = database.incomeDao()

    @Singleton
    @Provides
    fun injectExpenseCategoryDao(database: MintDatabase) = database.expenseCategoryDao()

    @Singleton
    @Provides
    fun injectIncomeCategoryDao(database: MintDatabase) = database.incomeCategoryDao()
}