package com.xiaojinzi.component.compiler.kt

import com.google.auto.service.AutoService
import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.xiaojinzi.component.anno.*
import java.util.*

class TestProcessor(
    override val environment: SymbolProcessorEnvironment,
    val logger: KSPLogger = environment.logger,
    val codeGenerator: CodeGenerator = environment.codeGenerator,
) : BaseHostProcessor(
    environment = environment,
) {

    private val TAG = "\nProcessor"

    private fun ClassName.withTypeArguments(arguments: List<TypeName>): TypeName {
        return if (arguments.isEmpty()) {
            this
        } else {
            this.parameterizedBy(arguments)
        }
    }

    override fun doProcess(resolver: Resolver): List<KSAnnotated> {

        val testClass =
            resolver.getClassDeclarationByName("com.xiaojinzi.component.demo.TestInterface")
        logger.warn("testClass = $testClass")

        testClass?.run {

            val testFunction = this.getAllFunctions().first()
            logger.warn("testFunction = $testFunction")
            logger.warn("testFunction.returnType1 = ${(testFunction.returnType?.resolve())}")

            val testFunctionReturnType2 = testFunction.returnTypeToTypeName() as? ClassName
            logger.warn("testFunction.returnType2 = $testFunctionReturnType2")

            testFunction.returnType?.resolve()?.run {

                when (val dec = this.declaration) {
                    is KSClassDeclaration -> {
                        val testTypeName = dec.toClassName().withTypeArguments(arguments.map { it.toTypeName() })
                        logger.warn("testTypeNamexxxx = $testTypeName, xxx = ${dec.classKind == ClassKind.ANNOTATION_CLASS}")
                    }

                    else -> {}
                }

            }

        }

        return emptyList()

    }

    override fun finish() {
        super.finish()
        logger.warn("$TAG finish")
    }

    override fun onError() {
        super.onError()
        logger.warn("$TAG onError")
    }

}

// @AutoService(SymbolProcessorProvider::class)
class TestProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TestProcessor(
            environment = environment,
        )
    }

}



