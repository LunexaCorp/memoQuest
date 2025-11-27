package com.nikelyh.jewels.ui.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.R
import kotlinx.coroutines.delay

class MParejasActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParejasGameScreen()
        }
    }
}

// clase de la carta
data class Carta(
    val id: Int,
    val valor: Int,
    val imagenResId: Int,
    val descubierta: Boolean = false,
    val encontrada: Boolean = false
)

@Composable
fun ParejasGameScreen() {

    val context = LocalContext.current

    var cartas by remember { mutableStateOf(value = generarCartas()) }
    val totalParejas = cartas.size / 2

    var tiempo by remember { mutableIntStateOf(value = 60) }
    var primeraCarta by remember { mutableStateOf<Carta?>(null) }
    var bloquearClicks by remember { mutableStateOf(false) }
    var gameWon by remember { mutableStateOf(false) }

    // timepo
    LaunchedEffect(key1 = Unit) {
        while (tiempo > 0 && !gameWon) {
            delay(1000)
            tiempo--
        }

        if (tiempo == 0 && !gameWon) {
            Toast.makeText(context,"Perdiste, se te acabo el tiempo", Toast.LENGTH_SHORT).show()
            delay(800)
            (context as? Activity)?.finish()
        }
    }

    // manejo de fallas
    LaunchedEffect(key1=bloquearClicks) {
        if (bloquearClicks) {
            delay(600)
            cartas = cartas.map {
                if (it.descubierta && !it.encontrada)
                    it.copy(descubierta = false)
                else it
            }
            primeraCarta = null
            bloquearClicks = false
        }
    }

    // victoria
    LaunchedEffect(key1=gameWon) {
        if (gameWon) {
            Toast.makeText(context, "Juego terminado :D", Toast.LENGTH_LONG).show()
            delay(1200)
            (context as? Activity)?.finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Memory LP",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tiempo: $tiempo",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))


        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(16.dp)
        ) { 
            items(cartas, key = { it.id }) { carta ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(90.dp)
                        .clickable(enabled = !bloquearClicks && !carta.encontrada) {

                            if (!carta.descubierta && !carta.encontrada && tiempo > 0 && !gameWon) {

                                cartas = cartas.map {
                                    if (it.id == carta.id) it.copy(descubierta = true) else it
                                }

                                val actual = cartas.first { it.id == carta.id }

                                // primera en voltear
                                if (primeraCarta == null) {
                                    primeraCarta = actual

                                } else {

                                    val primera = primeraCarta!!

                                    // encontro la pareja??
                                    if (primera.valor == actual.valor) {

                                        Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()

                                        cartas = cartas.map {
                                            if (it.id == actual.id || it.id == primera.id)
                                                it.copy(encontrada = true, descubierta = true)
                                            else it
                                        }

                                        primeraCarta = null

                                        // encontro todas?
                                        if (cartas.count { it.encontrada } / 2 >= totalParejas) {
                                            gameWon = true
                                        }

                                    } else {
                                        Toast.makeText(context, "¡Fallaste!", Toast.LENGTH_SHORT).show()
                                        bloquearClicks = true
                                    }
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (carta.descubierta || carta.encontrada) Color.White else Color.Black
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (carta.descubierta || carta.encontrada) {

                            Image(
                                painter = painterResource(id = carta.imagenResId),
                                contentDescription = "Carta ${carta.valor}",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(4.dp)
                            )

                        } else {

                            Image(
                                painter = painterResource(id = R.drawable.revez),
                                contentDescription = "Carta Oculta",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

// las cartas con imagenes
fun generarCartas(): List<Carta> {

    val imagenes = listOf(
        R.drawable.card_simple_dart,
        R.drawable.card_simple_java,
        R.drawable.card_simple_js,
        R.drawable.card_simple_kotlin,
        R.drawable.card_simple_php,
        R.drawable.card_simple_python
    )

    val base = imagenes.mapIndexed { index, img ->
        Carta(
            id = index * 2,
            valor = index,
            imagenResId = img
        )
    }

    return (base + base.map { it.copy(id = it.id + 1) }).shuffled()
}
