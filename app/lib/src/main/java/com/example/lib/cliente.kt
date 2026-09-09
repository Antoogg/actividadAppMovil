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
    val recargoNoc: Double = if (horaPed>=22 || horaPed<6) 1000.0 else 0.0
    val calfiEnvioGratis: Boolean = total>=15000 && cliente.esCliFrecuente

    fun calcularTotalFinal(): Double {
        return total+recargoNoc
    }
}

class ResumenPedido(var cantidadProductos: Int = 0, var totalAPagar: Double = 0.0)


sealed class EstadoPedido {
    object Preparando : EstadoPedido()
    data class EnCamino(val repartidor: String) : EstadoPedido()
    object Entregado : EstadoPedido()
    data class Cancelado(val motivo: String) : EstadoPedido()
}


fun mostrarEstado(estado: EstadoPedido): String {
    return when (estado) {
        is EstadoPedido.Preparando -> "El pedido está en preparación."
        is EstadoPedido.EnCamino -> "El pedido va en camino con: ${estado.repartidor}."
        is EstadoPedido.Entregado -> "El pedido ha sido entregado con éxito."
        is EstadoPedido.Cancelado -> "El pedido fue cancelado. Motivo: ${estado.motivo}"
    }
}

suspend fun confirmarPago(monto: Double): Boolean {
    delay(1000)
    return monto > 0
}
suspend fun asignarDelivery(): String {
    delay(1000)
    return "Carlos"
}
suspend fun procesarPedido(monto: Double) = coroutineScope {
    launch {
        println("Notificación: Tu pedido está siendo preparado")
    }

    val pago = async { confirmarPago(monto) }
    val repartidor = async { asignarDelivery() }

    val pagoAprobado = pago.await()
    val repartidorAsignado = repartidor.await()

    val estadoFinal = if (pagoAprobado) {
        EstadoPedido.EnCamino(repartidorAsignado)
    } else {
        EstadoPedido.Cancelado("Pago rechazado")
    }

    println(mostrarEstado(estadoFinal))
}

fun main() = runBlocking {
    val cliente = Cliente("Ana Gómez", "Av España 304", esCliFrecuente = true)
    val pedProduc: List<Producto> = listOf(
        Plato("Hamburguesa Doble Queso", 12000.0, "Grande"),
        Plato("Papas Fritas", 5000.0, "Mediano"),
        Bebida("Cerveza Ambar", 3500.0, esAlcoholica = true),
        Bebida("Jugo Natural", 2500.0, esAlcoholica = false),
        Bebida("Vino Tinto", 8000.0, esAlcoholica = true),
        Plato("Producto Inválido", -2000.0, "Chico")
    )


    val bebidasAlcoholicas = pedProduc.filterIsInstance<Bebida>()
        .filter { it.esAlcoholica }
    val nomBebAlcoholicas = bebidasAlcoholicas.map { it.nombre }


    println("Pedido en preparacion para: ${cliente.nombre} entrega en ${cliente.direccion} (¿Clienta Frecuente?: ${cliente.esCliFrecuente})")

    val producValidos = pedProduc.filter { producto ->
        try {
            require(producto.precioBase >= 0){
            "Producto Inválido: ${producto.nombre} tiene precio negativo"
        }
        true

    }  catch (e: IllegalArgumentException){
        println(e.message)
        false
    }
} .also {
    println("Log: Se confirmaron ${it.size} productos válidos")
}

    val subTotal = producValidos.sumOf { it.calcularPrecioFinal() }

    val miPedido = Pedido(cliente, pedProduc, horaPed = 23)

    println("Recargo nocturno: $${miPedido.recargoNoc}")
    println("¿Aplica envío gratis?: ${miPedido.calfiEnvioGratis}")
    println("Total a pagar: $${miPedido.calcularTotalFinal()}")

    val etiquetaEnvio = cliente.let {
        val prioridad = if (it.esCliFrecuente) "Alta" else "Normal"
        "ENVÍO A: ${it.nombre} | Dirección: ${it.direccion} | Prioridad: $prioridad"
    }

    val resumen = ResumenPedido().apply {
        cantidadProductos = producValidos.size
        totalAPagar = miPedido.calcularTotalFinal()
    }

    println("Cantidad de productos: ${resumen.cantidadProductos}")
    println("Total a pagar: $${resumen.totalAPagar}")

    val costoEnvio = cliente.run {
        if (subTotal >= 15000.0 && esCliFrecuente) {
            0.0
        } else {
            3500.0
        }
    }

    println(etiquetaEnvio)
    println("Bebidas alcohólicas: $nomBebAlcoholicas | Subtotal: $$subTotal | Envío: $$costoEnvio")

    println(mostrarEstado(EstadoPedido.Preparando))

    procesarPedido(miPedido.calcularTotalFinal())
}
