package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
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

class PreviewShellSourceAction : AnAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("查看远程脚本") {
        this.render = render
    }


    override fun actionPerformed(p0: AnActionEvent) {
        render.getConsole(p0.project).info(
            xxlService.getGlueSource(
                globalConfig.executorName,
                render.info.jobHandlerName
            )
        )
    }
}
