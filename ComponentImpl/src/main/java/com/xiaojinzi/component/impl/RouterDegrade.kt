package com.xiaojinzi.component.impl

import android.content.Intent
import com.xiaojinzi.component.anno.support.CheckClassNameAnno

/**
 * 降级的一个接口, 使用 [com.xiaojinzi.component.anno.RouterDegradeAnno] 注解标记一个类为降级处理
 */
@CheckClassNameAnno
interface RouterDegrade {

    /**
     * 是否匹配这个路由
     *
     * @param request 路由请求对象
     */
    fun isMatch(request: RouterRequest): Boolean

    /**
     * 当路由失败的时候, 如果路由匹配 [RouterDegrade.isMatch]
     *
     * @param request 路由请求对象
     */
    fun onDegrade(request: RouterRequest): Intent

}