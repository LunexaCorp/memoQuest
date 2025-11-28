package com.nikelyh.jewels.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.os.VibrationEffect
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
import androidx.compose.material3.Scaffold
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
import com.nikelyh.jewels.ui.theme.JewelsTheme
import kotlinx.coroutines.delay

class MParejasActivity : ComponentActivity() {
    //bibliotecas
    private lateinit var mp: MediaPlayer
    private lateinit var soundPool: SoundPool
    private var flipSound  = 0
    private var correctSound  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build()

            flipSound = soundPool.load(this, R.raw.flipcard, 1)
            correctSound = soundPool.load(this, R.raw.correct, 1)

        } catch (e: Exception) {
            Toast.makeText(this, "Error a la hora de cargar sonidos....", Toast.LENGTH_SHORT).show()
        }

        //reproducir la musica

        try {
            mp = MediaPlayer.create(this, R.raw.brackground_music).apply {
                isLooping = true
                setVolume(0.2f, 0.2f)
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error a la hora de inciar la musica", Toast.LENGTH_SHORT).show()
        }

        setContent {
            JewelsTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ParejasGameScreen(
                        modifier = Modifier.padding(innerPadding),
                        soundPool = soundPool,
                        flipSound = flipSound,
                        correctSound = correctSound

                    )
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mp.release() //Apagar cuando se sale del activity
    }
    override fun onPause() {
        super.onPause()
        if (mp.isPlaying) mp.pause()
    }

    override fun onResume() {
        super.onResume()
        mp.start()
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
fun ParejasGameScreen(
    modifier: Modifier = Modifier,
    soundPool: SoundPool,
    flipSound: Int,
    correctSound: Int
) {

    val context = LocalContext.current

    var cartas by remember { mutableStateOf(value = generarCartas()) }
    val totalParejas = cartas.size / 2

    var tiempo by remember { mutableIntStateOf(value = 30) }
    var primeraCarta by remember { mutableStateOf<Carta?>(null) }
    var bloquearClicks by remember { mutableStateOf(false) }
    var gameWon by remember { mutableStateOf(false) }

    //perdida-vibracion
    fun vibrar() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
            } else {
                @Suppress("DEPRECATION")
                VibrationEffect.createOneShot(120, 255)
            }

            vibrator.vibrate(effect)

        } catch (e: Exception) {
            Toast.makeText(context, "Vibración falló: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }




    // timepo
    LaunchedEffect(key1 = Unit) {
        while (tiempo > 0 && !gameWon) {
            delay(1000)
            tiempo--
        }

        if (tiempo == 0 && !gameWon) {
            Toast.makeText(context,"Perdiste, se te acabo el tiempo", Toast.LENGTH_SHORT).show()
            delay(600)

            val intent = Intent(context, GameOverActivity::class.java)
            intent.putExtra("modo", "parejas")
            context.startActivity(intent)
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
            delay(700)
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

                                soundPool.play(flipSound,1f,1f,0,0,1f)
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

                                        soundPool.play(correctSound,1f,1f,0,0,1f)
//                                        Toast.makeText(context, "¡Correcto!", Toast.LENGTH_SHORT).show()

                                        cartas = cartas.map {
                                            if (it.id == actual.id || it.id == primera.id){
                                                it.copy(encontrada = true, descubierta = true)
                                            } else{
                                                it
                                            }

                                        }

                                        primeraCarta = null

                                        // encontro todas?
                                        if (cartas.count { it.encontrada } / 2 >= totalParejas) {
                                            gameWon = true
                                        }

                                    } else {
//                                        Toast.makeText(context, "¡Fallaste!", Toast.LENGTH_SHORT).show()
                                        vibrar()
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
