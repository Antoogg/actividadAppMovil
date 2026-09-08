package com.example.lib

data class  Cliente (
    val nombre: String,
    val direccion: String
)

open class Producto(
    val nombre: String,
    val precioBase: Double
){
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








fun main(){





     val horaPedido = 23
    val esClienteFrecuente = true



    val recargoNoturno = if (horaPedido >= 22 || horaPedido <6){
        1000.0
    } else {
        0.0
    }
  
}
