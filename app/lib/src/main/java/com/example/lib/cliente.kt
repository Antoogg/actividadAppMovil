package com.example.lib

package com.example.lib
import kotlinx.coroutines.*

data class  Cliente (val nombre: String, val direccion: String, val esCliFrecuente: Boolean)

open class Producto(val nombre: String, val precioBase: Double){
    open fun calcularPrecioFinal(): Double{7
        return precioBase}
}

class Plato(
    nombre: String,
    precioBase: Double,
    val tamanoPorcion: String
) : Producto(nombre, precioBase){

    override fun calcularPrecioFinal(): Double{
        return if (tamanoPorcion == "Grande") {
            precioBase * 1.25
        } else {
            precioBase
        }
    }
}

class Bebida(
    nombre: String,
    precioBase: Double,
    val esAlcoholica: Boolean
) : Producto(nombre, precioBase){

    override fun calcularPrecioFinal(): Double {
        return if (esAlcoholica){
            precioBase + 800
        } else {
            precioBase
        }
    }
}

class pedido(val cliente: Cliente, val productos: List<Producto>, val horaPed:Int){
    val total:Double= productos.sumOf { it.calcularPrecioFinal() }
    val recargoNoc:Double= if(horaPed>=22 || horaPed<6) 1000.0 else 0.0
    val calfiEnvioGratis:Boolean=total>=15000 && cliente.esCliFrecuente

    fun calcularTotalFinal(): Double{
        return total+recargoNoc
    }
}

fun main(){
    val pedProduc: List<Producto> = listOf(
        Plato("Hamburguesa Doble Queso", 12000.0, "Grande"),
        Plato("Papas Fritas", 5000.0, "Mediano"),
        Bebida("Cerveza Ambar", 3500.0, esAlcoholica = true),
        Bebida("Jugo Natural", 2500.0, esAlcoholica = false),
        Bebida("Vino Tinto", 8000.0, esAlcoholica = true)
    )
    val bebidasAlcoholicas = pedProduc.filterIsInstance<Bebida>()
        .filter{it.esAlcoholica}
    val nomBebAlcoholicas = bebidasAlcoholicas.map{it.nombre}
    val subTotal = pedProduc.sumOf{it.calcularPrecioFinal()}

    println("Nombres de bebidas alcohólicas: $nomBebAlcoholicas")
    println("Total del pedido: $$subTotal")

}
