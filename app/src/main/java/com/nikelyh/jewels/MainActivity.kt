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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@Composable
fun Panel(modifier: Modifier = Modifier) {
    val listaModos = remember { ModosAdapter().obtenerLista() }
    var indiceActual by remember { mutableStateOf(0)}
    val modoSeleccionado = listaModos[indiceActual]
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(modoSeleccionado.colorFondo)
    ) {
        Header(
            modifier = Modifier
                .weight(0.7f)
        )
        Mascota(
            modifier = Modifier
                .weight(1f)
        )
        SelectorModo(
            modifier = Modifier
                .weight(1.5f)
            ,
            modo = modoSeleccionado,
            onAnterior = {
                indiceActual = (indiceActual - 1 + listaModos.size) % listaModos.size
            },
            onSiguiente = {
                indiceActual = (indiceActual + 1) % listaModos.size
            },
        )
        Footer(
            modifier = Modifier
                .weight(0.5f)
            ,
            modo = modoSeleccionado
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()

        ) {
            Image(
                modifier = Modifier
                    .weight(1f),
                painter = painterResource(R.drawable.ranking),
                contentDescription = null
            )
            Image(
                modifier = Modifier
                    .weight(1f),
                painter = painterResource(R.drawable.user_square),
                contentDescription = null
            )
        }
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ){
            Text(
                text = "JEWELS",
                fontSize = 40.sp,
                color = Color.White
            )
        }
    }

}

@Composable
fun Mascota(modifier: Modifier = Modifier){
    Row(
        modifier = modifier
            .fillMaxSize()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            text = "Nivel 1",
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SelectorModo(
    modifier: Modifier = Modifier,
    modo: Modo,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
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
            nombre = modo.nombre,
            imagen = modo.iconoResId
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
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
}

@Composable
fun Modo(modifier: Modifier = Modifier, nombre: String, imagen: Int){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .weight(2f),
            painter = painterResource(imagen),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .weight(1f)
            ,
            color = Color.White,
            textAlign = TextAlign.Center,
            text = nombre,
            fontSize = 30.sp
        )
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier, modo: Modo){
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.Center
    ){
        Button(
            modifier = Modifier
                .padding(16.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),

            onClick = {
                val intent = Intent(context, modo.ActivityDestino)
                context.startActivity(intent)
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                ,
                text = "JUGAR",
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun cambiarModo(direccion: String){
    if(direccion.uppercase().equals("IZQUIERDA")){
        ModosAdapter().getModo()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JewelsTheme {
        Panel()
    }
}