package com.chonmb.xxl.link.fragment.line

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.CreateAndTriggerJobAction
import com.chonmb.xxl.link.fragment.action.CreateAndTriggerSourceJobAction
import com.chonmb.xxl.link.fragment.action.CreateJobAction
import com.chonmb.xxl.link.fragment.action.DeleteJobAction
import com.chonmb.xxl.link.fragment.action.EnvironmentSelectActionGroup
import com.chonmb.xxl.link.fragment.action.LatestJobLogAction
import com.chonmb.xxl.link.fragment.action.PreviewGlueJavaSourceAction
import com.chonmb.xxl.link.fragment.action.StartJobScheduleAction
import com.chonmb.xxl.link.fragment.action.StopJobScheduleAction
import com.chonmb.xxl.link.fragment.action.TriggerActionGroup
import com.chonmb.xxl.link.fragment.action.TriggerJobAction
import com.chonmb.xxl.link.fragment.action.UpdateAndTriggerSourceJobAction
import com.chonmb.xxl.link.fragment.action.UpdateSourceJobAction
import com.chonmb.xxl.link.fragment.action.XxlJobSettingAction
import com.chonmb.xxl.link.fragment.action.XxlLogActionGroup
import com.chonmb.xxl.link.fragment.action.XxlTextAction
import com.chonmb.xxl.link.fragment.console.XxlConsole
import com.chonmb.xxl.link.fragment.console.XxlConsoleHandler
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.codeInsight.daemon.LineMarkerInfo.LineMarkerGutterIconRenderer
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement


open class XxlFunctionMenuRender : LineMarkerGutterIconRenderer<PsiElement>, XxlConsoleHandler {
    var console: XxlConsole? = null;
    val info: XxlFunctionLineMarkerInfo
    var jobInfo: JobInfoDTO? = null
    var linked: Boolean? = null

    constructor(info: XxlFunctionLineMarkerInfo) : super(info) {
        this.info = info
    }

    fun init() {
        try {
            jobInfo = xxlService.getJob(globalConfig.executorName, info.jobHandlerName)
            linked = true
        } catch (_: Exception) {
            linked = false
            jobInfo = null
        }
        if (info.icon != jobIcons.getLineMarkerIcon(info.jobHandlerName)) {
            freshLineMarker(info.method.project, info.element)
        }
    }

    override fun getConsole(project: Project?): XxlConsole {
        if (console == null) {
            project?.let { console = XxlConsole(it, info.jobHandlerName) }
        }
        return console!!
    }

    private fun getBeanFunctionMenu(): ActionGroup? {
        return XxlActionGroupBuilder()
            .menu(TriggerJobAction(this))
            .menu(TriggerActionGroup(this))
            .separator()
            .menu(XxlLogActionGroup(jobInfo, this))
            .separator { jobInfo?.scheduleType == "CRON" }
            .menu(StartJobScheduleAction(this)) { jobInfo?.scheduleType == "CRON" && jobInfo?.triggerStatus == 0 }
            .menu(StopJobScheduleAction(this)) { jobInfo?.scheduleType == "CRON" && jobInfo?.triggerStatus != 0 }
            .separator()
            .menu(XxlJobSettingAction(jobInfo))
            .menu(DeleteJobAction(this))
            .separator()
            .menu(EnvironmentSelectActionGroup())
            .build()
    }

    private fun getJavaSourceFunctionMenu(): ActionGroup? {
        return XxlActionGroupBuilder()
            .menu(UpdateAndTriggerSourceJobAction(this))
            .menu(TriggerJobAction(this))
            .menu(TriggerActionGroup(this))
            .separator()
            .menu(XxlLogActionGroup(jobInfo, this))
            .separator { jobInfo?.scheduleType == "CRON" }
            .menu(StartJobScheduleAction(this)) { jobInfo?.scheduleType == "CRON" && jobInfo?.triggerStatus == 0 }
            .menu(StopJobScheduleAction(this)) { jobInfo?.scheduleType == "CRON" && jobInfo?.triggerStatus != 0 }
            .separator()
            .menu(UpdateSourceJobAction(this))
            .menu(PreviewGlueJavaSourceAction(this))
            .menu(XxlJobSettingAction(jobInfo))
            .menu(DeleteJobAction(this))
            .separator()
            .menu(EnvironmentSelectActionGroup())
            .build()
    }

    private fun getErrorLinked(): ActionGroup? {
        return XxlActionGroupBuilder()
            .menu(XxlTextAction("请检查域名，账号，密码或应用名称是否正确"))
            .menu(EnvironmentSelectActionGroup(icon = jobIcons.warning))
            .build()
    }

    private fun getCreateFunctionMenu(): ActionGroup? {
        return XxlActionGroupBuilder()
            .menu(CreateJobAction(this))
            .menu(CreateAndTriggerJobAction(this))
            .menu(CreateAndTriggerSourceJobAction(this))
            .separator()
            .menu(EnvironmentSelectActionGroup())
            .build()
    }

    override fun getPopupMenuActions(): ActionGroup? {
        init()
        if (linked == false) {
            return getErrorLinked();
        }
        return if (jobInfo == null) {
            getCreateFunctionMenu()
        } else if (jobInfo?.glueType.equals("GLUE_GROOVY")) {
            getJavaSourceFunctionMenu()
        } else {
            getBeanFunctionMenu()
        }
    }


}
