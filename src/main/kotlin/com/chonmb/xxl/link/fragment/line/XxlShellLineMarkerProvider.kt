package com.chonmb.xxl.link.fragment.line

import com.chonmb.xxl.link.config.fragmentConfig
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.LeafPsiElement

class XxlShellLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<PsiElement>? {
        if (isTargetElement(psiElement)) {
            return getXxlShellLineMarkerInfo(psiElement)
        }
        return null
    }

    fun isTargetElement(psiElement: PsiElement): Boolean {
        if (psiElement !is LeafPsiElement) return false

        val file = psiElement.containingFile ?: return false
        if (file.virtualFile.extension != "sh") return false

        val project = file.project

        // 获取 Document
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return false

        // 获取元素起始偏移量
        val startOffset = psiElement.textRange.startOffset + 1

        // 获取行号（从 0 开始）
        val line = document.getLineNumber(startOffset)

        return line == 0 && startOffset == 1
    }

    fun getXxlShellLineMarkerInfo(method: PsiElement): LineMarkerInfo<PsiElement>? {
        return XxlShellLineMarkerInfo(method)
    }

}
