package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.BuildGlueSourceInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.action.KillJobAction
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
import com.chonmb.xxl.link.fragment.line.freshLineMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager
import java.util.function.Consumer

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class UpdateAndTriggerShellJobAction : AStarAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("更新Shell并执行一次") {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "更新Shell脚本后执行任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        p0.project?.let { project ->
            val script =
                PsiDocumentManager.getInstance(project).getDocument(render.info.targetElement.containingFile)?.text
            if (script != null) {
                xxlService.updateGlueCodeJobAndTriggerIt(
                    globalConfig.executorName,
                    render.info.jobHandlerName,
                    script
                )?.let {
                    render.getConsole(p0.project).asyncInfo(
                        { cursor ->
                            XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(it, cursor.cursor()))
                        }
                    )
                }
//                val console = render.getInitConsole(p0.project)
//                log?.let { console.addConsoleAction(KillJobAction(it)) }
//                xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
                freshLineMarker(p0.project, render.info.element)
            }
        }
    }
}
