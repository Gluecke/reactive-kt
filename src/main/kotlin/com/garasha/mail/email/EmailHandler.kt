package com.garasha.mail.email

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.time.Duration

@Component
class EmailHandler(val emailRepo: EmailRepo) {
    fun getEmail(name: String): Mono<GarashaEmail> = emailRepo.get(name)?.toMono()
            ?: throw RuntimeException("Email $name Not Found")


    fun patchEmail(emailToAdd: GarashaEmail): Mono<Unit> = emailRepo.merge(emailToAdd).toMono()

    fun getEmails(): Flux<GarashaEmail> = emailRepo.get()
            .values
            .toFlux()
            .delayElements(Duration.ofMillis(1000))

}

@Component
class EmailRepo {
    private val emails = mutableMapOf("me" to GarashaEmail("me", mutableSetOf("me@gmail.com", "i@hotmail.com")),
            "you" to GarashaEmail(emails = mutableSetOf("you@yahoo.com"), name = "you"))

    fun merge(email: GarashaEmail) {
        emails.merge(email.name, email, { e, n -> GarashaEmail(e.name, mutableSetOf(*e.emails.toTypedArray(), *n.emails.toTypedArray())) })
    }

    fun get(nameToFind: String): GarashaEmail? = emails[nameToFind]

    fun get() = emails

}

data class GarashaEmail(val name: String, var emails: MutableSet<String>)
