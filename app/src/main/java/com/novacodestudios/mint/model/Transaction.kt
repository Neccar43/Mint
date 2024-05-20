package com.novacodestudios.mint.model

data class Transaction(
    val id: Int,
    val name: String,
    val amount: Double,
    val isExpense: Boolean,
    val category: String,
    val categoryColor: Int,
    val date: Long,
)