package com.sushkevych.securitydevices.beanpostprocessor

import com.sushkevych.securitydevices.annotation.DeviceAuthorization
import com.sushkevych.securitydevices.dto.request.UserRequest
import com.sushkevych.securitydevices.model.MongoDeviceStatus
import com.sushkevych.securitydevices.repository.DeviceStatusRepository
import com.sushkevych.securitydevices.service.UserService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.InvocationHandler
import org.springframework.cglib.proxy.Proxy
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class DeviceAuthorizationBeanPostProcessor : BeanPostProcessor {

    @Autowired
    private lateinit var deviceStatusRepository: DeviceStatusRepository

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is UserService) {
            val methods = bean::class.java.methods
            if (methods.any { it.isAnnotationPresent(DeviceAuthorization::class.java) }) {
                return Proxy.newProxyInstance(
                    bean.javaClass.classLoader,
                    bean.javaClass.interfaces,
                    DeviceAuthorizationInvocationHandler(bean, deviceStatusRepository)
                )
            }
        }
        return bean
    }
}
@Suppress("SpreadOperator")
class DeviceAuthorizationInvocationHandler(
    private val bean: Any,
    private val deviceStatusRepository: DeviceStatusRepository
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val methodParams = args ?: emptyArray()
        val result = method.invoke(bean, *methodParams)

        if (args?.any { it is UserRequest } == true) {
            val userRequest = args.find { it is UserRequest } as UserRequest
            createDeviceStatus(userRequest)
        }

        return result
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
