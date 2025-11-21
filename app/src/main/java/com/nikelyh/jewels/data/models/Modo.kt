package com.nikelyh.jewels.data.models

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

class Modo constructor(
    val id: Number,
    val nombre: String,
    @DrawableRes val iconoResId: Int,
    val colorFondo: Color,
    val ActivityDestino: Class<out Activity>
){

}