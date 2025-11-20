package com.nikelyh.jewels.data.models

import androidx.compose.ui.unit.Dp

class Tarjeta constructor(
    val id: Number,
    val id_pareja: Number,
    var forma: String,
    var picture: String,
    val ancho: Dp,
    val alto: Dp,
    var estado: Number=VOLTEADO
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