package com.nikelyh.jewels.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nikelyh.jewels.data.database.AppDatabase
import com.nikelyh.jewels.data.database.CartaCompradaEntity
import com.nikelyh.jewels.data.database.UsuarioEntity

object AlmacenamientoJuego {

    var usuarioActual: String? = null

    private val dbFirebase = FirebaseFirestore.getInstance()
    private const val PREFS_AUTH = "AuthPrefs"
    private const val KEY_USERNAME = "username"

    // Gestión de Sesión
    fun inicializarSesion(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)
        usuarioActual = prefs.getString(KEY_USERNAME, null)
    }

    fun conectarUsuario(context: Context, nombreUsuario: String) {
        // 1. Guardamos el nombre en el celular
        val prefs = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERNAME, nombreUsuario).apply()
        usuarioActual = nombreUsuario

        val docRef = dbFirebase.collection("usuarios").document(nombreUsuario)

        // 2. PREGUNTAMOS A LA NUBE: "¿Ya existe este usuario?"
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // RECUPERAR DATOS (Bajar de la nube al celular)
                Toast.makeText(context, "¡Usuario encontrado! Recuperando partida...", Toast.LENGTH_SHORT).show()

                // 1. Recuperar Monedas
                val monedasNube = document.getLong("monedas")?.toInt() ?: 10
                // Guardamos en Room (Local)
                val dao = AppDatabase.getDatabase(context).gameDao()
                dao.guardarUsuario(UsuarioEntity(id = 1, monedas = monedasNube))

                // 2. Recuperar Cartas
                val data = document.data ?: emptyMap()
                data.keys.filter { it.startsWith("carta_") }.forEach { key ->
                    // Extraemos el número (ej: "carta_21312308" -> 21312308)
                    val idCarta = key.removePrefix("carta_").toIntOrNull()
                    if (idCarta != null) {
                        dao.comprarCarta(CartaCompradaEntity(resourceId = idCarta))
                    }
                }

                Toast.makeText(context, "Progreso sincronizado.", Toast.LENGTH_LONG).show()
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

                // Si el contexto es una actividad, la cerramos para que no se pueda volver atrás
                if (context is android.app.Activity) {
                    context.finish()
                }

            } else {
                // CASO B: ES NUEVO (Subir lo local a la nube)
                Toast.makeText(context, "Creando usuario nuevo en la nube...", Toast.LENGTH_SHORT).show()

                // Forzamos un guardado para que se cree el documento en Firebase
                val monedasLocales = cargarMonedas(context)
                guardarMonedas(context, monedasLocales)

                val cartasLocales = cargarCartasCompradas(context)
                cartasLocales.forEach { id -> guardarCartaComprada(context, id) }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al buscar usuario. Revisa tu internet.", Toast.LENGTH_LONG).show()
        }
    }

    fun desconectarUsuario(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USERNAME).apply()
        usuarioActual = null
        Toast.makeText(context, "Desconectado (Modo Offline)", Toast.LENGTH_SHORT).show()
    }

    // --- MONEDAS ---
    // usa Room + Firebase
    fun guardarMonedas(context: Context, cantidad: Int) {
        // 1. SIEMPRE en Room (Local)
        val dao = AppDatabase.getDatabase(context).gameDao()
        dao.guardarUsuario(UsuarioEntity(id = 1, monedas = cantidad))

        // 2. SOLO SI ESTÁ CONECTADO -> Firebase (Nube)
        usuarioActual?.let { nombre ->
            try {
                val datos = hashMapOf(
                    "monedas" to cantidad,
                    "usuario" to nombre,
                    "ultima_modificacion" to System.currentTimeMillis()
                )
                dbFirebase.collection("usuarios").document(nombre)
                    .set(datos, SetOptions.merge())
            } catch (e: Exception) {
                Log.e("FIREBASE", "Error de conexión")
            }
        }
    }

    fun cargarMonedas(context: Context): Int {
        val dao = AppDatabase.getDatabase(context).gameDao()
        return dao.getUsuario()?.monedas ?: 10
    }

    // CARTAS
    fun guardarCartaComprada(context: Context, idImagen: Int) {
        // 1. SIEMPRE en Room
        val dao = AppDatabase.getDatabase(context).gameDao()
        dao.comprarCarta(CartaCompradaEntity(resourceId = idImagen))

        // 2. SOLO SI ESTÁ CONECTADO -> Firebase
        usuarioActual?.let { nombre ->
            try {
                val datosCarta = hashMapOf<String, Any>("carta_$idImagen" to true)
                dbFirebase.collection("usuarios").document(nombre)
                    .set(datosCarta, SetOptions.merge())
            } catch (e: Exception) {
                Log.e("FIREBASE", "Error al subir carta")
            }
        }
    }

    fun cargarCartasCompradas(context: Context): List<Int> {
        // Solo leemos de Room
        val dao = AppDatabase.getDatabase(context).gameDao()
        return dao.getCartasCompradas()
    }
}