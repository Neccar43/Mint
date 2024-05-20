package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Embedded
import androidx.room.Relation


data class CategoryWithExpenses(
    @Embedded val category: ExpenseCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val expenses: List<Expense>
)
