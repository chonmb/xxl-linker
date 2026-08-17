package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.dialog.SelfConfigTriggerDialog
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.NlsActions
import java.util.function.Supplier

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class SelfConfigTriggerJobAction : AnAction {
    val render: XxlFunctionMenuRender
    val instances : List<String>

    constructor(render: XxlFunctionMenuRender,instances:List<String>) : super({ "自定义执行参数" }) {
        this.render = render
        this.instances = instances
    }


    override fun actionPerformed(p0: AnActionEvent) {
        val dialog= SelfConfigTriggerDialog(p0.project,render,instances)
        dialog.showAndGet()

    }
}
