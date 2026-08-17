package com.chonmb.xxl.link.fragment.line

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.EnvironmentSelectActionGroup
import com.chonmb.xxl.link.fragment.action.XxlJobSettingAction
import com.chonmb.xxl.link.fragment.action.XxlLogActionGroup
import com.chonmb.xxl.link.fragment.action.XxlTextAction
import com.chonmb.xxl.link.fragment.action.shell.CreateShellJobAction
import com.chonmb.xxl.link.fragment.action.shell.DeleteShellJobAction
import com.chonmb.xxl.link.fragment.action.shell.LatestShellJobLogAction
import com.chonmb.xxl.link.fragment.action.shell.PreviewShellSourceAction
import com.chonmb.xxl.link.fragment.action.shell.TriggerShellActionGroup
import com.chonmb.xxl.link.fragment.action.shell.TriggerShellJobAction
import com.chonmb.xxl.link.fragment.action.shell.CreateAndTriggerShellJobAction
import com.chonmb.xxl.link.fragment.action.shell.UpdateAndTriggerShellJobAction
import com.chonmb.xxl.link.fragment.action.shell.UpdateShellJobAction
import com.chonmb.xxl.link.fragment.console.XxlConsole
import com.chonmb.xxl.link.fragment.console.XxlConsoleHandler
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.codeInsight.daemon.LineMarkerInfo.LineMarkerGutterIconRenderer
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement


class XxlShellMenuRender : LineMarkerGutterIconRenderer<PsiElement>, XxlConsoleHandler {
    var console: XxlConsole? = null;
    val info: XxlShellLineMarkerInfo

    constructor(info: XxlShellLineMarkerInfo) : super(info) {
        this.info = info
    }

    override fun getConsole(project: Project?): XxlConsole {
        if (console == null) {
            project?.let { console = XxlConsole(it, info.jobHandlerName) }
        }
        return console!!
    }

    fun isShellJob(job: JobInfoDTO?): Boolean {
        return job?.glueType == "GLUE_SHELL"
    }

    override fun getPopupMenuActions(): ActionGroup? {
        val groupBuilder = XxlActionGroupBuilder()
        val existJob = runCatching {
            xxlService.existJob(globalConfig.executorName, info.jobHandlerName)
        }.getOrDefault(null)
        val job = if (existJob == true) xxlService.jobs[info.jobHandlerName] else null

        groupBuilder
            .menu(UpdateAndTriggerShellJobAction(this)) { isShellJob(job) }
            .menu(TriggerShellJobAction(this)) { isShellJob(job) }
            .menu({ TriggerShellActionGroup(this) }) { isShellJob(job) }
            .menu(CreateAndTriggerShellJobAction(this)) { existJob == false }
            .menu(CreateShellJobAction(this)) { existJob == false }
            .separator() { existJob == true }
            .menu(UpdateShellJobAction(this)) { existJob == true }
            .menu(PreviewShellSourceAction(this)) { isShellJob(job) }
            .menu(XxlLogActionGroup(job, this)) { job != null }
            .separator() { existJob != null }
            .menu(XxlJobSettingAction(job)) { job != null }
            .menu(DeleteShellJobAction(this)) { existJob == true }
            .separator() { existJob != null }
            .menu(EnvironmentSelectActionGroup()) { existJob != null }
            .menu(XxlTextAction("请检查域名，账号，密码或应用名称是否正确")) { existJob == null }
            .menu(EnvironmentSelectActionGroup(icon = jobIcons.warning)) { existJob == null }

        if (info.icon != jobIcons.getLineMarkerIcon(info.jobHandlerName)) {
            freshLineMarker(info.targetElement.project, info.targetElement)
        }

        return groupBuilder.build()
    }


}
