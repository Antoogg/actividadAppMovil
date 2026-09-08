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
        return precioBase * 1.25
    }
}









fun main(){
  
}
