package com.example.lib
import kotlinx.coroutines.*

data class Cliente(val nombre: String, val direccion: String, val esCliFrecuente: Boolean)

open class Producto(val nombre: String, val precioBase: Double) {
    open fun calcularPrecioFinal(): Double {
        return precioBase
    }
}

class Plato(
    nombre: String,
    precioBase: Double,
    val tamanoPorcion: String
) : Producto(nombre, precioBase) {

    override fun calcularPrecioFinal(): Double {
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
) : Producto(nombre, precioBase) {

    override fun calcularPrecioFinal(): Double {
        return if (esAlcoholica) {
            precioBase + 800
        } else {
            precioBase
        }
    }
}

class Pedido(val cliente: Cliente, val productos: List<Producto>, val horaPed: Int) {
    val total: Double = productos.sumOf { it.calcularPrecioFinal() }
    val recargoNoc: Double = if (horaPed >= 22 || horaPed < 6) 1000.0 else 0.0
    val calfiEnvioGratis: Boolean = total >= 15000 && cliente.esCliFrecuente

    fun calcularTotalFinal(): Double {
        return total + recargoNoc
    }
}

class ResumenPedido(var cantidadProductos: Int = 0, var totalAPagar: Double = 0.0)

// 1. Definición de Sealed Class
sealed class EstadoPedido {
    object Preparando : EstadoPedido()
    data class EnCamino(val repartidor: String) : EstadoPedido()
    object Entregado : EstadoPedido()
    data class Cancelado(val motivo: String) : EstadoPedido()
}

// 2. Expresión 'when' exhaustiva (no requiere 'else')
fun mostrarEstado(estado: EstadoPedido): String {
    return when (estado) {
        is EstadoPedido.Preparando -> "El pedido está en preparación."
        is EstadoPedido.EnCamino -> "El pedido va en camino con: ${estado.repartidor}."
        is EstadoPedido.Entregado -> "El pedido ha sido entregado con éxito."
        is EstadoPedido.Cancelado -> "El pedido fue cancelado. Motivo: ${estado.motivo}"
    }
}

fun main() {
    val cliente = Cliente("Ana Gómez", "Av España 304", esCliFrecuente = true)
    val pedProduc: List<Producto> = listOf(
        Plato("Hamburguesa Doble Queso", 12000.0, "Grande"),
        Plato("Papas Fritas", 5000.0, "Mediano"),
        Bebida("Cerveza Ambar", 3500.0, esAlcoholica = true),
        Bebida("Jugo Natural", 2500.0, esAlcoholica = false),
        Bebida("Vino Tinto", 8000.0, esAlcoholica = true)
    )

    val bebidasAlcoholicas = pedProduc.filterIsInstance<Bebida>()
        .filter { it.esAlcoholica }
    val nomBebAlcoholicas = bebidasAlcoholicas.map { it.nombre }
    val subTotal = pedProduc.sumOf { it.calcularPrecioFinal() }

    println("Pedido en preparacion para: ${cliente.nombre} entrega en ${cliente.direccion} (¿Clienta Frecuente?: ${cliente.esCliFrecuente})")

    val productosValidos = pedProduc.filter { it.precioBase > 0 }
        .also { println("Log: Se confirmaron ${it.size} productos válidos.") }

    val etiquetaEnvio = cliente.let {
        val prioridad = if (it.esCliFrecuente) "Alta" else "Normal"
        "ENVÍO A: ${it.nombre} | Dirección: ${it.direccion} | Prioridad: $prioridad"
    }

    val resumen = ResumenPedido().apply {
        cantidadProductos = productosValidos.size
        totalAPagar = productosValidos.sumOf { it.calcularPrecioFinal() }
    }

    val costoEnvio = cliente.run {
        if (esCliFrecuente) 0.0 else 3500.0
    }

    println("Costo de envío: $$costoEnvio")
    println(etiquetaEnvio)
    println("Nombres de bebidas alcohólicas: $nomBebAlcoholicas")
    println("Total del pedido: $$subTotal")

    // --- Prueba del flujo de Estados ---
    println("\n--- ESTADOS DEL PEDIDO ---")
    val estados: List<EstadoPedido> = listOf(
        EstadoPedido.Preparando,
        EstadoPedido.EnCamino("Carlos"),
        EstadoPedido.Entregado,
        EstadoPedido.Cancelado("Sin cobertura disponible")
    )

    for (estado in estados) {
        println(mostrarEstado(estado))
    }
}
