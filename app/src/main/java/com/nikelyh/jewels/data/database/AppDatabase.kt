package com.nikelyh.jewels.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = [UsuarioEntity::class, CartaCompradaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jewels_database"
                )
                    // temporal unicamente para simplicidad
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }
}