package com.novacodestudios.mint.util

import android.text.format.DateFormat
import com.novacodestudios.mint.model.GroupedTransactions
import com.novacodestudios.mint.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun <T> executeWithResource(block: suspend () -> T): Flow<Resource<T>> {
    return flow {
        emit(Resource.loading())
        try {
            emit(Resource.success(block()))
        } catch (e: Exception) {
            emit(Resource.error(e))
        }
    }
}
fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return format.format(date)
}

 private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
 fun List<Transaction>.groupByWeek(isEnable:Boolean=false): List<GroupedTransactions> {
    val groupedTransactions = mutableListOf<GroupedTransactions>()

    val oneWeekAgo = Calendar.getInstance()
    oneWeekAgo.add(Calendar.DAY_OF_MONTH, -7)

    this.filter { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis > oneWeekAgo.timeInMillis
    }.sortedByDescending { it.date }.groupBy { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }.forEach { (key, value) ->
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = key }
        val header:String=if (isEnable){
            when {
                dateFormat.format(today.time) == dateFormat.format(date.time) -> "Today"
                dateFormat.format(today.apply {
                    add(
                        Calendar.DAY_OF_MONTH,
                        -1
                    )
                }.time) == dateFormat.format(date.time) -> "Yesterday"

                else -> DateFormat.format("EEEE", date.time).toString()
            }
        }else{
            DateFormat.format("EEEE", date.time).toString()
        }
        groupedTransactions.add(GroupedTransactions(header, value))
    }

    return groupedTransactions
}

// TODO: bu fonksiyonun header kısmını yeniden yaz
 fun List<Transaction>.groupByMonth(): List<GroupedTransactions> {
    val groupedTransactions = mutableListOf<GroupedTransactions>()

    val today = Calendar.getInstance()

    this.filter { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == today.get(
            Calendar.YEAR
        )
    }.sortedByDescending { it.date }.groupBy { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }.forEach { (key, value) ->
        val date = Calendar.getInstance().apply { timeInMillis = key }
        val headerText = DateFormat.format("MMMM", date.time).toString()
        groupedTransactions.add(GroupedTransactions(headerText, value))
    }

    return groupedTransactions
}

 fun List<Transaction>.groupByToday(): List<GroupedTransactions> {
    val groupedTransactions = mutableListOf<GroupedTransactions>()

    val today = Calendar.getInstance()

    this.filter { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        dateFormat.format(cal.time) == dateFormat.format(today.time)
    }.sortedByDescending { it.date }.let { todayTransactions ->
        if (todayTransactions.isNotEmpty()) {
            groupedTransactions.add(
                GroupedTransactions(
                    "Today",
                    todayTransactions
                )
            )
        }
    }

    return groupedTransactions
}

 fun List<Transaction>.groupByYearMonth(): List<GroupedTransactions> {
    val groupedTransactions = mutableListOf<GroupedTransactions>()

    this.filter { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
    }.sortedByDescending { it.date }.groupBy { transaction ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = transaction.date
        cal.get(Calendar.MONTH)
    }.forEach { (key, value) ->
        val date = Calendar.getInstance().apply { set(Calendar.MONTH, key) }
        val headerText = DateFormat.format("MMMM", date.time).toString()
        groupedTransactions.add(GroupedTransactions(headerText, value))
    }

    return groupedTransactions
}
