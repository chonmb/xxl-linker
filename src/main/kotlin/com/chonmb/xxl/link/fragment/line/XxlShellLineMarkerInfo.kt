package com.chonmb.xxl.link.fragment.line


import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.res.jobIcons

import com.intellij.codeInsight.daemon.LineMarkerInfo

import com.intellij.openapi.editor.markup.GutterIconRenderer

import com.intellij.psi.PsiElement


fun getTooltipInfo(method: PsiElement): String {
    return "执行环境: ${globalConfig.activeEnv}\n目标域名: ${globalConfig.domain}"
}

fun getFileName(element: PsiElement):String{
    return element.containingFile.name.substringBeforeLast(".","Shell")
}

class XxlShellLineMarkerInfo : LineMarkerInfo<PsiElement> {

    val jobHandlerName: String
    val targetElement: PsiElement

    constructor(targetElement: PsiElement) : super(
        targetElement,
        targetElement.textRange,
        jobIcons.getLineMarkerIcon(getFileName(targetElement)),  // 使用 IDEA 的执行图标
        { getTooltipInfo(targetElement) }, // 悬停提示
        null,
        GutterIconRenderer.Alignment.RIGHT,
        { "" }
    ) {
        this.jobHandlerName=getFileName(targetElement)
        this.targetElement=targetElement
    }

    override fun createGutterRenderer(): GutterIconRenderer? {
        return XxlShellMenuRender(this)
    }

}
