package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
   entity = IncomeCategory::class,
   parentColumns = arrayOf("id"),
   childColumns = arrayOf("categoryId"),
   onDelete = ForeignKey.CASCADE,
)])
 data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val name: String,
    val categoryId: Int,
    val date: Long,
    val description: String = ""
)
