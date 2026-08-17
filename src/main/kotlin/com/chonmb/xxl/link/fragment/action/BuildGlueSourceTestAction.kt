package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class BuildGlueSourceTestAction : AnAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super("创建Glue代码") {
        this.render = render
    }


    override fun actionPerformed(p0: AnActionEvent) {
        val glue=xxlService.convertGlueSource(render.info.method)
        render.getConsole(p0.project).info(
            glue.warningInfo.joinToString("\n")
        )
        render.getConsole(p0.project).info(
            glue.source
        )
    }
}
