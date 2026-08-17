package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.BuildGlueSourceInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.error
import com.chonmb.xxl.link.fragment.console.info
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

class UpdateSourceJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("更新Glue代码") {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "更新Glue代码：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        val glue = xxlService.convertGlueSource(render.info.method)
        if (glue.success) {
            xxlService.updateGlueJobSource(
                globalConfig.executorName,
                render.info.jobHandlerName,
                glue.source
            )
            info("成功创建任务${render.info.jobHandlerName}", p0.project)
        } else {
            error(
                "创建任务失败${render.info.jobHandlerName}\n" +
                        "warnings:\n${glue.warningInfo.joinToString("\n")}",
                p0.project
            )
        }
    }
}
