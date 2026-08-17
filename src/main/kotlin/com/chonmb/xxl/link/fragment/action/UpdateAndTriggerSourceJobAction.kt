package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.BuildGlueSourceInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.config.globalConstant
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class UpdateAndTriggerSourceJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("更新Glue并执行一次") {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "更新Glue代码后执行任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        val glue = xxlService.convertGlueSource(render.info.method)
        if (glue.success) {
            val log = xxlService.updateGlueCodeJobAndTriggerIt(
                globalConfig.executorName,
                render.info.jobHandlerName,
                glue.source
            )
            log?.let {
                render.getConsole(p0.project)
                    .asyncInfo(
                        { cursor ->
                            val detail=xxlService.getProcessingLogDetails(it, cursor.cursor())
                            if (detail != null
                                && detail.logContent.isNotEmpty()
                                && detail.logContent.contains(globalConstant.rpcErrorMsg)
                                && xxlService.isOverLongGlueSource(globalConfig.executorName, render.info.jobHandlerName)
                            ) {
                                warning("源码上下文超长，请调整后再尝试", p0.project)
                            }
                            XxlJobConsoleOutputContext(detail)
                        }
                    )
            }
//            val console = render.getInitConsole(p0.project)
//            log?.let { console.addConsoleAction(KillJobAction(it)) }
//            xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
        } else {

            NotificationGroupManager.getInstance().getNotificationGroup("xxl.notification")
                .createNotification(
                    "无法更新Glue代码",
                    "warnings:\n" + glue.warningInfo.joinToString("\n"),
                    NotificationType.ERROR
                )
                .notify(p0.project)
        }
    }
}
