package com.nikelyh.jewels.ui.adapters

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.nikelyh.jewels.data.models.Tarjeta
import com.nikelyh.jewels.R
import com.nikelyh.jewels.data.AlmacenamientoJuego

object TarjetaAdapter {

    val list_images_base = mutableListOf<Int>(
        R.drawable.payaso,
        R.drawable.chicken,
        R.drawable.dragon,
        R.drawable.smile,
        R.drawable.robot,
        R.drawable.linux
    )

    var list_images = mutableListOf<Int>()

    val list_images_venta = listOf<Int>(
        R.drawable.dino,
        R.drawable.royale_esqueletos,
        R.drawable.royale_cerdo,
        R.drawable.royale_canon,
        R.drawable.royale_mosquetera
    )

    val list_descripciones_venta = listOf<String>(
        "Un dinosaurio común y corriente, le gustan los helados",
        "Estos esqueletos quieren ir a la batalla pronto",
        "Un cerdo volador... ¿ es enserio ?",
        "Un cohete con fondo, ¿ que más puedes pedir ?",
        "La mosquetera... probablemente ya la conozcas"
    )

    val list_precios_venta = listOf<Int>(
        10,
        20,
        30,
        40,
        60
    )

    var id_pareja: Int = 1

    var venta = tarjetasVenta()
    var pares: MutableList<Tarjeta> = mutableListOf()

    fun init(context: Context){
        list_images.clear()
        list_images.addAll(list_images_base)

        val compradas = AlmacenamientoJuego.cargarCartasCompradas(context)
        for (imgId in compradas) {
            if (!list_images.contains(imgId)) {
                list_images.add(imgId)
            }
        }

        venta.forEach { tarjetaVenta ->
            if (compradas.contains(tarjetaVenta.picture)){
                tarjetaVenta.comprado = true
            }
        }

        id_pareja = 1
        pares = iniciarTarjetas()
    }

    fun tarjetasVenta(): MutableList<Tarjeta>{
        val lista: MutableList<Tarjeta> = mutableListOf()
        var index_image: Int = 0
        for(i in 1..list_images_venta.size){
            var color: Color = crearColorAleatorio()

            lista.add(Tarjeta(
                id = i,
                id_pareja = id_pareja,
                picture = list_images_venta[index_image],
                estado = Tarjeta.VOLTEADO,
                color = color,
                precio = list_precios_venta[index_image],
                descripcion = list_descripciones_venta[index_image],
                comprado = false
            ))
            index_image+=1
            id_pareja+=1
        }
        return lista
    }
    fun iniciarTarjetas(): MutableList<Tarjeta>{
        val lista: MutableList<Tarjeta> = mutableListOf()
        var index_image: Int = 0

        for(i in 1..list_images.size*2 step 2){
            var color: Color = crearColorAleatorio()

            lista.add(Tarjeta(
                id = i,
                id_pareja = id_pareja,
                picture = list_images[index_image],
                estado = Tarjeta.VOLTEADO,
                color = color
                )
            )
            lista.add(Tarjeta(
                id = i + 1,
                id_pareja = id_pareja,
                picture = list_images[index_image],
                estado = Tarjeta.VOLTEADO,
                color = color
            )
            )
            index_image+=1
            id_pareja+=1
        }
        return lista
    }

    fun crearColorAleatorio(): Color{
        val r: Int = (0..255).random()
        val g: Int = (0..255).random()
        val b: Int = (0..255).random()
        val color = Color(r, g, b)
        return color
    }

    fun obtenerLista(): List<Tarjeta>{
        return pares.shuffled()
    }

    fun obtenerListaVenta(): List<Tarjeta>{
        return venta
    }

    fun activarCartaComprada(context: Context, tarjetaComprada: Tarjeta){
        if (!list_images.contains(tarjetaComprada.picture)) {
            list_images.add(tarjetaComprada.picture)
            pares = iniciarTarjetas()
            AlmacenamientoJuego.guardarCartaComprada(context, tarjetaComprada.picture)
        }
    }


    fun tarjetasVolteadasNoAcertadas(): Int{
        var total: Int = 0
        for(tarjeta in pares){
            if(tarjeta.estado  == Tarjeta.VOLTEADO && tarjeta.acertado == false){
                total++
            }
        }
        return total
    }
}