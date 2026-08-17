package com.chonmb.xxl.link.fragment.dialog

import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.action.KillJobAction
import com.chonmb.xxl.link.fragment.console.XxlJobConsoleOutputContext
import com.chonmb.xxl.link.fragment.console.info
import com.chonmb.xxl.link.fragment.line.XxlFunctionMenuRender
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.JBColor
import com.intellij.util.ui.FormBuilder
import java.awt.Color
import javax.swing.InputVerifier
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField

/**
 *@author chonmb
 *@date 2026/4/9 16:01
 */

class SelfConfigTriggerDialog : DialogWrapper {

    val instances: List<String>

    val instancesCombo: ComboBox<String>
    val params = JTextField(20)
    val render: XxlFunctionMenuRender

    val project: Project?

    constructor(project: Project?, render: XxlFunctionMenuRender, instances: List<String>) : super(project) {
        title = render.info.jobHandlerName + "执行参数设置"
        this.instances = instances
        this.project = project
        this.render = render
        this.instancesCombo = ComboBox<String>(instances.toTypedArray())
        if (instances.isNotEmpty()) {
            instancesCombo.selectedItem = instances[0]
        }

        init()
        setSize(400, 300)
    }

    override fun createCenterPanel(): JComponent? {
        return FormBuilder.createFormBuilder()
            .addComponent(JLabel(getNoticeMsg()))
            .addLabeledComponent(JLabel("执行实例"), instancesCombo)
            .addLabeledComponent(JLabel("执行参数"), params)
            .panel
    }

    fun getNoticeMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n" +
                "目标域名：${globalConfig.activeConfig?.domain}\n" +
                "执行任务：${render.info.jobHandlerName}\n" +
                "任务描述：${xxlService.jobs[render.info.jobHandlerName]?.jobDesc}\n"
    }

    override fun doOKAction() {
        super.doOKAction()
        xxlService.triggerJobOnce(
            globalConfig.executorName,
            render.info.jobHandlerName,
            params.text,
            instancesCombo.selectedItem as String
        )?.let {
            render.getConsole(project).asyncInfo(
                { cursor ->
                    XxlJobConsoleOutputContext(xxlService.getProcessingLogDetails(it, cursor.cursor()))
                }
            )
        }
//        val console = render.getInitConsole(project)
//        log?.let { console.addConsoleAction(KillJobAction(it)) }
//        xxlService.getProcessedJobLogDetails(log) { console.processInfo(it) }
    }

    override fun doValidate(): ValidationInfo? {
        return super.doValidate()
    }
}
