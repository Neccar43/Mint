package com.novacodestudios.mint.data.local.entitiy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.novacodestudios.mint.model.Category

@Entity
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true)
    override val id:Int=0,
    override val name:String,
    override val color:Int
):Category
