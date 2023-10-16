package com.sushkevych.securitydevices.beanpostprocessor

import com.sushkevych.securitydevices.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.repository.DeviceStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.InvocationHandler
import org.springframework.cglib.proxy.Proxy
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

@Component
class DeviceAuthorizationAnnotationBeanPostProcessor : BeanPostProcessor {
    @Autowired
    private lateinit var deviceStatusRepository: DeviceStatusRepository

    private val beans = mutableMapOf<String, KClass<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean::class

        if (beanClass.java.isAnnotationPresent(Service::class.java)) {
            beans[beanName] = beanClass
        }

        return super.postProcessBeforeInitialization(bean, beanName)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beans[beanName]?.let { beanClass ->
            Proxy.newProxyInstance(
                beanClass.java.classLoader,
                beanClass.java.interfaces,
                DeviceAuthorizationInvocationHandler(bean, deviceStatusRepository, beanClass)
            )
        } ?: bean
    }
}

class DeviceAuthorizationInvocationHandler(
    private val bean: Any,
    private val deviceStatusRepository: DeviceStatusRepository,
    private val originalBean: KClass<*>
) : InvocationHandler {

    @Suppress("SpreadOperator")
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val methodParams = args ?: emptyArray()
        val result = method.invoke(bean, *methodParams)

        if (args?.any { it is UserRequest } == true && hasDeviceAuthorizationAnnotation(originalBean, method)) {
            val userRequest = args.find { it is UserRequest } as UserRequest
            createDevicesStatus(userRequest).subscribe()
        }

        return result
    }

    private fun hasDeviceAuthorizationAnnotation(originalBean: KClass<*>, method: Method): Boolean {
        return originalBean.memberFunctions.any { beanMethod ->
            beanMethod.name == method.name &&
                    beanMethod.javaClass.typeParameters.contentEquals(method.javaClass.typeParameters) &&
                    beanMethod.findAnnotation<DeviceAuthorization>() != null
        }
    }

    private fun createDevicesStatus(userRequest: UserRequest): Mono<Unit> {
        return Flux.fromIterable(userRequest.devices)
            .flatMap { userDeviceRequest ->
                if (userDeviceRequest.userDeviceId == null) {
                    Mono.empty()
                } else {
                    getDeviceStatusOrCreateNew(userDeviceRequest.userDeviceId)
                }
            }
            .then(Unit.toMono())
    }

    private fun getDeviceStatusOrCreateNew(userDeviceId: String): Mono<MongoDeviceStatus> =
        deviceStatusRepository.findByUserDeviceId(userDeviceId)
            .switchIfEmpty(createNewDeviceStatus(userDeviceId))

    private fun createNewDeviceStatus(userDeviceId: String): Mono<MongoDeviceStatus> =
        deviceStatusRepository.save(
            MongoDeviceStatus(
                userDeviceId = userDeviceId,
                status = MongoDeviceStatus.MongoDeviceStatusType.AUTHORIZATION,
                batteryLevel = null,
                statusDetails = null
            )
        )
}
