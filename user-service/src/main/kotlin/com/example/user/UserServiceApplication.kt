package com.example.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToServerSentEvents
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDate

@SpringBootApplication
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}

@Configuration
class RouterConfig{

    @Bean
    fun applicationRoutes(userHandler: UserHandler) = router {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("/users", userHandler::findAll)
        }

        accept(MediaType.TEXT_EVENT_STREAM).nest {
            GET("/users", userHandler::stream)
        }
    }

}

@Component
class UserHandler {

    private val users = Flux.just(
            User("João", "de João e Maria", LocalDate.now().minusDays(1)),
            User("Maria", "de João e Maria", LocalDate.now().minusDays(10)),
            User("João", "do pé de feijão", LocalDate.now().minusDays(100)))

    private val userStream = Flux
            .zip(Flux.interval(Duration.ofMillis(100)), users.repeat())
            .map { it.t2 }

    fun findAll(req: ServerRequest) =
            ServerResponse.ok().body(users)

    fun stream(req: ServerRequest) =
            ServerResponse.ok().bodyToServerSentEvents(userStream)

}

data class User(
        val firstName: String,
        val lastName: String,
        val birthDate: LocalDate
)