package com.xiaojinzi.component.support

import com.xiaojinzi.component.anno.support.CheckClassNameAnno

/**
 * 懒加载设计
 * time   : 2018/11/27
 *
 * @author : xiaojinzi
 */
@CheckClassNameAnno
interface Callable<T> {

    /**
     * 获取实际的兑现
     *
     * @return 获取实现对象
     */
    fun get(): T

}