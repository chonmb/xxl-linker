package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.fragment.console.AsyncConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.AsyncConsoleOutputCursor
import com.chonmb.xxl.link.fragment.console.XxlConsoleHandler
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.util.NlsActions
import java.util.function.Function
import java.util.function.Supplier
import javax.swing.Icon

/**
 *@author chonmb
 *@date 2026/4/9 15:21
 */

class XxlLogActionGroup : DefaultActionGroup {
    val job: JobInfoDTO?
    val consoleHandler: XxlConsoleHandler

    constructor(job: JobInfoDTO?, consoleHandler: XxlConsoleHandler) : super() {
        this.job = job
        this.consoleHandler = consoleHandler
        templatePresentation.text = "历史执行日志"
        isPopup = true
        if (job != null) {
            initMenu(xxlService.getJobLogs(job))
        }
    }

    fun initMenu(items: List<JobLogDTO>) {
        for (item in items) {
            val icon = getLogIcon(item)
            if (icon === jobIcons.processing) {
                add(XxlProcessingLogItemAction(item.triggerTime ?: "unknown_time", this, item))
            } else {
                add(XxlLogItemAction({ item.triggerTime }, icon, this, item))
            }

        }
    }

    fun getLogIcon(log: JobLogDTO): Icon {
        if (log.triggerCode == 200 && log.handleCode == 200) {
            return jobIcons.success
        }
        if (log.triggerCode == 200 && log.handleCode == 0) {
            return jobIcons.processing
        }
        return jobIcons.error
    }

    class XxlLogItemAction : AnAction {
        val actionGroup: XxlLogActionGroup
        val jobLogDTO: JobLogDTO
        val processing: Boolean

        constructor(
            dynamicText: Supplier<@NlsActions.ActionText String?>,
            icon: Icon?,
            group: XxlLogActionGroup,
            log: JobLogDTO
        ) : super(dynamicText, icon) {
            this.actionGroup = group
            this.jobLogDTO = log
            this.processing = icon == jobIcons.processing
        }

        override fun actionPerformed(p0: AnActionEvent) {
            if (processing) {
                actionGroup.consoleHandler.getConsole(p0.project)
                    .asyncInfo(
                        { cursor ->
                            XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(jobLogDTO, cursor.cursor()))
                        }
                    )
            } else {
                actionGroup.consoleHandler.getConsole(p0.project).info(xxlService.getJobLogDetails(this.jobLogDTO))
            }
        }


    }

    class XxlProcessingLogItemAction : DefaultActionGroup {
        val actionGroup: XxlLogActionGroup
        val log: JobLogDTO

        constructor(text: String, group: XxlLogActionGroup, log: JobLogDTO) : super() {
            templatePresentation.text = text
            templatePresentation.icon = jobIcons.processing
            isPopup = true
            this.actionGroup = group
            this.log = log
            add(XxlLogItemAction({ "查看实时日志" }, jobIcons.processing, group, log))
            add(KillJobAction(log))
        }

        override fun actionPerformed(e: AnActionEvent) {
            super.actionPerformed(e)
            actionGroup.consoleHandler.getConsole(e.project)
                .asyncInfo(
                    { cursor ->
                        XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(log, cursor.cursor()))
                    }
                )
        }
    }
}
