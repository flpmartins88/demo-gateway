package com.example.demogateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class DemoGatewayApplication

fun main(args: Array<String>) {
    runApplication<DemoGatewayApplication>(*args)
}

@Configuration(proxyBeanMethods = false)
class Router {

    @Bean
    fun myRoutes(builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes()
                .route {
                    it.path("/orders")
                            .filters { f: GatewayFilterSpec -> f.addRequestHeader("Hello", "World") }
                            .uri("http://localhost:8083")
                }
                .build()
    }

}