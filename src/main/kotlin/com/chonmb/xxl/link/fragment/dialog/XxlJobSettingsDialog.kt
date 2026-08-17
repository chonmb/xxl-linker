package com.chonmb.xxl.link.fragment.dialog

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.util.ui.FormBuilder
import org.jsoup.internal.StringUtil
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 *@author chonmb
 *@date 2026/4/9 16:01
 */

class XxlJobSettingsDialog : DialogWrapper {

    val job: JobInfoDTO

    val remark = JTextField(20)
    val scheduleTypeMap = mapOf("无" to "NONE", "CRON" to "CRON")
    val scheduleType = ComboBox<String>(scheduleTypeMap.keys.toTypedArray())
    val routeStrategyMap = mapOf("第一个" to "FIRST")
    val routeStrategy = ComboBox(routeStrategyMap.keys.toTypedArray())
    val blockStrategyMap = mapOf("单机串行" to "SERIAL_EXECUTION")
    val blockStrategy = ComboBox(blockStrategyMap.keys.toTypedArray())
    val scheduleConfigPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    val scheduleConfig = JTextField(20)
    val project: Project?

    constructor(project: Project?, job: JobInfoDTO) : super(project) {
        title = job.executorHandler + "任务设置"
        this.job = job
        this.project = project

        scheduleType.addActionListener { e ->
            when (scheduleType.selectedItem) {
                "CRON" -> {
                    scheduleConfigPanel.removeAll()
                    scheduleConfigPanel.add(JLabel("CRON"))
                    scheduleConfigPanel.add(scheduleConfig)
                    scheduleConfigPanel.isVisible = true
                    scheduleConfigPanel.revalidate()
                    scheduleConfigPanel.repaint()
                }

                else -> scheduleConfigPanel.isVisible = false
            }
        }

        remark.text = job.jobDesc
        if (!StringUtil.isBlank(job.scheduleConf)) {
            scheduleConfig.text = job.scheduleConf
        } else {
            scheduleConfig.text = "0 0 0 * * ?"
        }
        blockStrategy.selectedItem = job.executorBlockStrategy

        if (job.scheduleType == "CRON") {
            scheduleConfigPanel.add(JLabel("CRON"))
            scheduleConfigPanel.add(scheduleConfig)
        } else {
            scheduleConfigPanel.isVisible = false
        }
        scheduleType.selectedItem = job.scheduleType

        init()
        setSize(400, 500)
    }

    override fun createCenterPanel(): JComponent? {
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JLabel("任务描述"), remark)
            .addSeparator()
            .addLabeledComponent(JLabel("调度类型"), scheduleType)
            .addComponent(scheduleConfigPanel)
            .addSeparator()
            .addLabeledComponent(JLabel("路由策略"), routeStrategy)
            .addLabeledComponent(JLabel("阻塞处理策略"), blockStrategy)
            .panel
    }

    fun updateJob(): JobInfoDTO {
        job.jobDesc = remark.text
        scheduleTypeMap[scheduleType.selectedItem as String]?.let { job.scheduleType = it }
        if ("无" == scheduleType.selectedItem) {
            job.scheduleConf = ""
        } else {
            job.scheduleConf = scheduleConfig.text
        }
        blockStrategyMap[blockStrategy.selectedItem as String]?.let { job.executorBlockStrategy = it }
        return job
    }

    override fun doOKAction() {
        super.doOKAction()
        val updated = updateJob()
        if (!xxlService.updateJob(updated)) {
            info("配置失败", project)
        } else {
            info("配置成功", project)
        }
    }

    override fun doValidate(): ValidationInfo? {
        return super.doValidate()
    }
}
