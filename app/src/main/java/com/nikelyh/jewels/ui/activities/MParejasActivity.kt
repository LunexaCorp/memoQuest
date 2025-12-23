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
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.models.Tarjeta
import com.nikelyh.jewels.data.AlmacenamientoJuego
import com.nikelyh.jewels.ui.adapters.TarjetaAdapter
import com.nikelyh.jewels.ui.theme.JewelsTheme
import kotlinx.coroutines.delay

class MParejasActivity : ComponentActivity() {
    private lateinit var mp: MediaPlayer
    private lateinit var soundPool: SoundPool
    private var flipSound = 0
    private var correctSound = 0

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


        try {
            mp = MediaPlayer.create(this, R.raw.brackground_music).apply {
                isLooping = true
                setVolume(0.2f, 0.2f)
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error a la hora de inciar la musica", Toast.LENGTH_SHORT).show()
        }

        TarjetaAdapter.init(this)

        setContent {
            JewelsTheme {
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
        mp.release()
        soundPool.release()
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

@Composable
fun ParejasGameScreen(
    modifier: Modifier = Modifier,
    soundPool: SoundPool,
    flipSound: Int,
    correctSound: Int
) {
    val context = LocalContext.current

    // usaremos las tarjetas desbloqueadas
    val listaTarjetas = remember {
        TarjetaAdapter.obtenerLista().toMutableStateList()
    }
    val totalParejas = listaTarjetas.size / 2

    var tiempo by remember { mutableIntStateOf(60) } // 60 segundos
    var movimientos by remember { mutableIntStateOf(0) }
    var primeraCarta by remember { mutableStateOf<Tarjeta?>(null) }
    var bloquearClicks by remember { mutableStateOf(false) }
    var gameWon by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    // Variables para monedas
    val monedasPorVictoria = 5
    val monedasActuales = remember {
        mutableStateOf(AlmacenamientoJuego.cargarMonedas(context))
    }

    // Contador de parejas encontradas
    var parejasEncontradas by remember { mutableStateOf(0) }

    // Función de vibrar
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
                VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
            } else {
                @Suppress("DEPRECATION")
                VibrationEffect.createOneShot(150, 255)
            }

            vibrator.vibrate(effect)

        } catch (e: Exception) {
            // Error silencioso
        }
    }

    // TEMPORIZADOR
    LaunchedEffect(key1 = Unit) {
        while (tiempo > 0 && !gameWon && !gameOver) {
            delay(1000)
            tiempo--
        }

        if (tiempo == 0 && !gameWon) {
            // PERDISTE - tiempo agotado
            gameOver = true
            showResultDialog = true
        }
    }

    // Manejo de fallas (voltear cartas después de error)
    LaunchedEffect(key1 = bloquearClicks) {
        if (bloquearClicks) {
            delay(1000)
            // Voltear las cartas que no son pareja
            listaTarjetas.forEachIndexed { index, tarjeta ->
                if (tarjeta.estado == Tarjeta.DE_FRENTE && !tarjeta.acertado) {
                    listaTarjetas[index] = tarjeta.copy(estado = Tarjeta.VOLTEADO)
                }
            }
            primeraCarta = null
            bloquearClicks = false
        }
    }

    // Si ganaste..
    LaunchedEffect(key1 = gameWon) {
        if (gameWon) {
            delay(500)
            showResultDialog = true

            // AGREGAR 5 MONEDAS AL GANAR
            AlmacenamientoJuego.guardarMonedas(
                context,
                AlmacenamientoJuego.cargarMonedas(context) + monedasPorVictoria
            )
            monedasActuales.value = AlmacenamientoJuego.cargarMonedas(context)
        }
    }

    // Si perdiste
    LaunchedEffect(key1 = gameOver) {
        if (gameOver) {
            delay(500)
            showResultDialog = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x40000000)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Monedas actuales
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.coin_pikachu),
                        contentDescription = "Monedas",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${monedasActuales.value}",
                        color = Color(0xFFFFD700),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tiempo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tiempo",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tiempo > 10) Color(0xFF4CAF50)
                                else Color(0xFFF44336)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tiempo.toString(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Parejas encontradas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Parejas",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$parejasEncontradas/$totalParejas",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "Memory",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.coin_pikachu),
                    contentDescription = "Recompensa",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gana +$monedasPorVictoria monedas",
                    color = Color(0xFFFFD700),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tablero
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaTarjetas, key = { it.id.toInt() }) { tarjeta ->
                TarjetaJuego(
                    tarjeta = tarjeta,
                    onClick = {
                        if (!bloquearClicks && tarjeta.estado != Tarjeta.DE_FRENTE &&
                            !tarjeta.acertado && tiempo > 0 && !gameWon && !gameOver) {

                            soundPool.play(flipSound, 1f, 1f, 0, 0, 1f)
                            movimientos++

                            // Voltear carta actual
                            val indice = listaTarjetas.indexOfFirst { it.id == tarjeta.id }
                            val nuevaTarjeta = Tarjeta(
                                id = tarjeta.id,
                                id_pareja = tarjeta.id_pareja,
                                picture = tarjeta.picture,
                                estado = Tarjeta.DE_FRENTE,
                                color = tarjeta.color,
                                acertado = tarjeta.acertado
                            )
                            listaTarjetas[indice] = nuevaTarjeta

                            // Lógica de parejas
                            if (primeraCarta == null) {
                                primeraCarta = nuevaTarjeta
                            } else {
                                val primera = primeraCarta!!

                                // Verificar si son pareja
                                if (primera.id_pareja == tarjeta.id_pareja) {
                                    // Si son pareja
                                    soundPool.play(correctSound, 1.2f, 1.2f, 0, 0, 1f)

                                    // Marcar ambas como acertadas
                                    val indicePrimera = listaTarjetas.indexOfFirst { it.id == primera.id }
                                    val indiceActual = listaTarjetas.indexOfFirst { it.id == tarjeta.id }

                                    listaTarjetas[indicePrimera] = primera.copy(acertado = true)
                                    listaTarjetas[indiceActual] = nuevaTarjeta.copy(acertado = true)

                                    primeraCarta = null
                                    parejasEncontradas++

                                    // Verificar si ganó
                                    if (parejasEncontradas >= totalParejas) {
                                        gameWon = true
                                    }

                                } else {
                                    // ERROR
                                    vibrar()
                                    bloquearClicks = true
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // boton de rendirse
        Button(
            onClick = {
                gameOver = true
                showResultDialog = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(200.dp)
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.rendirse),
                    contentDescription = "Rendirse",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rendirse",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Dialogo de resultado
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = {
                Text(
                    text = if (gameWon) "¡Victoria!" else "Fin del juego",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (gameWon) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            },
            text = {
                Column {
                    Text(
                        text = if (gameWon) {
                            "¡Encontraste todas las parejas!"
                        } else if (tiempo <= 0) {
                            "Se acabó el tiempo."
                        } else {
                            "Te rendiste."
                        },
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (gameWon) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.coin_pikachu),
                                contentDescription = "Monedas ganadas",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+$monedasPorVictoria monedas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                        Text(
                            text = "Total: ${monedasActuales.value} monedas",
                            fontSize = 14.sp,
                            color = Color(0xFFAAAAAA),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Movimientos: $movimientos", fontSize = 16.sp)
                    Text(text = "Tiempo restante: $tiempo segundos", fontSize = 16.sp)
                    Text(text = "Parejas encontradas: $parejasEncontradas/$totalParejas", fontSize = 16.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false

                        // REDIRIGIR A LA PANTALLA CORRECTA
                        if (gameWon) {
                            // GANASTE -> WinActivity
                            val intent = Intent(context, WinActivity::class.java)
                            intent.putExtra("modo", "parejas")
                            intent.putExtra("monedas", monedasPorVictoria)
                            intent.putExtra("parejas", parejasEncontradas)
                            intent.putExtra("movimientos", movimientos)
                            intent.putExtra("tiempo", tiempo)
                            context.startActivity(intent)
                        } else {
                            // PERDISTE -> GameOverActivity
                            val intent = Intent(context, GameOverActivity::class.java)
                            intent.putExtra("modo", "parejas")
                            intent.putExtra("razon", if (tiempo <= 0) "tiempo" else "rendirse")
                            intent.putExtra("parejas", parejasEncontradas)
                            intent.putExtra("movimientos", movimientos)
                            context.startActivity(intent)
                        }
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gameWon) Color(0xFF4CAF50) else Color(0xFF2196F3)
                    )
                ) {
                    Text("Continuar")
                }
            }
        )
    }
}

// Tarjeta
@Composable
fun TarjetaJuego(
    tarjeta: Tarjeta,
    onClick: () -> Unit
) {
    // Animacion de volteo
    val rotation = animateFloatAsState(
        targetValue = if (tarjeta.estado == Tarjeta.DE_FRENTE) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "cardRotation"
    )

    // Efecto de escala
    val scale = animateFloatAsState(
        targetValue = if (tarjeta.acertado) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(12.dp))
            .shadow(
                elevation = if (tarjeta.estado == Tarjeta.DE_FRENTE) 8.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (tarjeta.acertado) Color(0xFF00FF00) else Color(0x80000000)
            )
            .clickable(
                enabled = !tarjeta.acertado,
                onClick = onClick
            )
            .graphicsLayer {
                rotationY = rotation.value
                scaleX = scale.value
                scaleY = scale.value
                cameraDistance = 8 * density
            },
        colors = CardDefaults.cardColors(
            containerColor = when {
                tarjeta.acertado -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                tarjeta.estado == Tarjeta.DE_FRENTE -> Color.White
                else -> Color(0xFF2D3047) // Color oscuro para el reverso
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            //frente de la pantalla
            if (rotation.value > 90f) {
                Image(
                    painter = painterResource(id = tarjeta.picture),
                    contentDescription = "Tarjeta",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(8.dp)
                        .graphicsLayer {
                            rotationY = 180f
                        }
                )

                // eefecto de cartas encontradas
                if (tarjeta.acertado) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x4000FF00),
                                        Color(0x0000FF00)
                                    )
                                )
                            )
                    )
                }
            }
            // Revez de la carta
            else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.revez),
                        contentDescription = "Carta Oculta",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}