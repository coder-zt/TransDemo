package com.xiaojinzi.component.anno

import kotlin.reflect.KClass

/**
 * 标记一个实现类是某些接口的实现
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS, AnnotationTarget.PROPERTY_GETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class ServiceAnno(

    /**
     * 这个服务对应的接口
     */
    vararg val value: KClass<*>,

    /**
     * 实现类唯一的名字. 要不就不填写这个属性值, 如果不为空就得和 value 属性的数量是一致的
     * debug 的时候可以开启检查, 会检查出重名的
     */
    val name: Array<String> = [],

    /**
     * 是否是单例,默认是单例模式的
     */
    val singleTon: Boolean = true,

    /**
     * 自动初始化. 当框架初始化后, 框架会 post 一个事件到 Handler
     * 然后执行自动初始化的工作
     * 默认实现类是不会初始化的, 除非你框架初始化之后立马用到了.
     * 而有些场景的 Service 的实现类是需要尽早初始化的. 不管是否用到.
     * 比如剪切板的 Service, 比如监测网络请求的 Service 等等
     *
     * @return 是否自动初始化
     */
    val autoInit: Boolean = false

)