package com.chonmb.xxl.link.fragment.dialog

import com.chonmb.xxl.link.config.PluginSettings
import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.line.freshEditFileMarker
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.awt.BorderLayout
import javax.swing.*


/**
 *@author chonmb
 *@date 2026/4/1 09:29
 */

class XxlSessionConfigDialog : DialogWrapper {

    val sessionConfigs: MutableList<SessionConfig?>? = globalConfig.state.sessions.map {
        SessionConfig(
            it.domain ?: "",
            it.username ?: "",
            it.password ?: "",
            it.loginRemember,
            it.executorName ?: "",
            it.env,
            it.star
        )
    }.toMutableList()

    val leftPanel: XxlSessionConfigListPanel = XxlSessionConfigListPanel(this)
    val rightPanel: XxlSessionConfigDetailPanel = XxlSessionConfigDetailPanel(this)

    val project: Project?

    constructor(project: Project?) : super(project) {
        title = "Xxl会话配置"
        this.project = project

        init()
        setSize(700, 500)
    }


    override fun createCenterPanel(): JComponent? {
        val main = JPanel(BorderLayout())
        main.add(leftPanel, BorderLayout.WEST)
        main.add(rightPanel, BorderLayout.CENTER)
        return main
    }

    fun List<String>.findDuplicates(): List<String> =
        groupBy { it }                     // 按字符串分组，得到 Map<String, List<String>>
            .filter { it.value.size > 1 }  // 保留出现次数大于 1 的项
            .keys                          // 取出字符串本身
            .toList()

    override fun doValidate(): ValidationInfo? {
        val allConfig = ArrayList<String>()
        for (item in leftPanel.listModel.toArray()) {
            (item as SessionConfig).env?.let { allConfig.add(it) }
        }
        val duplicates = allConfig.findDuplicates()
        if (duplicates.isNotEmpty()) {
            val info = ValidationInfo("duplicate configuration " + duplicates.toString())
            info.okEnabled = false
            info.warning = true
            return info
        }
        return null
    }

    override fun doOKAction() {
        super.doOKAction()
        rightPanel.saveSessionConfig()
        val activeSession = rightPanel.activeSession

        val sessionList = leftPanel.listModel.toArray().filter { it is SessionConfig }.map {
            PluginSettings.SessionState().apply {
                domain = (it as SessionConfig).domain
                username = it.username
                password = it.password
                loginRemember = it.loginRemember ?: true
                executorName = it.executorName
                env = it.env
                star = it.star
            }
        }.toMutableList()

        globalConfig.state.sessions = sessionList
        activeSession.let { xxlService.setActiveConfig(it) }
        freshEditFileMarker(project)
    }

}
