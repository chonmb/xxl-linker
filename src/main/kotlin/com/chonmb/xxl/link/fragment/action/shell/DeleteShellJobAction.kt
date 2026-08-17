package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.console.error
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
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

class DeleteShellJobAction : AStarAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("删除任务") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "删除任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        if (xxlService.removeJob(
                globalConfig.executorName,
                render.info.jobHandlerName
            )
        ){
            info("删除成功",p0.project)
            freshLineMarker(p0.project, render.info.element)
        }else{
            error("删除失败",p0.project)
        }
    }
}
