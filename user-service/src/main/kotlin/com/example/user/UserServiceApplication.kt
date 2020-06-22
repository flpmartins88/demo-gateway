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
import reactor.core.publisher.Mono
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
            POST("/users", userHandler::save)
        }

        accept(MediaType.TEXT_EVENT_STREAM).nest {
            GET("/users", userHandler::stream)
        }
    }

}

@Component
class UserHandler {

    private val users = mutableListOf(
            User("João", "de João e Maria", LocalDate.now().minusDays(1)),
            User("Maria", "de João e Maria", LocalDate.now().minusDays(10)),
            User("João", "do pé de feijão", LocalDate.now().minusDays(100))
    )

    fun save(request: ServerRequest): Mono<ServerResponse> {
        TODO("mensagem de erro")
    }

//        request.bodyToMono(User::class.java)
//                .doOnNext { user -> users.add(user) }
//                .flatMap { ServerResponse.created(request.uri()).body(Mono.just(it)) }




    fun findAll(request: ServerRequest) =
            ServerResponse.ok()
                    .body(Flux.fromIterable(users))

    fun stream(request: ServerRequest) =
            ServerResponse.ok().bodyToServerSentEvents(
                    Flux.zip(Flux.interval(Duration.ofMillis(100)), Flux.fromIterable(users).repeat()).map { it.t2 }
            )

}

data class User(
        val firstName: String,
        val lastName: String,
        val birthDate: LocalDate
)