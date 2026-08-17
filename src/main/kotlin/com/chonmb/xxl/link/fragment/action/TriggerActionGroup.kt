package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.config.globalConstant
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.Icon

class TriggerActionGroup : DefaultActionGroup {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender, icon: Icon? = null) : super() {
        icon?.let { templatePresentation.icon = icon }
        isPopup = true
        this.render = render
        val instances = xxlService.getRegisteredInstance(globalConfig.executorName)
        if (instances != null && instances.isNotEmpty()) {
            instances.forEach { instance ->
                add(InstanceItemAction(render, instance))
            }
            addSeparator()
            add(TriggerAllInstanceJobAction(render))
            add(SelfConfigTriggerJobAction(render, instances))
            templatePresentation.text = "执行策略"
        } else {
            templatePresentation.text = "执行策略(无注册实例)"
        }
    }

    class InstanceItemAction : AStarAction {
        val instance: String
        val render: XxlFunctionMenuRender

        constructor(render: XxlFunctionMenuRender, instance: String) : super(
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
            xxlService
                .triggerJobOnce(globalConfig.executorName, render.info.jobHandlerName, address = instance)?.let {
                    render.getConsole(p0.project).asyncInfo(
                        { cursor ->
                            val detail = xxlService.getProcessingLogDetails(it, cursor.cursor())
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
        }
    }

}


