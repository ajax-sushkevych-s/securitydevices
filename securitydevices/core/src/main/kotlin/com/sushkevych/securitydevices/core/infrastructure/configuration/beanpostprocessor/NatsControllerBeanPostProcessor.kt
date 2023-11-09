package com.sushkevych.securitydevices.core.infrastructure.configuration.beanpostprocessor

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import com.google.protobuf.GeneratedMessageV3
import com.sushkevych.securitydevices.core.infrastructure.adapters.nats.NatsController
import reactor.core.scheduler.Schedulers

@Component
class NatsControllerBeanPostProcessor: BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            initializeNatsController(bean)
        }
        return bean
    }

    fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
            initializeNatsController(controller: NatsController<RequestT, ResponseT>) {
        controller.connection.createDispatcher { message ->
            val parsedData = controller.parser.parseFrom(message.data)
            controller.handle(parsedData)
                .map { it.toByteArray() }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe { controller.connection.publish(message.replyTo, it) }
        }.apply { subscribe(controller.subject) }
    }
}
