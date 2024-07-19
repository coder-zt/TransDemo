package com.xiaojinzi.component.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author 张滔
 * @date 2024/7/15
 * @description
 */
abstract class ASMStubClassTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val outputFile =
            File(outputFolder.asFile.get(), "com/xiaojinzi/component/support/ASMUtil.java")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
                package com.xiaojinzi.component.support;
                            
                import androidx.annotation.Keep;
                import com.xiaojinzi.component.impl.IModuleLifecycle;
                import java.util.ArrayList;
                import java.util.List;
                
                @Keep
                public class ASMUtil {
                
                    public static IModuleLifecycle findModuleApplicationAsmImpl(String str) {
                        return null;
                    }
                    
                    public static List getModuleNames() {
                        ArrayList arrayList = new ArrayList();
                        return arrayList;
                    }
                }
            """.trimIndent()
        )
    }
}
