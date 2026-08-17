package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class StopJobScheduleAction : AStarAction {
    val render: XxlFunctionMenuRender

    constructor(render: XxlFunctionMenuRender) : super({ "结束任务" }, jobIcons.stopIcon) {
        this.render = render
    }


    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "停止任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n" +
                "调度信息：${xxlService.jobs[render.info.jobHandlerName]?.scheduleConf}\n" +
                "是否确认执行?"
    }

    override fun doAction(p0: AnActionEvent) {
        xxlService.stopJob(
            globalConfig.executorName,
            render.info.jobHandlerName
        )
        info("任务已停止",p0.project)
    }
}
