package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.action.KillJobAction
import com.chonmb.xxl.link.fragment.action.SelfConfigTriggerJobAction
import com.chonmb.xxl.link.fragment.action.TriggerAllInstanceJobAction
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.Icon

class TriggerShellActionGroup : DefaultActionGroup {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender, icon: Icon? = null) : super() {
        icon?.let { templatePresentation.icon = icon }
        isPopup = true
        this.render = render
        val instances = xxlService.getRegisteredInstance(globalConfig.executorName)
        if (instances != null && instances.isNotEmpty()) {
            instances.forEach { instance ->
                add(InstanceItemAction(render, instance))
            }
            templatePresentation.text = "执行策略"
        } else {
            templatePresentation.text = "执行策略(无注册实例)"
        }
    }

    class InstanceItemAction : AStarAction {
        val instance: String
        val render: XxlShellMenuRender

        constructor(render: XxlShellMenuRender, instance: String) : super(
            { instance }
        ) {
            this.instance = instance
            this.render = render
        }

        override fun showMsg(): String {
            return "目标环境：${globalConfig.activeConfig?.env}\n" +
                    "目标域名：${globalConfig.activeConfig?.domain}\n" +
                    "触发任务：${render.info.jobHandlerName}@${instance}\n" +
                    "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                    "是否确认执行?"
        }

        override fun doAction(p0: AnActionEvent) {
            xxlService.triggerJobOnce(globalConfig.executorName, render.info.jobHandlerName, address = instance)
                ?.let {
                    render.getConsole(p0.project).asyncInfo(
                        { cursor ->
                            XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(it, cursor.cursor()))
                        }
                    )
                }
//            val console=render.getInitConsole(p0.project)
//            log?.let { console.addConsoleAction(KillJobAction(it)) }
//            xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
        }
    }

}


