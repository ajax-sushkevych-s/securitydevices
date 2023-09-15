package com.sushkevych.securitydevices.beanpostprocessor

import com.sushkevych.securitydevices.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.repository.DeviceStatusRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.InvocationHandler
import org.springframework.cglib.proxy.Proxy
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
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

@Suppress("SpreadOperator")
class DeviceAuthorizationInvocationHandler(
    private val bean: Any,
    private val deviceStatusRepository: DeviceStatusRepository,
    private val originalBean: KClass<*>
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val methodParams = args ?: emptyArray()
        val result = method.invoke(bean, *methodParams)

        if (args?.any { it is UserRequest } == true && hasDeviceAuthorizationAnnotation(originalBean, method)) {
            val userRequest = args.find { it is UserRequest } as UserRequest
            createDeviceStatus(userRequest)
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

    private fun createDeviceStatus(userRequest: UserRequest) {
        userRequest.devices
            .mapNotNull { it.userDeviceId }
            .filter { userDeviceId -> deviceStatusRepository.findByUserDeviceId(userDeviceId) == null }
            .forEach { userDeviceId ->
                val deviceStatus = MongoDeviceStatus(
                    id = ObjectId(),
                    userDeviceId = userDeviceId,
                    status = MongoDeviceStatus.MongoDeviceStatusType.AUTHORIZATION,
                    batteryLevel = null,
                    statusDetails = null
                )
                deviceStatusRepository.save(deviceStatus)
            }
    }
}
