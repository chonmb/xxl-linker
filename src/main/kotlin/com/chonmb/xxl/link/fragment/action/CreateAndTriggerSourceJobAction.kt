package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.config.globalConstant
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.line.freshLineMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class CreateAndTriggerSourceJobAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("创建Glue并执行一次") {
        this.render = render
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "创建并执行Glue任务：${render.info.jobHandlerName}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        xxlService.createGlueCodeJobAndTriggerIt(
            globalConfig.executorName,
            render.info
        )?.let {
            render.getConsole(p0.project).asyncInfo(
                { cursor ->
                    val detail = xxlService.getProcessingLogDetails(it, cursor.cursor());
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
//            console.addConsoleAction(KillJobAction(log))
//            xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
            freshLineMarker(p0.project, render.info.element)
        }
    }
}
