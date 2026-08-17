package com.chonmb.xxl.link.fragment.dialog

import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.intellij.platform.eel.provider.utils.copy
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.ListSelectionEvent

class XxlSessionConfigListPanel : JPanel {

    val sessionConfigList: JBList<SessionConfig>
    val listModel: DefaultListModel<SessionConfig>

    val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    val addButton = JButton("新增",jobIcons.addIcon)
    val deleteButton = JButton("删除",jobIcons.removeIcon)
    val copyButton = JButton("复制",jobIcons.fileIcon)

    val main: XxlSessionConfigDialog


    constructor(main: XxlSessionConfigDialog) : super(BorderLayout()) {
        this.main = main
        this.listModel = DefaultListModel<SessionConfig>()
        for (config in main.sessionConfigs!!) {
            if (config != null) {
                this.listModel.addElement(config)
            }
        }
        this.sessionConfigList = JBList<SessionConfig>(listModel)
        sessionConfigList.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component? {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (value is SessionConfig) {
                    setText(value.env)
                    icon = if (value.star) jobIcons.fullStar else jobIcons.emptyStar
                }
                return this
            }
        }
        sessionConfigList.addListSelectionListener { e -> onSelectedValue(e) }


        add(JBScrollPane(sessionConfigList), BorderLayout.CENTER)


        addButton.addActionListener { addSessionConfig() }
        deleteButton.addActionListener { removeSessionConfig() }
        copyButton.addActionListener { copySessionConfig() }
        buttonPanel.add(addButton)
        buttonPanel.add(copyButton)
        buttonPanel.add(deleteButton)
        add(buttonPanel, BorderLayout.NORTH)
    }

    private fun copySessionConfig() {
        val config = sessionConfigList.selectedValue
        if (config != null) {
            addSessionConfig(SessionConfig(
                config.domain,
                config.username,
                config.password,
                config.loginRemember,
                config.executorName,
                config.env+"-copied"
            ))
        }
    }

    fun onSelectedValue(e: ListSelectionEvent) {
        if (!e.valueIsAdjusting) {
            main.rightPanel.saveSessionConfig()
            main.rightPanel.setSessionFieldValue(sessionConfigList.selectedValue)
        }
    }

    fun addSessionConfig(config: SessionConfig? = null) {
        val newConfig = config ?: SessionConfig(
            "",
            "",
            "",
            true,
            "",
            "new-config-" + listModel.size
        )
        listModel.addElement(newConfig)
        sessionConfigList.setSelectedValue(newConfig, true)
    }

    fun removeSessionConfig() {
        listModel.removeElement(sessionConfigList.getSelectedValue())
        if (!listModel.isEmpty) {
            sessionConfigList.selectedIndex = 0
        }
    }

}
