package com.nikelyh.jewels

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.data.models.Modo
import com.nikelyh.jewels.ui.adapters.ModosAdapter
import com.nikelyh.jewels.ui.theme.JewelsTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import com.nikelyh.jewels.ui.activities.NRedireccionActivity
import com.nikelyh.jewels.ui.activities.StoreActivity
import com.nikelyh.jewels.ui.adapters.MonedasAdapter
import com.nikelyh.jewels.ui.adapters.TarjetaAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.nikelyh.jewels.data.AlmacenamientoJuego.inicializarSesion(this)
        MonedasAdapter.init(this)
        TarjetaAdapter.init(this)
        enableEdgeToEdge()
        setContent {
            JewelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Panel(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

val ScienceFont = FontFamily(
    Font(R.font.sciencegothic_condensed_black, FontWeight.Normal)
)
val LoraFont = FontFamily(
    Font(R.font.lora_semibold, FontWeight.Normal)
)
val PacificoFont = FontFamily(
    Font(R.font.pacifico_regular, FontWeight.Normal)
)

@Composable
fun Panel(modifier: Modifier = Modifier) {
    val listaModos = remember { ModosAdapter.obtenerLista() }
    var indiceActual by remember { mutableStateOf(0)}
    var monedas = remember { MonedasAdapter.numeroMonedas() }
    val modoSeleccionado = listaModos[indiceActual]
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(modoSeleccionado.colorFondo)
    ) {
        Header(
            modifier = Modifier
                .weight(.7f),
            monedas = monedas
        )
        CabeceraModo(
            modifier = Modifier
                .weight(1f)

            ,
            modo = modoSeleccionado
        )
        SelectorModo(
            modifier = Modifier
                .weight(3f)
            ,
            modo = modoSeleccionado,

            )
        Footer(
            modifier = Modifier
                .weight(1f)
            ,
            modo = modoSeleccionado,
            onAnterior = {
                indiceActual = (indiceActual - 1 + listaModos.size) % listaModos.size
            },
            onSiguiente = {
                indiceActual = (indiceActual + 1) % listaModos.size
            },
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, monedas: Int){
    val context = LocalContext.current

    // --- LÓGICA DE LOGIN (DIALOG) ---
    var showDialog by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    val usuarioConectado = com.nikelyh.jewels.data.AlmacenamientoJuego.usuarioActual
    val estaOnline = usuarioConectado != null

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = if(estaOnline) "Perfil de Jugador" else "Sincronizar Nube") },
            text = {
                Column {
                    if (estaOnline) {
                        Text("Hola, $usuarioConectado 👋")
                        Text("Tu progreso está seguro en la nube.", fontSize = 12.sp, color = Color.Gray)
                        Text("\n¿Deseas cerrar sesión?", fontWeight = FontWeight.Bold)
                    } else {
                        Text("Juega seguro. Ingresa un alias para guardar tus monedas y cartas en la nube.")
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Tu Alias") },
                            singleLine = true,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (estaOnline) {
                            com.nikelyh.jewels.data.AlmacenamientoJuego.desconectarUsuario(context)
                        } else {
                            if (usernameInput.isNotEmpty()) {
                                com.nikelyh.jewels.data.AlmacenamientoJuego.conectarUsuario(context, usernameInput)
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text(if(estaOnline) "Cerrar Sesión" else "Conectar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // --- DISEÑO VISUAL (HEADER) ---
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp), // Un poco de margen a los lados
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Distribuye mejor el espacio
        ) {

            // 1. IZQUIERDA: Botón de Perfil (Login)
            IconButton(
                modifier = Modifier.weight(0.7f), // Le damos poco peso, es solo un icono
                onClick = { showDialog = true }
            ) {
                Icon(
                    // Cambia de icono si está conectado
                    imageVector = if (estaOnline) Icons.Default.AccountCircle else Icons.Default.Person,
                    contentDescription = "Login",
                    // Verde si está online, Blanco si no
                    tint = if (estaOnline) Color(0xFF4CAF50) else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // 2. CENTRO: Título (Reducido un poco)
            Text(
                modifier = Modifier
                    .weight(1.8f) // El que más ocupa
                    .padding(horizontal = 4.dp),
                text = if(estaOnline) "$usuarioConectado" else "Memo Quest", // Truco: Muestra el nombre si está conectado!
                fontFamily = PacificoFont,
                fontSize = 22.sp, // BAJAMOS DE 27 A 22 para que quepa
                color = Color.White,
                maxLines = 1, // Que no se baje de línea
                overflow = TextOverflow.Ellipsis, // Pone "..." si el nombre es muy largo
                textAlign = TextAlign.Center
            )

            // 3. DERECHA: Monedas + Tienda (Agrupados)
            Row(
                modifier = Modifier.weight(1.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                // Monedas
                Image(
                    modifier = Modifier.size(30.dp), // Un pelín más pequeño
                    painter = painterResource(R.drawable.coin_pikachu),
                    contentDescription = null
                )
                Text(
                    text = "$monedas",
                    fontFamily = PacificoFont,
                    fontSize = 24.sp, // Bajamos de 30 a 24
                    color = Color.White,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )

                // Tienda
                IconButton(
                    onClick = {
                        NRedireccionActivity(StoreActivity::class.java, context)
                    }
                ) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.buy),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun CabeceraModo(modifier: Modifier = Modifier, modo: Modo){
    Row(
        modifier = modifier
            .fillMaxSize()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
            ,
            text = "Modo "+modo.nombre,
            textAlign = TextAlign.Center,
            fontSize = 40.sp,
            fontFamily = ScienceFont,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SelectorModo(
    modifier: Modifier = Modifier,
    modo: Modo,

    ){
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {


        Modo(
            modifier = Modifier
                .weight(2f)
                .fillMaxSize()
            ,
            modo = modo
        )

    }
}

@Composable
fun Modo(modifier: Modifier = Modifier, modo: Modo){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .weight(2f),
            painter = painterResource(modo.iconoResId),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
            ,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = LoraFont,
            text = modo.descripcion,
            fontSize = 30.sp
        )
    }
}

@Composable
fun Footer(
    modifier: Modifier = Modifier,
    modo: Modo,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
){
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.Center
    ){

        IconButton (
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(10.dp)
            ,
            onClick = {
                onAnterior()
            }
        ){
            Icon(
                painter = painterResource(id = R.drawable.arrow_circle_left),
                contentDescription = "Anterior",
                tint = Color.LightGray
            )

        }

        Button(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterVertically)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),

            onClick = {
                val intent = Intent(context, modo.activityDestino)
                context.startActivity(intent)
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                ,
                text = "JUGAR",
                fontSize = 30.sp,
                color = Color.White,
                fontFamily = ScienceFont,
                textAlign = TextAlign.Center
            )
        }
        IconButton (
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(10.dp)
            ,
            onClick = {
                onSiguiente()
            }
        ){
            Icon(
                painter = painterResource(id = R.drawable.arrow_circle_right),
                contentDescription = "Siguiente",
                tint = Color.LightGray
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JewelsTheme {
        Panel()
    }
}