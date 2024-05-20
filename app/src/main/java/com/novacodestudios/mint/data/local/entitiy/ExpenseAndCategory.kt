package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseAndCategory(
    @Embedded val expense: Expense,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: ExpenseCategory
)