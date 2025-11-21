package com.nikelyh.jewels.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MParejasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParejasGameScreen()
        }
    }
}
//modelo de la carta para pruebas
data class Carta(
    val id: Int,
    val valor: Int,
    val descubierta: Boolean = false,
    val encontrada: Boolean = false
)

// ------------------------- UI --------------------------------
@Composable
fun ParejasGameScreen() {
    val context = LocalContext.current
    // lista inicial
    val inicial = remember { generarCartas() }
    var cartas by remember { mutableStateOf(value = inicial) }
    val totalParejas = inicial.size / 2

    var tiempo by remember { mutableStateOf(value=30) }
    var primeraCarta by remember { mutableStateOf<Carta?>(value=null) }
    var bloquearClicks by remember { mutableStateOf(value=false) }
    var gameWon by remember { mutableStateOf(value = false) }

    //comportamiento en base al tiempo
    LaunchedEffect(key1=Unit) {
        while (tiempo > 0 && !gameWon) {
            delay(1000) //pasa 1 segundo por segundo
            tiempo--
        }
        // si tiempo llega a 0, volvemos al menú
        if (tiempo == 0 && !gameWon) {
            Toast.makeText(context, "Tiempo agotado", Toast.LENGTH_SHORT).show()
            // cerramos activity
            delay(800)
            (context as? Activity)?.finish()
        }
    }

    // delay cuando hay falla
    LaunchedEffect(key1=primeraCarta, key2=cartas, key3=bloquearClicks) {
        val descubiertasNoEncontradas = cartas.filter { it.descubierta && !it.encontrada }
        if (descubiertasNoEncontradas.size == 2 && bloquearClicks) {
            delay(500)
            cartas = cartas.map {
                if (it.descubierta && !it.encontrada) it.copy(descubierta = false)
                else it
            }
            primeraCarta = null
            bloquearClicks = false
        }
    }

    // mensaje de la victoria
    LaunchedEffect(key1=gameWon) {
        if (gameWon) {
            Toast.makeText(context, "¡Has completado el juego!", Toast.LENGTH_LONG).show()
            delay(1000)
            (context as? Activity)?.finish() //cerramos activity
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tiempo: $tiempo",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        //mallas del grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(16.dp)
        ) {
            items(cartas, key = { it.id }) { carta ->

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(90.dp)
                        .background(
                            if (carta.descubierta || carta.encontrada)
                                Color(0xFFFFD700)
                            else
                                Color.Gray
                        )
                        .clickable(enabled = !bloquearClicks && !carta.encontrada) {

                            if (!carta.descubierta && !carta.encontrada && !gameWon && tiempo > 0) {

                                // tapamos la carta
                                cartas = cartas.map {
                                    if (it.id == carta.id) it.copy(descubierta = true) else it
                                }

                                val actual = cartas.first { it.id == carta.id }

                                // comprobar la primera carta
                                if (primeraCarta == null) {
                                    primeraCarta = actual

                                } else {
                                    val primera = primeraCarta!!

                                    // es pareja???
                                    if (primera.valor == actual.valor) {


                                        Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()

                                        cartas = cartas.map {
                                            if (it.id == actual.id || it.id == primera.id)
                                                it.copy(encontrada = true, descubierta = true)
                                            else it
                                        }
                                        primeraCarta = null


                                        val encontradas = cartas.count { it.encontrada }
                                        if (encontradas / 2 >= totalParejas) {
                                            gameWon = true
                                        }

                                    } else {
                                        Toast.makeText(context, "¡Fallaste!", Toast.LENGTH_SHORT).show()
                                        bloquearClicks = true
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (carta.descubierta || carta.encontrada) {
                        Text(
                            text = carta.valor.toString(),
                            color = Color.Black,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun generarCartas(): List<Carta> { //con los objetos de Carta
    val valores = (1..6).toList() // para 6 pares
    return (valores + valores)
        .shuffled()
        .mapIndexed { index, valor ->
            Carta(id = index, valor = valor)
        }
}
