package com.chonmb.xxl.link.fragment.line

import com.chonmb.xxl.link.config.fragmentConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiImportList
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiImportStatementBase
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier

fun getJobHandlerName(method: PsiMethod): String {
    val annotationAttributes =
        method.annotations.find { it.qualifiedName == fragmentConfig.targetMethodAnnotation }?.attributes
    annotationAttributes?.let {
        return if (it.size > 1) {
            (it.find { it1 -> it1.attributeName == fragmentConfig.targetMethodAnnotationField }?.attributeValue as JvmAnnotationConstantValue)
                .constantValue.toString()
        } else {
            (it.getOrNull(0)?.attributeValue as JvmAnnotationConstantValue).constantValue.toString()
        }
    }
    return ""
}

fun freshLineMarker(project: Project?, element: PsiElement?) {
    element?.let { DaemonCodeAnalyzer.getInstance(project).restart(it.containingFile) }
}

fun freshEditFileMarker(project: Project?) {
    project?.let {
        val psi=FileEditorManager.getInstance(it).selectedTextEditor?.virtualFile?.findPsiFile(project)
        psi?.let { p0 -> DaemonCodeAnalyzer.getInstance(project).restart(p0) }
    }
}

fun getTooltipInfo(method: PsiMethod): String {
    val jobHandler=getJobHandlerName(method)
    return "执行环境: ${globalConfig.activeEnv}\n目标域名: ${globalConfig.domain}\n任务描述: ${xxlService.jobs[jobHandler]?.jobDesc}\n任务标记: $jobHandler"
}

class XxlFunctionLineMarkerInfo : LineMarkerInfo<PsiElement> {

    val jobHandlerName: String
    val method: PsiMethod

    constructor(method: PsiMethod, targetElement: PsiElement) : super(
        targetElement,
        targetElement.textRange,
        jobIcons.getLineMarkerIcon(getJobHandlerName(method)),  // 使用 IDEA 的执行图标
        { getTooltipInfo(method) }, // 悬停提示
        null,
        GutterIconRenderer.Alignment.RIGHT,
        { "" }
    ) {
        this.method = method
        jobHandlerName = getJobHandlerName(method)
    }

    override fun createGutterRenderer(): GutterIconRenderer? {
        return XxlFunctionMenuRender(this)
    }

}
