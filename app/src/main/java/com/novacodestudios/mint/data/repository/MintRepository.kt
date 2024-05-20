package com.novacodestudios.mint.data.repository

import android.util.Log
import com.novacodestudios.mint.data.local.dao.ExpenseCategoryDao
import com.novacodestudios.mint.data.local.dao.ExpenseDao
import com.novacodestudios.mint.data.local.dao.IncomeCategoryDao
import com.novacodestudios.mint.data.local.dao.IncomeDao
import com.novacodestudios.mint.data.local.entitiy.Expense
import com.novacodestudios.mint.data.local.entitiy.ExpenseAndCategory
import com.novacodestudios.mint.data.local.entitiy.ExpenseCategory
import com.novacodestudios.mint.data.local.entitiy.Income
import com.novacodestudios.mint.data.local.entitiy.IncomeAndCategory
import com.novacodestudios.mint.data.local.entitiy.IncomeCategory
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.util.Resource
import com.novacodestudios.mint.util.executeWithResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class MintRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val incomeCategoryDao: IncomeCategoryDao
) {
    companion object{
        private const val TAG = "MintRepository"
    }


    suspend fun deleteExpense(expense: Expense): Flow<Resource<Unit>> {
        return executeWithResource {
            expenseDao.delete(expense)
        }
    }

    suspend fun deleteIncome(income: Income): Flow<Resource<Unit>> {
        return executeWithResource {
            incomeDao.delete(income)
        }
    }

    suspend fun deleteExpenseCategory(category: ExpenseCategory): Flow<Resource<Unit>> {
        return executeWithResource {
            expenseCategoryDao.delete(category)
        }
    }

    suspend fun deleteIncomeCategory(category: IncomeCategory): Flow<Resource<Unit>> {
        return executeWithResource {
            incomeCategoryDao.delete(category)
        }
    }

    suspend fun upsertExpense(expense: Expense): Flow<Resource<Unit>> {
        return executeWithResource {
            expenseDao.upsert(expense)
        }
    }

    suspend fun upsertIncome(income: Income): Flow<Resource<Unit>> {
        return executeWithResource {
            incomeDao.upsert(income)
        }
    }

    suspend fun upsertExpenseCategory(category: ExpenseCategory): Flow<Resource<Unit>> {
        return executeWithResource {
            expenseCategoryDao.upsert(category)
        }
    }

    suspend fun upsertIncomeCategory(category: IncomeCategory): Flow<Resource<Unit>> {
        return executeWithResource {
            incomeCategoryDao.upsert(category)
        }
    }

    fun getExpenseAndCategoryById(id:Int):Flow<Resource<ExpenseAndCategory>> = channelFlow {
        send(Resource.loading())
        expenseDao.getExpenseAndCategoryById(id).catch {
            send(Resource.error(Exception(it)))
        }.collectLatest {
            send(Resource.success(it))
        }
    }
    fun getIncomeAndCategoryById(id:Int):Flow<Resource<IncomeAndCategory>> = channelFlow {
        send(Resource.loading())
        incomeDao.getIncomeAndCategory(id).catch {
            send(Resource.error(Exception(it)))
        }.collectLatest {
            send(Resource.success(it))
        }
    }


    fun getCombinedTransaction(): Flow<Resource<List<Transaction>>> {
        return channelFlow {
            send(Resource.loading())

            val expensesFlow = expenseDao.getExpensesAndCategories()
            val incomesFlow = incomeDao.getIncomesAndCategories()

            expensesFlow.combine(incomesFlow) { expenseAndCategories, incomeAndCategories ->
                send(Resource.loading())
                val transactions = mergeTransactions(expenseAndCategories, incomeAndCategories)
                transactions
            }.catch {
                send(Resource.error(Exception(it)))
            }.collectLatest {
                Log.d(TAG, "getCombinedTransaction: $it")
                send(Resource.success(it))
            }
        }

    }

    private fun mergeTransactions(
        expenses: List<ExpenseAndCategory>,
        incomes: List<IncomeAndCategory>
    ): List<Transaction> {
        val mergedList = mutableListOf<Transaction>()
        for (expense in expenses) {
            val transaction = Transaction(
                id = expense.expense.id,
                name = expense.expense.name,
                amount = expense.expense.amount,
                isExpense = true,
                category = expense.category.name,
                categoryColor = expense.category.color,
                date = expense.expense.date
            )
            mergedList.add(transaction)
        }
        for (income in incomes) {
            val transaction = Transaction(
                id = income.income.id,
                name = income.income.name,
                amount = income.income.amount,
                isExpense = false,
                category = income.category.name,
                categoryColor = income.category.color,
                date = income.income.date
            )
            mergedList.add(transaction)
        }
        return mergedList
    }

    fun getExpenseCategories():Flow<Resource<List<ExpenseCategory>>> = flow {
        emit(Resource.loading())
        expenseCategoryDao.getAll().catch {
            emit(Resource.error(Exception(it)))
        }.collect{
            emit(Resource.success(it))
        }
    }



    fun getIncomeCategories():Flow<Resource<List<IncomeCategory>>> = flow {
        emit(Resource.loading())
        incomeCategoryDao.getAll().catch {
            emit(Resource.error(Exception(it)))
        }.collect{
            emit(Resource.success(it))
        }
    }

    fun deleteAllData()= executeWithResource {
        expenseDao.deleteAll()
        incomeDao.deleteAll()
        expenseCategoryDao.deleteAll()
        incomeCategoryDao.deleteAll()
    }

    fun deleteExpenseById(id:Int):Flow<Resource<Unit>> = executeWithResource {
        expenseDao.deleteById(id)
    }

    fun deleteIncomeById(id:Int):Flow<Resource<Unit>> = executeWithResource {
        incomeDao.deleteById(id)
    }
}