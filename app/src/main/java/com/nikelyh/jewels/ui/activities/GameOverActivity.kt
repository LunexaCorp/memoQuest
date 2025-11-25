package com.nikelyh.jewels.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nikelyh.jewels.MainActivity
import com.nikelyh.jewels.ui.theme.JewelsTheme
import com.nikelyh.jewels.R

class GameOverActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JewelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PanelGameOver(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun PanelGameOver(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Header()
        Body(
            onRetry = {
                MRedireccionActivity(MBusquedaActivity::class.java)
            },
            onRendirse = {
                MRedireccionActivity(MainActivity::class.java)
            }
        )
    }
}

val BungeeSpice = FontFamily(
    Font(R.font.bungee_spice, FontWeight.Normal)
)


@Composable
fun Header(modifier: Modifier = Modifier){
    Text(
        modifier = modifier,
        text = "PERDISTE",
        color = Color.White,
        fontFamily = BungeeSpice,
        fontSize = 40.sp
    )
}

@Composable
fun Body(modifier: Modifier = Modifier, onRetry: () -> Unit , onRendirse: () -> Unit){
    Image(
        painter = painterResource(R.drawable.game_over),
        contentDescription = "game over"
    )
    Button(
        modifier = Modifier
            .padding(start = 0.dp, top = 30.dp, end = 0.dp, bottom = 15.dp)
        ,
        colors = ButtonColors(
            containerColor = Color.Yellow,
            contentColor = Color.White,
            disabledContentColor = Color.Red,
            disabledContainerColor = Color.White
        ),
        onClick = {
            onRetry()
        }
    ) {
        Text(
            text = "Intentar de nuevo",
            fontSize = 30.sp,
            color = Color.Black,
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
            text = "Rendirse",
            fontSize = 30.sp,
            fontFamily = ScienceFont,
            color = Color.Black
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PanelGameOverPreview() {
    JewelsTheme {
        PanelGameOver()
    }
}


@Composable
fun MRedireccionActivity(activity: Class<ComponentActivity>){
    val context = LocalContext.current
    val intent = Intent(context, activity)
    context.startActivity(intent)
}