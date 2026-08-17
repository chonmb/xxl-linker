package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
import com.chonmb.xxl.link.fragment.line.freshLineMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class CreateShellJobAction : AStarAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("创建脚本任务") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "创建任务：${render.info.jobHandlerName}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        p0.project?.let { project ->
            val script =
                PsiDocumentManager.getInstance(project).getDocument(render.info.targetElement.containingFile)?.text
            if (script != null) {
                xxlService.createJob(
                    globalConfig.executorName,
                    render.info.jobHandlerName,
                    "GLUE_SHELL",
                    script
                )
                info("创建成功", p0.project)
                freshLineMarker(p0.project, render.info.element)
            }else{
                warning("创建失败", p0.project)
            }
        }
    }
}
