package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = ExpenseCategory::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("categoryId"),
    onDelete = ForeignKey.CASCADE,
)])
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name:String,
    val amount: Double,
    val categoryId: Int,
    val date: Long,
    val description: String = ""
)
