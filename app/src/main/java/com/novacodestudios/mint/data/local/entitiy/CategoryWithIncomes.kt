package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Embedded
import androidx.room.Relation


data class CategoryWithIncomes(
    @Embedded val category: IncomeCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val incomes: List<Income>
)
