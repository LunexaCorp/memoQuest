package com.nikelyh.jewels.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface GameDao{
    // Gestión de Monedas
    @Query("SELECT * FROM usuario WHERE id = 1")
    fun getUsuario(): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarUsuario(usuario: UsuarioEntity)

    @Query("UPDATE usuario SET monedas = :cantidad WHERE id = 1")
    fun actualizarMonedas(cantidad: Int)

    // Gestión de Cartas
    @Query("SELECT resourceId FROM inventario_cartas")
    fun getCartasCompradas(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun comprarCarta(carta: CartaCompradaEntity)
}