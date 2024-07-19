package com.xiaojinzi.component.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.file.Directory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Internal

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
interface ParametersImpl : InstrumentationParameters {

    @get:Internal
    val inputFiles: ListProperty<Directory>
}