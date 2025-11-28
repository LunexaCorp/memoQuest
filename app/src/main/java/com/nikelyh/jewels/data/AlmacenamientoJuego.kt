package com.nikelyh.jewels.data

import android.content.Context

object AlmacenamientoJuego{
    private const val PREFS_NOMBRE = "MemoQuestDatos"
    private const val KEY_MONEDAS = "cantidad_monedas"
    private const val KEY_CARTAS_DESBLOQUEADAS = "cartas_ids"

    // MONEDAS
    fun guardarMonedas(context: Context, cantidad: Int){
        val prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_MONEDAS, cantidad).apply()
    }

    fun cargarMonedas(context: Context): Int{
        val prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_MONEDAS, 60)
    }

    // CARTAS
    // Guardamos los IDs de las imágenes (R.drawable.xxx) como Strings
    fun guardarCartaComprada(context: Context, idImagen: Int) {
        val prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
        val setActual = prefs.getStringSet(KEY_CARTAS_DESBLOQUEADAS, mutableSetOf()) ?: mutableSetOf()

        // Creamos una copia editable del set y agregamos la nueva carta
        val nuevoSet = setActual.toMutableSet()
        nuevoSet.add(idImagen.toString())

        prefs.edit().putStringSet(KEY_CARTAS_DESBLOQUEADAS, nuevoSet).apply()
    }

    fun cargarCartasCompradas(context: Context): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NOMBRE, Context.MODE_PRIVATE)
        val setGuardado = prefs.getStringSet(KEY_CARTAS_DESBLOQUEADAS, setOf()) ?: setOf()

        // Convertimos los Strings de vuelta a Int (IDs de recursos)
        return setGuardado.map { it.toInt() }
    }
}