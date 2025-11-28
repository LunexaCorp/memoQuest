package com.nikelyh.jewels.data.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

data class Tarjeta constructor(
    val id: Number,
    val id_pareja: Number,
    @DrawableRes val picture: Int,
    var estado: Number=VOLTEADO,
    val color: Color,
    var acertado : Boolean = false,
    var precio: Int = 0,
    var descripcion: String = "",
    var comprado: Boolean = true,
){
    companion object{
        val VOLTEADO: Number = 0
        val DE_FRENTE: Number = 1
    }

    init {

    }

}

/*
    DOCUMENTACIÓN
    Constructores: https://kotlinlang-org.translate.goog/?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc&_x_tr_hist=true#secondary-constructors
    companion object: https://kotlinlang.org/docs/object-declarations.html#data-objects
 */