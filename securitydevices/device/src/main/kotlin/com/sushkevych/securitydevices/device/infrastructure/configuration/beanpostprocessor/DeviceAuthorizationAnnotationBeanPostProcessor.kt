package com.sushkevych.securitydevices.device.infrastructure.configuration.beanpostprocessor

import com.sushkevych.securitydevices.devicestatus.application.port.DeviceStatusRepository
import com.sushkevych.securitydevices.devicestatus.infrastructure.repository.entity.MongoDeviceStatus
import com.sushkevych.securitydevices.core.infrastructure.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.user.domain.User
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

        if (args?.any { it is User } == true && hasDeviceAuthorizationAnnotation(originalBean, method)) {
            val userRequest = args.find { it is User } as User
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

    private fun createDevicesStatus(user: User): Mono<Unit> {
        return Flux.fromIterable(user.devices)
            .flatMap { userDevice ->
                if (userDevice.userDeviceId == null) {
                    Mono.empty()
                } else {
                    getDeviceStatusOrCreateNew(userDevice.userDeviceId!!)
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
                id = null,
                userDeviceId = userDeviceId,
                status = MongoDeviceStatus.MongoDeviceStatusType.AUTHORIZATION,
                batteryLevel = null,
                statusDetails = null
            )
        )
}
