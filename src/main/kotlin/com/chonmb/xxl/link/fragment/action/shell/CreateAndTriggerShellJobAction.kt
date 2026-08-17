package com.chonmb.xxl.link.fragment.action.shell

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.AStarAction
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.line.XxlShellMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class CreateAndTriggerShellJobAction : AStarAction {
    val render: XxlShellMenuRender

    constructor(render: XxlShellMenuRender) : super("创建脚本并执行一次") {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "创建Glue脚本后执行任务：${render.info.jobHandlerName}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        p0.project?.let { project ->
            val script =
                PsiDocumentManager.getInstance(project).getDocument(render.info.targetElement.containingFile)?.text
            if (script != null) {
                xxlService.createShellJobAndTriggerIt(
                    globalConfig.executorName,
                    render.info.jobHandlerName,
                    script
                )?.let {
                    render.getConsole(project).asyncInfo(
                        { cursor ->
                            XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(it, cursor.cursor()))
                        }
                    )
                }
//                val console=render.getInitConsole(p0.project)
//                xxlService.getProcessedJobLogDetails(log){console.processInfo(it)}
            }
        }
    }
}
