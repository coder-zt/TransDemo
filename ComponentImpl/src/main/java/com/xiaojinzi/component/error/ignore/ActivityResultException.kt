package com.xiaojinzi.component.error.ignore

import androidx.annotation.Keep

/**
 * 表示 Activity result 异常
 *
 * time   : 2018/11/03
 *
 * @author : xiaojinzi
 */
@Keep
class ActivityResultException(message: String? = null) : RuntimeException(message)