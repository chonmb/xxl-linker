package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.dialog.XxlJobSettingsDialog
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.line.freshLineMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class CreateJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("创建任务") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "创建任务：${render.info.jobHandlerName}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        xxlService.createJob(
            globalConfig.executorName,
            render.info.jobHandlerName
        )
        info("创建成功", p0.project)
        if (xxlService.existJob(globalConfig.executorName, render.info.jobHandlerName)) {
            xxlService.jobs[render.info.jobHandlerName]?.let {
                XxlJobSettingsDialog(p0.project, it).show()
            }
        }
        freshLineMarker(p0.project, render.info.element)
    }
}
