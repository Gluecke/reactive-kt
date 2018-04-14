package com.garasha.mail.email

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import java.net.URI

@Configuration
class EmailRouting {

    @Bean
    fun emailRouter(emailHandler: EmailHandler): RouterFunction<ServerResponse> {

        return router {
            ("/emails").nest {
                val searchPath = "search"
                val savePath = "save"
                GET("/{$searchPath}") { req ->
                    val emailToFind = req.pathVariable(searchPath)
                    ServerResponse.ok().body(emailHandler.getEmail(emailToFind))
                }
                GET("/") {
                    ServerResponse.ok().body(emailHandler.getEmails())
                }
                PUT("/{$savePath}") { req ->
                    val name = req.pathVariable(savePath)

                    req.bodyToMono(GarashaEmail::class.java)
                            .subscribe { bo -> emailHandler.patchEmail(bo) }

                    val uri = URI.create("/$name")

                    ServerResponse.created(uri).build()
                }
            }
        }
    }

}