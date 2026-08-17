package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.fragment.dialog.XxlJobSettingsDialog
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction


class XxlJobSettingAction : DumbAwareAction {
    val job: JobInfoDTO?

    constructor(job: JobInfoDTO?) : super("任务设置") {
        this.job = job
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
    }

    override fun actionPerformed(e: AnActionEvent) {
        job?.let {
            XxlJobSettingsDialog(e.project, it).show()
        }
    }
}
