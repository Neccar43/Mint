package com.novacodestudios.mint.presentation.report.component

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisConfig
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.novacodestudios.mint.model.GroupedTransactions
import com.novacodestudios.mint.model.Transaction
import com.novacodestudios.mint.ui.theme.MintTheme
import com.novacodestudios.mint.util.groupByWeek
import java.util.Calendar

@Composable
fun TransactionChart(
    modifier: Modifier = Modifier,
    barWidth: Dp = 50.dp,
    gropedTransactions: List<GroupedTransactions>
) {
    val maxRange = gropedTransactions
        .maxBy { it.transactions.sumOf { transaction -> transaction.amount } }
        .transactions
        .sumOf { it.amount }
        .times(-1)
    val barData = mutableListOf<BarData>()
    val yStepSize = 3
    for ((label, transactions) in gropedTransactions) {
        val data = BarData(
            point = Point(
                x = gropedTransactions.indexOf(GroupedTransactions(label, transactions)).toFloat(),
                y = transactions.sumOf { -it.amount }.toFloat()
            ),
            label = label.slice(0..2),
            color = MaterialTheme.colorScheme.primary
        )
        barData.add(data)

    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barData.size - 1)
        .bottomPadding(2.dp)
        .startDrawPadding(30.dp)
        .labelData { index -> barData[index].label }
        .axisOffset(0.dp)
        .axisLineColor(Color.Transparent)
        .axisConfig(AxisConfig(isAxisLineRequired = false))
        .build()
    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(0.dp)
        .axisConfig(AxisConfig(isAxisLineRequired = false))
        .labelData { index -> (index * (maxRange / yStepSize)).toInt().toString() }
        .build()
    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 5.dp,
            barWidth = barWidth,
            selectionHighlightData = null // bar a tıklamayı engelliyor
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 26.dp,
        backgroundColor = Color.Transparent
    )
    BarChart(modifier = modifier, barChartData = barChartData)
}

@Preview(showBackground = true)
@Composable
private fun cahrt() {
    val x = listOf(
        Transaction(
            id = 1,
            name = "Breakfast",
            amount = 5.0,
            isExpense = true,
            category = "Food",
            categoryColor = 0xFF5733,
            date = getDateForDay(Calendar.MONDAY)
        ),
        Transaction(
            id = 2,
            name = "Bus Ticket",
            amount = 2.5,
            isExpense = true,
            category = "Transport",
            categoryColor = 0xC70039,
            date = getDateForDay(Calendar.MONDAY)
        ),
        Transaction(
            id = 3,
            name = "Bus Ticket",
            amount = 5.5,
            isExpense = true,
            category = "Transport",
            categoryColor = 0xC70039,
            date = getDateForDay(Calendar.MONDAY)
        ),
        Transaction(
            id = 3,
            name = "Lunch",
            amount = 12.0,
            isExpense = true,
            category = "Food",
            categoryColor = 0xFFC300,
            date = getDateForDay(Calendar.TUESDAY)
        ),
        Transaction(
            id = 4,
            name = "Coffee",
            amount = 3.5,
            isExpense = true,
            category = "Food",
            categoryColor = 0xDAF7A6,
            date = getDateForDay(Calendar.TUESDAY)
        ),
        Transaction(
            id = 5,
            name = "Groceries",
            amount = 30.0,
            isExpense = true,
            category = "Groceries",
            categoryColor = 0x581845,
            date = getDateForDay(Calendar.WEDNESDAY)
        ),
        Transaction(
            id = 6,
            name = "Gym",
            amount = 20.0,
            isExpense = true,
            category = "Health",
            categoryColor = 0x900C3F,
            date = getDateForDay(Calendar.WEDNESDAY)
        ),
        Transaction(
            id = 7,
            name = "Dinner",
            amount = 25.0,
            isExpense = true,
            category = "Food",
            categoryColor = 0xFF5733,
            date = getDateForDay(Calendar.THURSDAY)
        ),
        Transaction(
            id = 8,
            name = "Taxi",
            amount = 15.0,
            isExpense = true,
            category = "Transport",
            categoryColor = 0xC70039,
            date = getDateForDay(Calendar.THURSDAY)
        ),
        Transaction(
            id = 9,
            name = "Cinema",
            amount = 30.0,
            isExpense = true,
            category = "Entertainment",
            categoryColor = 0xFFC300,
            date = getDateForDay(Calendar.FRIDAY)
        ),
        Transaction(
            id = 10,
            name = "Drinks",
            amount = 20.0,
            isExpense = true,
            category = "Entertainment",
            categoryColor = 0xDAF7A6,
            date = getDateForDay(Calendar.FRIDAY)
        ),
        Transaction(
            id = 11,
            name = "Shopping",
            amount = 50.0,
            isExpense = true,
            category = "Shopping",
            categoryColor = 0x581845,
            date = getDateForDay(Calendar.SATURDAY)
        ),
        Transaction(
            id = 12,
            name = "Dinner",
            amount = 30.0,
            isExpense = true,
            category = "Food",
            categoryColor = 0x900C3F,
            date = getDateForDay(Calendar.SATURDAY)
        ),
        Transaction(
            id = 13,
            name = "Brunch",
            amount = 25.0,
            isExpense = true,
            category = "Food",
            categoryColor = 0xFF5733,
            date = getDateForDay(Calendar.SUNDAY)
        ),
        Transaction(
            id = 14,
            name = "Park",
            amount = 15.0,
            isExpense = true,
            category = "Entertainment",
            categoryColor = 0xC70039,
            date = getDateForDay(Calendar.SUNDAY)
        )

    ).groupByWeek()
    MintTheme {
        TransactionChart(modifier = Modifier, gropedTransactions = x)
    }
}

fun getDateForDay(dayOfWeek: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
    }
    return calendar.timeInMillis
}

