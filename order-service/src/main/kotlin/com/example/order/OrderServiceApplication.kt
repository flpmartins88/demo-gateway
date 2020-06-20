package com.example.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.core.publisher.Flux
import java.util.*

@SpringBootApplication
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}

@Configuration
class RouterConfig {

    @Bean
    fun routes(orderHandler: OrderHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("/orders", orderHandler::findAll)
        }
    }
}

@Component
class OrderHandler {

    val orders = flowOf(
            Order(items = listOf(
                    Item("A001", "Keyboard", 1, 1000),
                    Item("A002", "Mouse", 1, 500)
            )),
            Order(items = listOf(
                    Item("A009", "Notebook", 1, 900000)
            ))
    )

    suspend fun findAll(request: ServerRequest): ServerResponse {
        LoggerFactory.getLogger(OrderHandler::class.java).debug(request.headers().toString())
        return ok().bodyAndAwait(orders)
    }

}

data class Order(val items: List<Item> = emptyList()) {
    val id: String = UUID.randomUUID().toString()
    val total: Long
        get() = this.items.map { it.price * it.quantity }.sum()
}

data class Item(
        val id: String,
        val name: String,
        val quantity: Int,
        val price: Long
)