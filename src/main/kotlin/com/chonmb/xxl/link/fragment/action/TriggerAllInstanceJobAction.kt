package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.config.globalConstant
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.NlsActions

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class TriggerAllInstanceJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("全部实例执行一次") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "触发所有实例任务：${render.info.jobHandlerName}@all\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        xxlService.triggerJobWithAllInstance(
            globalConfig.executorName,
            render.info.jobHandlerName
        )?.let {
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

//        val console = render.getInitConsole(p0.project)
//        log?.let { console.addConsoleAction(KillJobAction(it)) }
//        xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
    }
}
