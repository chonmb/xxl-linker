package com.chonmb.xxl.link.fragment.line

import com.chonmb.xxl.link.config.fragmentConfig
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod

class XxlFunctionLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<PsiElement>? {
        if (isTargetElement(psiElement)) {
            return getXxlFunctionLineMarkerInfo(psiElement as PsiMethod)
        }
        return null
    }

    fun isTargetElement(element: PsiElement): Boolean {
        return element is PsiMethod && element.annotations.any { it.qualifiedName == fragmentConfig.targetMethodAnnotation }
                && element.name != fragmentConfig.ignoreMethod
    }

    fun getXxlFunctionLineMarkerInfo(method: PsiMethod): LineMarkerInfo<PsiElement>? {
        val leaf = method.children.find { it is PsiIdentifier } as PsiIdentifier?
        return XxlFunctionLineMarkerInfo(method, leaf ?: method)
    }

}
