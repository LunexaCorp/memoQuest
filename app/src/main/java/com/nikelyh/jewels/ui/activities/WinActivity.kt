package com.nikelyh.jewels.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.MainActivity
import com.nikelyh.jewels.R
import com.nikelyh.jewels.ui.theme.JewelsTheme


class WinActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JewelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PanelWin(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun PanelWin(modifier: Modifier = Modifier){
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Yellow),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Cabecera()
        Cuerpo(
            onRetry = {
                val intent = Intent(context, MCampoMinadoActivity::class.java)
                context.startActivity(intent)
            },
            onRendirse = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )
    }
}



@Composable
fun Cabecera(modifier: Modifier = Modifier){
    Text(
        modifier = modifier,
        text = "Bien hecho",
        color = Color.White,
        fontFamily = BungeeSpice,
        fontSize = 40.sp
    )
}

@Composable
fun Cuerpo(modifier: Modifier = Modifier, onRetry: () -> Unit , onRendirse: () -> Unit){
    Image(
        painter = painterResource(R.drawable.win),
        contentDescription = "game over"
    )
    Button(
        modifier = Modifier
            .padding(start = 0.dp, top = 30.dp, end = 0.dp, bottom = 15.dp)
        ,
        colors = ButtonColors(
            containerColor = Color.Red
            ,
            contentColor = Color.White,
            disabledContentColor = Color.Red,
            disabledContainerColor = Color.White
        ),
        onClick = {
            onRetry()
        }
    ) {
        Text(
            text = "Volver a jugar",
            fontSize = 30.sp,
            color = Color.White,
            fontFamily = ScienceFont,
        )

    }

    Button(
        modifier = Modifier
            .padding(start = 0.dp, top = 15.dp, end = 0.dp, bottom = 30.dp)
        ,
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.White
        ),
        onClick = {
            onRendirse()
        }
    ) {
        Text(
            text = "Ir al menú",
            fontSize = 30.sp,
            fontFamily = ScienceFont,
            color = Color.Black
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PanelWinPreview() {
    JewelsTheme {
        PanelWin()
    }
}