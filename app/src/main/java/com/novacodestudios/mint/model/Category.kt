package com.novacodestudios.mint.model

import androidx.compose.ui.graphics.Color

interface Category {
    val id:Int
    val name:String
    val color:Int
    companion object{
        val colors = listOf(
            0xFFFF5733.toInt(), // Canlı Turuncu
            0xFFFFC300.toInt(), // Parlak Sarı
            0xFFDAF7A6.toInt(), // Açık Yeşil
            0xFFC70039.toInt(), // Parlak Kırmızı
            0xFF900C3F.toInt(), // Derin Kırmızı
            0xFF9B30FF.toInt(), // Parlak Mor,
            0xFF1F618D.toInt(), // Canlı Mavi
            0xFF28B463.toInt(), // Parlak Yeşil
            0xFFF39C12.toInt(), // Canlı Altın Sarısı
            0xFF8E44AD.toInt()  // Canlı Mor
        )
    }
}