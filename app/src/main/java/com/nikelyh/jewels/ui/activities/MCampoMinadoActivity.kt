package com.nikelyh.jewels.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.models.Tarjeta
import com.nikelyh.jewels.ui.adapters.ModosAdapter
import com.nikelyh.jewels.ui.adapters.MonedasAdapter
import com.nikelyh.jewels.ui.adapters.TarjetaAdapter
import com.nikelyh.jewels.ui.theme.JewelsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MCampoMinadoActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JewelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PanelModoSupervivencia(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        erroresEstablecidos = establecerErroresPermitidos()
    }
}

fun establecerErroresPermitidos(): Int{
    return (1..5).random()
}
var erroresEstablecidos: Int = establecerErroresPermitidos()
val ScienceFont = FontFamily(
    Font(R.font.sciencegothic_condensed_black, FontWeight.Normal)
)

@Composable
fun PanelModoSupervivencia(modifier: Modifier = Modifier){
    val listaTarjetas = remember {
        TarjetaAdapter.obtenerLista().toMutableStateList()
    }
    var indiceActual by remember { mutableStateOf(0)}
    val tarjetaClickeada = listaTarjetas[indiceActual]
    var cartaEnEspera: Tarjeta? by remember { mutableStateOf(null) }
    var estaProcesando by remember { mutableStateOf(false) }
    var erroresPermitidos by remember { mutableStateOf(erroresEstablecidos)}
    var meRindo by remember { mutableStateOf(false)}
    val scope = rememberCoroutineScope()
    var paresConectados by remember { mutableStateOf(0) }
    val monedasPosibles: Int = MonedasAdapter.monedasAGanar(erroresEstablecidos)

    val context = LocalContext.current


    if(meRindo || erroresPermitidos == 0){
        NRedireccionActivity(GameOverActivity::class.java, context)
    }

    var probabilidadAcierto: Float = erroresPermitidos.toFloat() / (( 6 - paresConectados ) *  2 - 1) * 100
    if(probabilidadAcierto == -100f){
        probabilidadAcierto = 100f
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ){
        TarjetasView(
            listaTarjetas = listaTarjetas,
            onTarjetaClick = {
                    tarjetaClickeada ->

                if(!estaProcesando && tarjetaClickeada.estado != Tarjeta.DE_FRENTE){
                    // Volteando carta actual
                    val indice: Int = listaTarjetas.indexOfFirst { it.id == tarjetaClickeada.id }
                    val nuevaTarjeta = Tarjeta(
                        id = tarjetaClickeada.id,
                        id_pareja = tarjetaClickeada.id_pareja,
                        picture = tarjetaClickeada.picture,
                        estado = Tarjeta.DE_FRENTE,
                        color = tarjetaClickeada.color
                    )
                    listaTarjetas[indice] = nuevaTarjeta

                    if(cartaEnEspera == null){
                        cartaEnEspera = nuevaTarjeta
                    }
                    else{
                        if(cartaEnEspera!!.id_pareja == tarjetaClickeada.id_pareja){
                            cartaEnEspera = null
                            paresConectados = paresConectados + 1

                            // Ganó
                            if(paresConectados == 6){
                                MonedasAdapter.addMonedas(context,monedasPosibles)
                                NRedireccionActivity(WinActivity::class.java, context)
                            }
                        }
                        else{
                            scope.launch {
                                estaProcesando = true
                                delay(1000)

                                val indiceActual = listaTarjetas.indexOfFirst { it.id == tarjetaClickeada!!.id }
                                val indiceAnterior = listaTarjetas.indexOfFirst { it.id == cartaEnEspera!!.id }

                                if (indiceActual != -1) {
                                    listaTarjetas[indiceActual] = listaTarjetas[indiceActual].copy(estado = Tarjeta.VOLTEADO)
                                }
                                if (indiceAnterior != -1) {
                                    listaTarjetas[indiceAnterior] = listaTarjetas[indiceAnterior].copy(estado = Tarjeta.VOLTEADO)
                                }
                                cartaEnEspera = null
                                estaProcesando = false
                                erroresPermitidos = erroresPermitidos - 1
                            }
                        }
                    }

                }
            }
        )
        DetallesView(
            modifier = Modifier
                .weight(1.3f)
            ,
            erroresPermitidos = erroresPermitidos,
            probabilidadAcierto = probabilidadAcierto,
            monedas = monedasPosibles
        )
        RendirseView(
            modifier = Modifier
                .weight(1f),
            onRendirseClick = {
                meRindo = true
            }
        )
    }
}


@Composable
fun TarjetasView(modifier: Modifier = Modifier, listaTarjetas: List<Tarjeta>,
                 onTarjetaClick: (Tarjeta) -> Unit){

    TarjetasFilaView(subList = listaTarjetas.slice(0..2),
        onTarjetaClick = onTarjetaClick)
    TarjetasFilaView(subList = listaTarjetas.slice(3..5), onTarjetaClick = onTarjetaClick)
    TarjetasFilaView(subList = listaTarjetas.slice(6..8), onTarjetaClick = onTarjetaClick)
    TarjetasFilaView(subList = listaTarjetas.slice(9..11), onTarjetaClick = onTarjetaClick)
}

@Composable
fun TarjetasFilaView(modifier: Modifier = Modifier, subList: List<Tarjeta>,
                     onTarjetaClick: (Tarjeta) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        for(tarjeta in subList){
            TarjetaView(
                modifier = Modifier.weight(1f),
                tarjeta = tarjeta,
                onTarjetaClick = { onTarjetaClick(tarjeta) }
            )
        }
    }
}

@Composable
fun TarjetaView(
    modifier: Modifier = Modifier,
    tarjeta: Tarjeta,
    onTarjetaClick: () -> Unit
){
    var color: Color = Color.Magenta
    if(tarjeta.estado == Tarjeta.DE_FRENTE){
        color = tarjeta.color
    }
    Column(
        modifier = modifier
            .padding(5.dp)
            .background(color)
    ){

        Image(
            modifier = Modifier
                .clickable{
                    onTarjetaClick()
                }
            ,
            painter = painterResource(
                if(tarjeta.estado == Tarjeta.VOLTEADO){
                    R.drawable.anverso
                }
                else{
                    tarjeta.picture
                }
            ),
            contentDescription = "carta"
        )
    }
}

@Composable
fun DetallesView(modifier: Modifier = Modifier, erroresPermitidos: Int,
                 probabilidadAcierto: Float, monedas: Int){
    Row (
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Probabilidad de acierto",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontFamily = ScienceFont,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "%.2f".format(probabilidadAcierto)+"%",
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                textAlign = TextAlign.Center,
                text = "Errores permitidos",
                fontSize = 20.sp,
                fontFamily = ScienceFont,
                fontWeight = FontWeight.Bold
            )
            Text(
                "$erroresPermitidos",
                fontSize = 30.sp
            )
        }


    }

    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .weight(1f)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                textAlign = TextAlign.Center,
                text = "Monedas a ganar",
                fontSize = 20.sp,
                fontFamily = ScienceFont,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ){

                if(erroresEstablecidos>2){
                    for (i in 1..monedas){
                        Moneda(cantidad = monedas)
                    }
                }
                else{
                    var multiplicador: Int = 0
                    var monedasMostradas: Int = 0
                    if(erroresEstablecidos == 1){
                        multiplicador = 5
                        monedasMostradas = 5
                    }else{
                        multiplicador = 3
                        monedasMostradas = 3
                    }
                    for (i in 1..monedasMostradas){
                        Moneda(cantidad = monedas)
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 3.dp),
                        text = "x$multiplicador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                }

            }

        }
    }
}
@Composable
fun Moneda(modifier: Modifier = Modifier, cantidad: Int){
    Image(
        painter = painterResource(R.drawable.coin_pikachu),
        contentDescription = "moneda"
    )
}

@Composable
fun RendirseView(
    modifier: Modifier = Modifier,
    onRendirseClick: () -> Unit
){
    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,

        ){
        Button(

            modifier = Modifier
            ,
            onClick = {
                onRendirseClick()
            }
        ) {
            Text(
                text = "No puedo",
                fontSize = 20.sp,
                fontFamily = ScienceFont,
                modifier = Modifier
                    .padding(10.dp)
            )
            Image(
                painter = painterResource(R.drawable.rendirse),
                contentDescription = null
            )

        }
    }

}

fun NRedireccionActivity(activity: Class<out Activity>, context : Context){
    val intent = Intent(context, activity)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun PanelModoSupervivenciaPreview() {
    JewelsTheme {
        PanelModoSupervivencia()
    }
}