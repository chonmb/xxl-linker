package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.console.warning
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.execution.ui.RunContentManager
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 *@author chonmb
 *@date 2026/3/26 17:21
 */

class KillJobAction : AStarAction {
    val jobLogDTO: JobLogDTO

    constructor(jobLogDTO: JobLogDTO) : super({ "停止任务" }, jobIcons.stopIcon) {
        this.jobLogDTO = jobLogDTO
    }

    override fun showMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "调度时间：${jobLogDTO.triggerTime}\n" +
                "是否停止任务?"
    }

    override fun doAction(p0: AnActionEvent) {
        if (xxlService.killJob(this.jobLogDTO)) {
            info("任务已停止", p0.project)
        } else {
            warning("任务停止失败", p0.project)
        }
    }

//    override fun update(e: AnActionEvent) {
//        super.update(e)
//        val selectedDescriptor = e.project?.let { RunContentManager.getInstance(it).selectedContent }
//
//        // 2. 检查是否是你的插件创建的控制台会话
//
//
//        // 3. 设置按钮的可见性和启用状态
////        e.presentation.isVisible = isMyConsole
////        e.presentation.isEnabled = isMyConsole
//    }
}
