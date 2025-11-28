package com.nikelyh.jewels.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.MainActivity
import com.nikelyh.jewels.PacificoFont
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.models.Tarjeta
import com.nikelyh.jewels.ui.adapters.ModosAdapter
import com.nikelyh.jewels.ui.adapters.MonedasAdapter
import com.nikelyh.jewels.ui.adapters.TarjetaAdapter
import com.nikelyh.jewels.ui.theme.JewelsTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class StoreActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JewelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PanelStore(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

val Fredoka = FontFamily(

    Font(R.font.fredoka_condensed_bold, FontWeight.Normal),
)
@Composable
fun PanelStore(modifier: Modifier = Modifier) {
    var monedas by remember { mutableStateOf(MonedasAdapter.numeroMonedas()) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HeaderStore(
            modifier = Modifier,
            monedas = monedas
        )
        AspectoCartas(
            onCompraRealizada = {
                monedas = MonedasAdapter.numeroMonedas()
            },
            monedas = monedas
        )
    }
}

@Composable
fun HeaderStore(modifier: Modifier, monedas: Int){
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        IconButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                NRedireccionActivity(MainActivity::class.java, context = context)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.volver),
                contentDescription = "Salir"
            )
        }

        Text(
            modifier = Modifier
                .weight(1f),
            text = "STORE",
            fontFamily = Fredoka,
            fontSize = 35.sp,
            color = Color.Blue,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.coin_pikachu),
                contentDescription = "moneda pikachu"
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                ,
                text = "$monedas",
                fontFamily = PacificoFont,
                fontSize = 40.sp,
                color = Color.Black
            )
        }

    }
}

@Composable
fun AspectoCartas(
    modifier: Modifier = Modifier,
    onCompraRealizada: () -> Unit,
    monedas: Int
) {
    Column(
        modifier = Modifier
    ) {
        for (tarjeta in TarjetaAdapter.obtenerListaVenta()) {
            AspectoCarta(
                modifier = Modifier
                    .weight(1f),
                tarjeta = tarjeta,
                onCompraClick = onCompraRealizada,
                monedas = monedas
            )
        }
    }
}

@Composable
fun AspectoCarta(
    modifier: Modifier,
    tarjeta: Tarjeta,
    onCompraClick: () -> Unit,
    monedas: Int
) {
    var isComprado by remember { mutableStateOf(tarjeta.comprado) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .border(5.dp, Color.Blue),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .padding(start = 3.dp)
                .weight(1f),
            painter = painterResource(
                tarjeta.picture
            ),
            contentDescription = "carta"
        )
        Column(
            modifier = Modifier
                .weight(1.2f)
                .padding(end = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "${tarjeta.descripcion}",
                textAlign = TextAlign.Center,
                fontFamily = Fredoka,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            )
            {
                Button(
                    onClick = {
                        if(monedas >= tarjeta.precio){
                            MonedasAdapter.deleteMonedas(tarjeta.precio)
                            tarjeta.comprado = true
                            isComprado = true
                            TarjetaAdapter.activarCartaComprada(tarjeta)
                            onCompraClick()
                        }
                    },
                    enabled = !isComprado
                ){
                    if(!isComprado){
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(R.drawable.coin_pikachu),
                            contentDescription = "moneda pikachu"
                        )
                        Text(
                            text = "${tarjeta.precio}",
                            fontSize = 20.sp
                        )
                    }
                    else{
                        Text(
                            text = "COMPRADO",
                            fontSize = 23.sp
                        )
                    }

                }

            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun PanelStorePreview() {
    JewelsTheme {
        PanelStore()
    }
}