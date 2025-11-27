package com.nikelyh.jewels.ui.adapters

import androidx.compose.ui.graphics.Color
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.models.Modo
import com.nikelyh.jewels.ui.activities.MBusquedaActivity
import com.nikelyh.jewels.ui.activities.MParejasActivity
import com.nikelyh.jewels.ui.activities.MCampoMinadoActivity

class ModosAdapter {
    private var indiceActual: Int = 0
    private val modos = listOf(
        Modo(1,
            "Match",
            R.drawable.cards_par,
            Color.Blue,
            MParejasActivity::class.java,
            "Encuentra todos los pares antes de que se acabe el tiempo"
        ),
        Modo(
            2,
            "Campo minado",
            R.drawable.survival,
            Color.Red,
            MCampoMinadoActivity::class.java,
            "Todo es cuestión de suerte, falla una y estás fuera."
        ),
        Modo(
            id = 3,
            nombre = "Busqueda",
            iconoResId = R.drawable.target,
            Color.Magenta,
            MBusquedaActivity::class.java,
            "Memoriza el objetivo antes de que se oculte."
        )
    )

    fun obtenerLista(): List<Modo>{
        return modos
    }

    fun anterior(){
        if(indiceActual == 0){
            indiceActual = modos.size
        }
        indiceActual = indiceActual - 1
    }

    fun siguiente(){
        if(indiceActual == modos.size - 1){
            indiceActual = -1
        }
        indiceActual = indiceActual + 1
    }

    fun getModo(): Modo{
        return modos.get(indiceActual)
    }

}