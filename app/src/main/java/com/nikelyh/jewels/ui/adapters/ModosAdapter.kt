package com.nikelyh.jewels.ui.adapters

import androidx.compose.ui.graphics.Color
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.models.Modo

class ModosAdapter {
    private var indiceActual: Int = 0
    private val modos = listOf(
        Modo(1, "Pareja", R.drawable.parejas, Color.Blue, MParejasActivity::class.java),
        Modo(2, "Supervivencia", R.drawable.supervivencia, Color.Red, MSupervivenciaActivity::class.java),
        Modo(id = 3, nombre = "Busqueda", iconoResId = R.drawable.busqueda, Color.Green, BusquedaActivity::class.java)
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