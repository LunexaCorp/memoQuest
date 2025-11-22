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
import androidx.compose.ui.text.font.Font
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
                .weight(.5f)
        )
        Mascota(
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
fun Header(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Text(
                modifier = Modifier
                    .weight(3f)
                ,
                text = "Memo Quest",
                fontFamily = PacificoFont,
                fontSize = 30.sp,
                color = Color.White
            )
            IconButton(
                modifier = Modifier
                    .weight(1f)
                ,
                onClick = {

                }
            ) {
                Image(
                    painter = painterResource(R.drawable.user),
                    contentDescription = null
                )
            }

        }
    }

}

@Composable
fun Mascota(modifier: Modifier = Modifier, modo: Modo){
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
                .padding(16.dp)
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