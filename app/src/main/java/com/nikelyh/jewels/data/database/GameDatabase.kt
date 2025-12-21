package com.nikelyh.jewels.data.database

import android.content.Context;
import androidx.room.*;

// Tabla para el estado del jugador
@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int = 1,
    val monedas: Int = 10
)

// Tabla para cartas compradas
@Entity(tableName = "inventario_cartas")
data class CartaCompradaEntity(
    @PrimaryKey val resourceId: Int
)
