package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Embedded
import androidx.room.Relation

data class IncomeAndCategory(
    @Embedded val income: Income,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: IncomeCategory
)
