package com.xiaojinzi.component.plugin

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
sealed class HandleModel {
    data class Module(val moduleName: String, val className: String) : HandleModel()
}