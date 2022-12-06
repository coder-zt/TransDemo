package com.xiaojinzi.component.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.xiaojinzi.component.ComponentActivityStack
import com.xiaojinzi.component.anno.support.CheckClassNameAnno
import com.xiaojinzi.component.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface IRouterRequestBuilder<T : IRouterRequestBuilder<T>> :
    IURIBuilder<T>,
    IBundleBuilder<T> {

    val options: Bundle?
    val intentFlags: List<Int>
    val intentCategories: List<String>

    val autoCancel: Boolean?
    val context: Context?
    val fragment: Fragment?

    /**
     * 是否是跳转拿 [com.xiaojinzi.component.bean.ActivityResult] 的
     */
    val isForResult: Boolean

    /**
     * 是否是为了目标 Intent
     */
    val isForTargetIntent: Boolean

    val requestCode: Int?

    val intentConsumer: Consumer<Intent>?

    val beforeRouteAction: (() -> Unit)?
    val beforeStartActivityAction: (() -> Unit)?
    val afterStartActivityAction: (() -> Unit)?
    val afterRouteSuccessAction: (() -> Unit)?
    val afterActivityResultRouteSuccessAction: (() -> Unit)?
    val afterRouteErrorAction: (() -> Unit)?
    val afterRouteEventAction: (() -> Unit)?

    fun autoCancel(autoCancel: Boolean): T
    fun context(context: Context?): T
    fun fragment(fragment: Fragment?): T
    fun isForResult(isForResult: Boolean): T
    fun isForTargetIntent(isForTargetIntent: Boolean): T
    fun requestCode(requestCode: Int?): T
    fun beforeRouteAction(@UiThread action: Action?): T
    fun beforeRouteAction(@UiThread action: (() -> Unit)?): T
    fun beforeStartActivityAction(@UiThread action: Action?): T
    fun beforeStartActivityAction(@UiThread action: (() -> Unit)?): T
    fun afterStartActivityAction(@UiThread action: Action?): T
    fun afterStartActivityAction(@UiThread action: (() -> Unit)?): T
    fun afterRouteSuccessAction(@UiThread action: Action?): T

    fun afterRouteSuccessAction(@UiThread action: (() -> Unit)?): T

    fun afterActivityResultRouteSuccessAction(@UiThread action: Action?): T
    fun afterActivityResultRouteSuccessAction(@UiThread action: (() -> Unit)?): T

    fun afterRouteErrorAction(@UiThread action: Action?): T
    fun afterRouteErrorAction(@UiThread action: (() -> Unit)?): T
    fun afterRouteEventAction(@UiThread action: Action?): T
    fun afterRouteEventAction(@UiThread action: (() -> Unit)?): T
    fun intentConsumer(@UiThread intentConsumer: Consumer<Intent>?): T
    fun addIntentFlags(vararg flags: Int): T
    fun addIntentCategories(vararg categories: String): T
    fun options(options: Bundle?): T
    fun build(): RouterRequest

}

@Suppress("UNCHECKED_CAST")
class RouterRequestBuilderImpl<T : IRouterRequestBuilder<T>>(
    context: Context? = null,
    fragment: Fragment? = null,
    private val uriBuilder: IURIBuilderImpl<T> = IURIBuilderImpl(),
    private val bundleBuilder: IBundleBuilderImpl<T> = IBundleBuilderImpl(),
) : IRouterRequestBuilder<T>,
    IURIBuilder<T> by uriBuilder,
    IBundleBuilder<T> by bundleBuilder // 占位
{

    override var options: Bundle? = null

    /**
     * Intent 的 flag,允许修改的
     */
    override val intentFlags: MutableList<Int> = mutableListOf()

    /**
     * Intent 的 类别,允许修改的
     */
    override val intentCategories: MutableList<String> = mutableListOf()

    override var autoCancel: Boolean = true
    override var context: Context? = null
    override var fragment: Fragment? = null

    override var requestCode: Int? = null
    override var isForResult = false
    override var isForTargetIntent = false

    override var intentConsumer: Consumer<Intent>? = null

    /**
     * 路由开始之前
     */
    override var beforeRouteAction: (() -> Unit)? = null

    /**
     * 执行 [Activity.startActivity] 之前
     */
    override var beforeStartActivityAction: (() -> Unit)? = null

    /**
     * 执行 [Activity.startActivity] 之后
     */
    override var afterStartActivityAction: (() -> Unit)? = null

    /**
     * 跳转成功之后的 Callback
     * 此时的跳转成功仅代表目标界面启动成功, 不代表跳转拿数据的回调被回调了
     * 假如你是跳转拿数据的, 当你跳转到 A 界面, 此回调就会回调了,
     * 当你拿到 Intent 的回调了, 和此回调已经没关系了
     */
    override var afterRouteSuccessAction: (() -> Unit)? = null

    override var afterActivityResultRouteSuccessAction: (() -> Unit)? = null

    /**
     * 跳转失败之后的 Callback
     */
    override var afterRouteErrorAction: (() -> Unit)? = null

    /**
     * 跳转成功和失败之后的 Callback
     */
    override var afterRouteEventAction: (() -> Unit)? = null

    var thisObject: T = this as T
        set(value) {
            uriBuilder.thisObject = value
            bundleBuilder.thisObject = value
            field = value
        }

    private fun getRealDelegateImpl(): T {
        return thisObject
    }

    override fun autoCancel(autoCancel: Boolean): T {
        this.autoCancel = autoCancel
        return getRealDelegateImpl()
    }

    override fun context(context: Context?): T {
        this.context = context
        return getRealDelegateImpl()
    }

    override fun fragment(fragment: Fragment?): T {
        this.fragment = fragment
        return getRealDelegateImpl()
    }

    override fun isForResult(isForResult: Boolean): T {
        this.isForResult = isForResult
        return getRealDelegateImpl()
    }

    override fun isForTargetIntent(isForTargetIntent: Boolean): T {
        this.isForTargetIntent = isForTargetIntent
        return getRealDelegateImpl()
    }

    override fun requestCode(requestCode: Int?): T {
        this.requestCode = requestCode
        return getRealDelegateImpl()
    }

    override fun beforeRouteAction(@UiThread action: Action?): T {
        return beforeRouteAction(action = {
            action?.run()
        })
    }

    override fun beforeRouteAction(@UiThread action: (() -> Unit)?): T {
        beforeRouteAction = action
        return getRealDelegateImpl()
    }

    override fun beforeStartActivityAction(@UiThread action: Action?): T {
        return beforeStartActivityAction(action = {
            action?.run()
        })
    }

    override fun beforeStartActivityAction(@UiThread action: (() -> Unit)?): T {
        beforeStartActivityAction = action
        return getRealDelegateImpl()
    }

    override fun afterStartActivityAction(@UiThread action: Action?): T {
        return afterStartActivityAction(action = {
            action?.run()
        })
    }

    override fun afterStartActivityAction(@UiThread action: (() -> Unit)?): T {
        afterStartActivityAction = action
        return getRealDelegateImpl()
    }

    override fun afterRouteSuccessAction(@UiThread action: Action?): T {
        return afterRouteSuccessAction(action = {
            action?.run()
        })
    }

    override fun afterRouteSuccessAction(@UiThread action: (() -> Unit)?): T {
        afterRouteSuccessAction = action
        return getRealDelegateImpl()
    }

    override fun afterActivityResultRouteSuccessAction(@UiThread action: Action?): T {
        return afterActivityResultRouteSuccessAction(action = {
            action?.run()
        })
    }

    override fun afterActivityResultRouteSuccessAction(@UiThread action: (() -> Unit)?): T {
        afterActivityResultRouteSuccessAction = action
        return getRealDelegateImpl()
    }

    override fun afterRouteErrorAction(@UiThread action: Action?): T {
        return afterRouteErrorAction(action = {
            action?.run()
        })
    }

    override fun afterRouteErrorAction(@UiThread action: (() -> Unit)?): T {
        afterRouteErrorAction = action
        return getRealDelegateImpl()
    }

    override fun afterRouteEventAction(@UiThread action: Action?): T {
        return afterRouteEventAction(action = {
            action?.run()
        })
    }

    override fun afterRouteEventAction(@UiThread action: (() -> Unit)?): T {
        afterRouteEventAction = action
        return getRealDelegateImpl()
    }

    /**
     * 当不是自定义跳转的时候, Intent 由框架生成,所以可以回调这个接口
     * 当自定义跳转,这个回调不会回调的,这是需要注意的点
     *
     *
     * 其实目标界面可以完全的自定义路由,这个功能实际上没有存在的必要,因为你可以为同一个界面添加上多个 [com.xiaojinzi.component.anno.RouterAnno]
     * 然后每一个 [com.xiaojinzi.component.anno.RouterAnno] 都可以有不同的行为.是可以完全的代替 [RouterRequest.intentConsumer] 方法的
     *
     * @param intentConsumer Intent 是框架自动构建完成的,里面有跳转需要的所有参数和数据,这里就是给用户一个
     * 更改的机会,最好别更改内部的参数等的信息,这里提供出来其实主要是可以让你调用Intent
     * 的 [Intent.addFlags] 等方法,并不是给你修改内部的 bundle 的
     */
    override fun intentConsumer(@UiThread intentConsumer: Consumer<Intent>?): T {
        this.intentConsumer = intentConsumer
        return getRealDelegateImpl()
    }

    override fun addIntentFlags(vararg flags: Int): T {
        intentFlags.addAll(flags.toList())
        return getRealDelegateImpl()
    }

    override fun addIntentCategories(vararg categories: String): T {
        intentCategories.addAll(listOf(*categories))
        return getRealDelegateImpl()
    }

    /**
     * 用于 API >= 16 的时候,调用 [Activity.startActivity]
     */
    override fun options(options: Bundle?): T {
        this.options = options
        return getRealDelegateImpl()
    }

    /**
     * 构建请求对象,这个构建是必须的,不能错误的,如果出错了,直接崩溃掉,因为连最基本的信息都不全没法进行下一步的操作
     *
     * @return 可能会抛出一个运行时异常, 由于您的参数在构建 uri 的时候出现的异常
     */
    override fun build(): RouterRequest {
        val builder = this
        return RouterRequest(
            autoCancel = builder.autoCancel,
            context = builder.context,
            fragment = builder.fragment,
            uri = builder.buildURI(),
            requestCode = builder.requestCode,
            isForResult = builder.isForResult,
            isForTargetIntent = builder.isForTargetIntent,
            options = builder.options,
            intentFlags = Collections.unmodifiableList(builder.intentFlags),
            intentCategories = Collections.unmodifiableList(builder.intentCategories),
            bundle = Bundle().apply {
                this.putAll(builder.bundle)
            },
            intentConsumer = builder.intentConsumer,
            beforeRouteAction = builder.beforeRouteAction,
            beforeStartActivityAction = builder.beforeStartActivityAction,
            afterStartActivityAction = builder.afterStartActivityAction,
            afterRouteSuccessAction = builder.afterRouteSuccessAction,
            afterActivityResultRouteSuccessAction = builder.afterActivityResultRouteSuccessAction,
            afterRouteErrorAction = builder.afterRouteErrorAction,
            afterRouteEventAction = builder.afterRouteEventAction,
        )
    }

    init {
        this.context = context
        this.fragment = fragment
        uriBuilder.thisObject = this as T
        bundleBuilder.thisObject = this as T
    }

}

/**
 * 构建一个路由请求对象 [RouterRequest] 对象的 Builder
 *
 * @author xiaojinzi
 */
class RouterRequestBuilder(
    private val routerRequestBuilder: RouterRequestBuilderImpl<RouterRequestBuilder> = RouterRequestBuilderImpl(),
) : IRouterRequestBuilder<RouterRequestBuilder> by routerRequestBuilder {

    init {
        routerRequestBuilder.thisObject = this
    }

}

/**
 * 表示路由的一个请求类,构建时候如果参数不对是有异常会发生的,使用的时候注意这一点
 * 但是在拦截器 [RouterInterceptor] 中构建是不用关心错误的,
 * 因为拦截器的 [RouterInterceptor.intercept] 方法
 * 允许抛出异常
 *
 *
 * time   : 2018/11/29
 *
 * @author xiaojinzi
 */
@Keep
@CheckClassNameAnno
data class RouterRequest(

    /**
     * 自动取消
     */
    val autoCancel: Boolean,

    val context: Context?,
    val fragment: Fragment?,

    /**
     * 这是一个很重要的参数, 一定不可以为空,
     * 如果这个为空了,一定不能继续下去,因为很多地方直接使用这个参数的,不做空判断的
     * 而且这个参数不可以为空的
     */
    val uri: Uri,

    /**
     * 请求码
     */
    val requestCode: Int? = null,

    /**
     * 框架是否帮助用户跳转拿 [com.xiaojinzi.component.bean.ActivityResult]
     * 有 requestCode 只能说明用户使用了某一个 requestCode,
     * 会调用 [Activity.startActivityForResult].
     * 但是不代表需要框架去帮你获取到 [com.xiaojinzi.component.bean.ActivityResult].
     * 所以这个值就是标记是否需要框架帮助您去获取 [com.xiaojinzi.component.bean.ActivityResult]
     */
    val isForResult: Boolean,
    /**
     * 是否是为了目标 Intent 来的
     */
    val isForTargetIntent: Boolean,
    /**
     * 跳转的时候 options 参数
     */
    val options: Bundle?,
    /**
     * Intent 的 flag, 集合不可更改
     */
    val intentFlags: List<Int>,
    /**
     * Intent 的 类别, 集合不可更改
     */
    val intentCategories: List<String>,
    /**
     * 携带的数据
     */
    val bundle: Bundle,
    /**
     * Intent 的回调, 可以让你做一些事情
     */
    val intentConsumer: Consumer<Intent>?,
    /**
     * 这个 [Action] 是在路由开始的时候调用的.
     * 和 [Activity.startActivity] 不是连着执行的.
     * 中间 post 到主线程的操作
     */
    val beforeRouteAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在 [Activity.startActivity] 之前调用的.
     * 和 [Activity.startActivity] 是连着执行的.
     */
    val beforeStartActivityAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在 [Activity.startActivity] 之后调用的.
     * 和 [Activity.startActivity] 是连着执行的.
     */
    val afterStartActivityAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在结束之后调用的.
     * 方法中 post 到主线程完成的
     */
    val afterRouteSuccessAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在路由 [com.xiaojinzi.component.bean.ActivityResult] 结束之后调用的.
     * 方法中 post 到主线程完成的
     */
    val afterActivityResultRouteSuccessAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在结束之后调用的.
     * 方法中 post 到主线程完成的
     */
    val afterRouteErrorAction: (() -> Unit)?,
    /**
     * 这个 [Action] 是在结束之后调用的.
     * 方法中 post 到主线程完成的
     */
    val afterRouteEventAction: (() -> Unit)?,
) // 占位
{

    companion object {
        const val KEY_SYNC_URI = "_componentSyncUri"
    }

    /**
     * 同步 Query 到 Bundle 中
     */
    suspend fun syncUriToBundle() {
        withContext(context = Dispatchers.IO) {
            // 如果 URI 没有变化就不同步了
            if (bundle.getInt(KEY_SYNC_URI) != uri.hashCode()) {
                ParameterSupport.syncUriToBundle(uri = uri, bundle = bundle)
                // 更新新的 hashCode
                bundle.putInt(KEY_SYNC_URI, uri.hashCode())
            }
        }
    }

    /**
     * 从 [Fragment] 和 [Context] 中获取上下文
     */
    val rawContext: Context?
        get() {
            var rawContext: Context? = null
            if (context != null) {
                rawContext = context
            } else if (fragment != null) {
                rawContext = fragment.context
            }
            return rawContext
        }

    /**
     * 从 [Fragment] 和 [Context] 中获取上下文
     *
     * 参数中的 [RouterRequest.context] 可能是一个 [android.app.Application] 或者是一个
     * [android.content.ContextWrapper] 或者是一个 [Activity]
     * 无论参数的类型是哪种, 此方法的返回值就只有两种类型：
     * 1. [android.app.Application]
     * 2. [Activity]
     *
     * 如果返回的是 [Activity] 的 [Context],
     * 当 [Activity] 销毁了就会返回 null
     * 另外就是返回 [android.app.Application]
     *
     * @return [Context], 可能为 null, null 就只有一种情况就是界面销毁了.
     * 构建 [RouterRequest] 的时候已经保证了
     */
    val rawAliveContext: Context?
        get() {
            val rawAct = Utils.getActivityFromContext(rawContext)
            // 如果不是 Activity 可能是 Application,所以直接返回
            return if (rawAct == null) {
                rawContext
            } else {
                // 如果是 Activity 并且已经销毁了返回 null
                if (Utils.isActivityDestroyed(rawAct)) {
                    null
                } else {
                    rawContext
                }
            }
        }

    /**
     * 从 [Context] 中获取 [Activity], [Context] 可能是 [android.content.ContextWrapper]
     *
     * @return 如果 Activity 销毁了就会返回 null
     */
    val aliveActivity: Activity?
        get() {
            if (context == null) {
                return null
            }
            val realActivity = Utils.getActivityFromContext(context)
                ?: return null
            return if (Utils.isActivityDestroyed(realActivity)) {
                null
            } else realActivity
        }

    /**
     * 从参数 [Fragment] 和 [Context] 获取 Activity,
     *
     * @return 如果 activity 已经销毁并且 fragment 销毁了就会返回 null
     */
    val rawAliveActivity: Activity?
        get() {
            var rawActivity = aliveActivity
            if (rawActivity == null) {
                if (fragment != null) {
                    rawActivity = fragment.activity
                }
            }
            if (rawActivity == null) {
                return null
            }
            return if (Utils.isActivityDestroyed(activity = rawActivity)) {
                null
            } else {
                // 如果不是为空返回的, 那么必定不是销毁的
                rawActivity
            }
        }

    /**
     * 首先调用 [.getRawActivity] 尝试获取此次用户传入的 Context 中是否有关联的 Activity
     * 如果为空, 则尝试获取运行中的所有 Activity 中顶层的那个
     */
    val rawOrTopAliveActivity: Activity?
        get() {
            var result = rawAliveActivity
            if (result == null) {
                // 如果不是为空返回的, 那么必定不是销毁的
                result = ComponentActivityStack.topAliveActivity
            }
            return result
        }

    /**
     * 这里转化的对象会比 [RouterRequestBuilder] 对象中的参数少一个 [RouterRequestBuilder.url]
     * 因为 uri 转化为了 scheme,host,path,queryMap 那么这时候就不需要 url 了
     */
    fun toBuilder(): RouterRequestBuilder {

        val builder = RouterRequestBuilder()

        builder.autoCancel(autoCancel = autoCancel)

        // 有关界面的两个
        builder.context(context = context)
        builder.fragment(fragment = fragment)

        // 还原一个 Uri 为各个零散的参数
        builder.scheme(scheme = uri.scheme!!)
        builder.userInfo(userInfo = uri.userInfo)
        builder.host(host = uri.host)
        builder.path(path = uri.path)

        val queryParameterNames = uri.queryParameterNames
        if (queryParameterNames != null) {
            for (queryParameterName in queryParameterNames) {
                builder.query(
                    queryName = queryParameterName,
                    queryValue = uri.getQueryParameter(queryParameterName)!!
                )
            }
        }

        // 不能拆分每个参数, 因为可能 url 中可能没有 path 参数
        // builder.url(url = uri.toString())

        builder.bundle.putAll(bundle)
        builder.requestCode(requestCode = requestCode)
        builder.isForResult(isForResult = isForResult)
        builder.isForTargetIntent(isForTargetIntent = isForTargetIntent)
        builder.options(options = options)
        // 这里需要新创建一个是因为不可修改的集合不可以给别人
        builder.addIntentCategories(*intentCategories.toTypedArray())
        builder.addIntentFlags(*intentFlags.toIntArray())
        builder.intentConsumer(intentConsumer = intentConsumer)
        builder.beforeRouteAction(action = beforeRouteAction)
        builder.beforeStartActivityAction(action = beforeStartActivityAction)
        builder.afterStartActivityAction(action = afterStartActivityAction)
        builder.afterRouteSuccessAction(action = afterRouteSuccessAction)
        builder.afterActivityResultRouteSuccessAction(action = afterActivityResultRouteSuccessAction)
        builder.afterRouteErrorAction(action = afterRouteErrorAction)
        builder.afterRouteEventAction(action = afterRouteEventAction)
        return builder
    }

}