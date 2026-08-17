package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.line.freshLineMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class CreateAndTriggerJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("创建并执行一次") {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "创建并执行任务：${render.info.jobHandlerName}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        val log=xxlService.createJobAndTriggerIt(
            globalConfig.executorName,
            render.info.jobHandlerName
        )
        render.getConsole(p0.project).asyncInfo(
            { cursor ->
                XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(log, cursor.cursor()))
            }
        )
        freshLineMarker(p0.project, render.info.element)
    }
}
