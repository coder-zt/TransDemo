package com.xiaojinzi.component.anno

import com.xiaojinzi.component.anno.support.UiThreadCreateAnno

/**
 * 拦截器的注解,用这个注解标记的不是全局的拦截器,但是你可以使用特定的字符串访问到这个拦截器
 */
@UiThreadCreateAnno
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS
)
@Retention(
    AnnotationRetention.BINARY
)
annotation class InterceptorAnno(

    /**
     * 拦截器的名字,这个不能重复,使用这个支持跨组件访问其他业务组件的拦截器
     * 同时很好的让代码隔离
     */
    val value: String

)