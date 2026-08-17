package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.dialog.XxlSessionConfigDialog
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction


class XxlSessionConfigAction : DumbAwareAction("xxl会话配置") {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
//        e.presentation.isVisible = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = XxlSessionConfigDialog(e.project)
        if (dialog.showAndGet()) {
//            xxlService.setActiveConfig(globalConfig.activeEnv?:"")
            // 配置已保存，可以触发配置变更事件或直接使用
        }
    }
}
