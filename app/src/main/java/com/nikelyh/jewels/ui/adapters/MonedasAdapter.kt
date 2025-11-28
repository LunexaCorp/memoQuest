package com.nikelyh.jewels.ui.adapters

import com.nikelyh.jewels.data.models.Moneda
import com.nikelyh.jewels.R
import com.nikelyh.jewels.ui.activities.erroresEstablecidos

object MonedasAdapter{
    //val monedas: MutableList<Moneda> = mutableListOf()
    val monedas: MutableList<Moneda> = generamonedas(160)


    fun addMonedas(cantidad: Int=1){
        for(i in 1..cantidad){
            monedas.add(Moneda(
                id = i,
                nombre = "Moneda $i",
                iconoResId = R.drawable.coin_pikachu
            ))
        }
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

    fun deleteMonedas(cantidad: Int=1){
        for(i in 1..cantidad){
            monedas.removeAt(monedas.size - 1)
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
            return 9
        }
        else if(erroresEstablecidos==1){
            return 25
        }
        return 2
    }
}