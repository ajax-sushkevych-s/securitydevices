package com.sushkevych.securitydevices.core.application.port

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CoreRepository<T, ID> {
    fun getById(id: ID): Mono<T>

    fun findAll(): Flux<T>

    fun save(entity: T): Mono<T>

    fun update(entity: T): Mono<T>

    fun deleteById(id: ID): Mono<Unit>
}
