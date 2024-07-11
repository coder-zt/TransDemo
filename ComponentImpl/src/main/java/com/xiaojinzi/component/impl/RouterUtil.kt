package com.xiaojinzi.component.impl

import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import com.xiaojinzi.component.bean.ActivityResult
import com.xiaojinzi.component.error.RouterRuntimeException
import com.xiaojinzi.component.support.LogUtil
import com.xiaojinzi.component.support.OnRouterCancel
import com.xiaojinzi.component.support.Utils
import com.xiaojinzi.component.support.executeAfterActivityResultRouteSuccessAction
import com.xiaojinzi.component.support.executeAfterRouteErrorAction
import com.xiaojinzi.component.support.executeAfterRouteSuccessAction

object RouterUtil {

    private const val TAG = Router.TAG

    /**
     * 当请求对象构建出来以后调用的
     */
    @AnyThread
    fun cancelCallback(
        request: RouterRequest?,
        callback: OnRouterCancel?
    ) {
        Utils.postActionToMainThreadAnyway {
            cancelCallbackOnMainThread(
                request,
                callback
            )
        }
        deliveryListener(
            cancelRequest = request,
            isActivityNormalCancel = true,
        )
    }

    @UiThread
    private fun cancelCallbackOnMainThread(
        request: RouterRequest?,
        callback: OnRouterCancel?,
    ) {
        if (request == null) {
            LogUtil.log(TAG, "route canceled, request is null!")
        } else {
            LogUtil.log(TAG, "route canceled：" + request.uri.toString())
        }
        if (callback == null) {
            return
        }
        callback.onCancel(request)
    }

    @AnyThread
    fun errorCallback(
        callback: Callback? = null,
        biCallback: BiCallback<*>? = null,
        errorResult: RouterErrorResult,
    ) {
        Utils.postActionToMainThreadAnyway {
            errorCallbackOnMainThread(
                callback,
                biCallback,
                errorResult
            )
        }
        deliveryListener(errorResult = errorResult)
    }

    @UiThread
    private fun errorCallbackOnMainThread(
        callback: Callback?,
        biCallback: BiCallback<*>?,
        errorResult: RouterErrorResult,
    ) {
        if (errorResult.originalRequest == null) {
            LogUtil.log(
                TAG,
                "route fail：routerRequest has not been created, errorClass is " + Utils.getRealThrowable(
                    errorResult.error
                ).javaClass.simpleName + ":" + Utils.getRealMessage(errorResult.error)
            )
        } else {
            LogUtil.log(
                TAG,
                "route fail：" + errorResult.originalRequest.uri.toString() + " and errorClass is " + Utils.getRealThrowable(
                    errorResult.error
                ).javaClass.simpleName + ",errorMsg is '" + Utils.getRealMessage(errorResult.error) + "'"
            )
        }
        // 执行 Request 中 的 errorCallback
        if (errorResult.originalRequest != null) {
            try {
                errorResult.originalRequest.executeAfterRouteErrorAction()
            } catch (e: Exception) {
                throw RouterRuntimeException(
                    message = "afterRouteErrorAction can't throw any exception!",
                    cause = e,
                )
            }
        }
        if (callback != null) {
            callback.onError(errorResult)
            callback.onEvent(null, errorResult)
        }
        biCallback?.onError(errorResult)
    }

    @AnyThread
    fun activityResultCancelCallback(
        request: RouterRequest?,
        callback: OnRouterCancel,
    ) {
        Utils.postActionToMainThreadAnyway {
            activityResultCancelCallbackOnMainThread(
                request = request,
                callback = callback
            )
        }
        deliveryListener(
            isActivityResultCancel = true,
            cancelRequest = request,
        )
    }

    @UiThread
    fun activityResultCancelCallbackOnMainThread(
        request: RouterRequest?,
        callback: OnRouterCancel?,
    ) {
        LogUtil.log(
            TAG,
            "route activityResult cancel：${request?.uri?.toString() ?: ""}"
        )
        callback?.onCancel(
            originalRequest = request,
        )
    }

    @AnyThread
    fun activityResultSuccessCallback(
        callback: BiCallback<ActivityResult>?,
        successResult: ActivityResultRouterResult,
    ) {
        Utils.postActionToMainThreadAnyway {
            activityResultSuccessCallbackOnMainThread(
                callback = callback,
                successResult = successResult
            )
        }
        deliveryListener(activityResultRouterResult = successResult)
    }

    @UiThread
    fun activityResultSuccessCallbackOnMainThread(
        callback: BiCallback<ActivityResult>?,
        successResult: ActivityResultRouterResult,
    ) {
        LogUtil.log(
            TAG,
            "route activityResult success：" + successResult.routerResult.originalRequest.uri.toString()
        )
        try {
            successResult.routerResult.finalRequest.executeAfterActivityResultRouteSuccessAction()
        } catch (e: Exception) {
            throw RouterRuntimeException(
                message = "afterActivityResultRouteSuccessAction can't throw any exception!",
                cause = e,
            )
        }
        callback?.onSuccess(
            result = successResult.routerResult,
            targetValue = successResult.activityResult,
        )
    }

    @AnyThread
    fun successCallback(
        callback: Callback?,
        successResult: RouterResult,
    ) {
        Utils.postActionToMainThreadAnyway { successCallbackOnMainThread(callback, successResult) }
        deliveryListener(
            successResult = successResult,
        )
    }

    @UiThread
    private fun successCallbackOnMainThread(
        callback: Callback?,
        result: RouterResult
    ) {
        LogUtil.log(TAG, "route success：" + result.originalRequest.uri.toString())
        // 执行 Request 中 的 afterCallback
        try {
            result.finalRequest.executeAfterRouteSuccessAction()
        } catch (e: Exception) {
            throw RouterRuntimeException(
                message = "afterRouteSuccessAction can't throw any exception!",
                cause = e,
            )
        }
        if (callback != null) {
            callback.onSuccess(result)
            callback.onEvent(result, null)
        }
    }

    @AnyThread
    fun deliveryListener(
        successResult: RouterResult? = null,
        activityResultRouterResult: ActivityResultRouterResult? = null,
        isActivityNormalCancel: Boolean = false,
        isActivityResultCancel: Boolean = false,
        errorResult: RouterErrorResult? = null,
        cancelRequest: RouterRequest? = null,
    ) {
        Utils.postActionToMainThreadAnyway {
            deliveryListenerOnMainThread(
                successResult = successResult,
                activityResultRouterResult = activityResultRouterResult,
                isActivityNormalCancel = isActivityNormalCancel,
                isActivityResultCancel = isActivityResultCancel,
                errorResult = errorResult,
                cancelRequest = cancelRequest,
            )
        }
    }

    @UiThread
    fun deliveryListenerOnMainThread(
        successResult: RouterResult? = null,
        activityResultRouterResult: ActivityResultRouterResult? = null,
        isActivityNormalCancel: Boolean = false,
        isActivityResultCancel: Boolean = false,
        errorResult: RouterErrorResult? = null,
        cancelRequest: RouterRequest? = null,
    ) {
        for (listener in Router.routerListeners) {
            try {
                if (successResult != null) {
                    listener.onSuccess(successResult = successResult)
                }
                if (activityResultRouterResult != null) {
                    listener.onActivityResultSuccess(successResult = activityResultRouterResult)
                }
                if (errorResult != null) {
                    listener.onError(errorResult = errorResult)
                }
                // 是否是获取 ActivityResult 的那种取消
                if (isActivityResultCancel) {
                    listener.onActivityResultCancel(
                        originalRequest = cancelRequest,
                    )
                } else if (isActivityNormalCancel) {
                    listener.onCancel(originalRequest = cancelRequest)
                }
            } catch (ignore: Exception) {
                // do nothing
            }
        }
    }

}