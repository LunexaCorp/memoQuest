package com.nikelyh.jewels.ui.adapters

import android.content.Context
import com.nikelyh.jewels.data.models.Moneda
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.AlmacenamientoJuego
import com.nikelyh.jewels.data.database.AppDatabase
import com.nikelyh.jewels.data.database.UsuarioEntity
import com.nikelyh.jewels.ui.activities.erroresEstablecidos

object MonedasAdapter{
    // Referencia a la base de datos
    private lateinit var db: AppDatabase

    var monedas: MutableList<Moneda> = mutableListOf()

    fun init(context: Context){
        // Actualizamos tu lista local visual basada en la DB
        val cantidadGuardada = AlmacenamientoJuego.cargarMonedas(context)
        monedas = generamonedas(cantidadGuardada)
    }

    fun addMonedas(context: Context, cantidad: Int=1){
        for(i in 1..cantidad){
            val id = (System.currentTimeMillis() + i).toInt()
            monedas.add(Moneda(
                id = i,
                nombre = "Moneda $i",
                iconoResId = R.drawable.coin_pikachu
            ))
        }
        // Él guardará en Room (offline) Y en Firebase (online).
        AlmacenamientoJuego.guardarMonedas(context, monedas.size)
    }

    fun generamonedas(cantidad: Int=10): MutableList<Moneda>{
        var lista: MutableList<Moneda> = mutableListOf()
        for(i in 1..cantidad){
            lista.add(Moneda(
                id = i,
                nombre = "Moneda $i",
                iconoResId = R.drawable.coin_pikachu
            ))
        }
        return lista
    }

    fun deleteMonedas(context: Context, cantidad: Int=1){
        if (monedas.isNotEmpty()) {
            for (i in 1..cantidad) {
                if (monedas.isNotEmpty()) {
                    monedas.removeAt(monedas.size - 1)
                }
            }
            // GUARDAMOS EL CAMBIO TAMBIÉN AQUÍ
            AlmacenamientoJuego.guardarMonedas(context, monedas.size)
        }
    }

    fun numeroMonedas(): Int{
        return monedas.size
    }
    // Para modo minas
    fun monedasAGanar(erroresEstablecidoss: Int): Int{
        if(erroresEstablecidoss == 4){
            return 3
        }
        if(erroresEstablecidos ==3) {
            return 5
        }
        else if(erroresEstablecidos==2){
            return 15
        }
        else if(erroresEstablecidos==1){
            return 25
        }
        return 3
    }
}