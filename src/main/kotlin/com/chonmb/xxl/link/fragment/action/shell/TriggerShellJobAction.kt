package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.action.KillJobAction
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class TriggerShellJobAction : AStarAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("执行一次") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "触发任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        xxlService.triggerJobOnce(
            globalConfig.executorName,
            render.info.jobHandlerName
        )?.let {
            render.getConsole(p0.project).asyncInfo(
                { cursor ->
                    XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(it, cursor.cursor()))
                }
            )
        }
//        val console=render.getInitConsole(p0.project)
//        log?.let { console.addConsoleAction(KillJobAction(it)) }
//        xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
    }


}
