package com.nikelyh.jewels.ui.adapters

import androidx.compose.ui.graphics.Color
import com.nikelyh.jewels.data.models.Tarjeta
import com.nikelyh.jewels.R

class TarjetaAdapter {

    private val pares: List<Tarjeta> = listOf(
        Tarjeta(
            id = 1,
            id_pareja = 1,
            picture = R.drawable.payaso,
            estado = Tarjeta.VOLTEADO,
            color = Color.Green
        ),
        Tarjeta(
            id = 2,
            id_pareja = 1,
            picture = R.drawable.payaso,
            estado = Tarjeta.VOLTEADO,
            color = Color.Green
        ),
        Tarjeta(
            id = 3,
            id_pareja = 2,
            picture = R.drawable.chicken,
            estado = Tarjeta.VOLTEADO,
            color = Color.Red
        ),
        Tarjeta(
            id = 4,
            id_pareja = 2,
            picture = R.drawable.chicken,
            estado = Tarjeta.VOLTEADO,
            color = Color.Red
        ),
        Tarjeta(
            id = 5,
            id_pareja = 3,
            picture = R.drawable.dragon,
            estado = Tarjeta.VOLTEADO,
            color = Color.Yellow
        ),
        Tarjeta(
            id = 6,
            id_pareja = 3,
            picture = R.drawable.dragon,
            estado = Tarjeta.VOLTEADO,
            color = Color.Yellow
        ),
        Tarjeta(
            id = 7,
            id_pareja = 4,
            picture = R.drawable.smile,
            estado = Tarjeta.VOLTEADO,
            color = Color.Green
        ),
        Tarjeta(
            id = 8,
            id_pareja = 4,
            picture = R.drawable.smile,
            estado = Tarjeta.VOLTEADO,
            color = Color.Green
        ),
        Tarjeta(
            id = 9,
            id_pareja = 5,
            picture = R.drawable.robot,
            estado = Tarjeta.VOLTEADO,
            color = Color.White
        ),
        Tarjeta(
            id = 10,
            id_pareja = 5,
            picture = R.drawable.robot,
            estado = Tarjeta.VOLTEADO,
            color = Color.White
        ),
        Tarjeta(
            id = 11,
            id_pareja = 6,
            picture = R.drawable.linux,
            estado = Tarjeta.VOLTEADO,
            color = Color.Gray
        ),
        Tarjeta(
            id = 12,
            id_pareja = 6,
            picture = R.drawable.linux,
            estado = Tarjeta.VOLTEADO,
            color = Color.Gray
        ),
    )

    fun obtenerLista(): List<Tarjeta>{
        return pares.shuffled()
    }

    fun tarjetasVolteadasNoAcertadas(): Int{
        var total: Int = 0
        for(tarjeta in pares){
            if(tarjeta.estado  == Tarjeta.VOLTEADO && tarjeta.acertado == false){
                total++
            }
        }
        return total
    }
}