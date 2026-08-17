package com.chonmb.xxl.link.fragment.dialog

import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class XxlSessionConfigDetailPanel : JPanel {
    val envField: JTextField = JTextField(20)
    private val domainField = JBTextField(20)
    private val executorNameField: JTextField = JTextField(20)
    private val usernameField: JTextField = JTextField(20)
    private val passwordField: JBPasswordField = JBPasswordField()
    private val loginRememberCheckBox = JBCheckBox()
    private val activeCheckBox = JBCheckBox("active")
    private val checkAccess = JButton("检查链接")
    private val starButton = JButton("星标", jobIcons.emptyStar)

    private val checkBoxPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    fun JButton.setStarButtonIcon(value: Boolean) {
        this.icon = if (value) jobIcons.fullStar else jobIcons.emptyStar
    }

    private val formPanel: JPanel =
        FormBuilder.createFormBuilder().addLabeledComponent(JBLabel("Env:"), envField, 1, false)
            .addLabeledComponent(JBLabel("Server URL:"), domainField, 1, false)
            .addLabeledComponent(JBLabel("ExecutorName:"), executorNameField, 1, false)
            .addLabeledComponent(JBLabel("Username:"), usernameField, 1, false)
            .addLabeledComponent(JBLabel("Password:"), passwordField, 1, false)
            .addLabeledComponent(JBLabel("LoginRemember:"), loginRememberCheckBox, 1, false)
            .addComponent(checkBoxPanel)
            .addComponent(checkAccess)
            .panel
    private val emptyPanel: JLabel = JLabel("No configurations selected.", 2)
    var currentConfiguration: SessionConfig? = null
    var activeSession = globalConfig.activeEnv

    val main: XxlSessionConfigDialog

    constructor(main: XxlSessionConfigDialog) : super() {
        this.main = main
        passwordField.columns = 20
        checkBoxPanel.add(starButton)
        checkBoxPanel.add(activeCheckBox)
        envField.document.addDocumentListener(
            object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) {
                    setFieldText(e)
                }

                override fun removeUpdate(e: DocumentEvent?) {
                    setFieldText(e)
                }

                override fun changedUpdate(e: DocumentEvent?) {
                    setFieldText(e)
                }

                fun setFieldText(e: DocumentEvent?) {
                    val text = e?.document?.getText(0, e.document.length)
                    currentConfiguration?.env = text
                    val index = main.leftPanel.sessionConfigList.selectedIndex
                    if (index > -1) {
                        main.leftPanel.listModel.setElementAt(currentConfiguration, index)
                    }

                }
            }
        )
        activeCheckBox.addActionListener { e ->
            activeSession = envField.text
        }

        checkAccess.addActionListener { e ->
            if (xxlService.checkLogin(domainField.text, usernameField.text, String(passwordField.password))) {
                checkAccess.icon = jobIcons.success
            } else {
                checkAccess.icon = jobIcons.error
            }
        }

        starButton.addActionListener { e ->
            currentConfiguration?.let { config ->
                config.star = !config.star
                starButton.setStarButtonIcon(config.star)
                val index = main.leftPanel.sessionConfigList.selectedIndex
                if (index > -1) {
                    main.leftPanel.listModel.setElementAt(config, index)
                }
            }
        }

        setSessionFieldValue(null)
        setSize(300, 500)
    }

    fun setSessionFieldValue(sessionConfig: SessionConfig?) {
        removeAll()
        currentConfiguration = sessionConfig
        if (sessionConfig != null) {
            envField.text = sessionConfig.env
            domainField.text = sessionConfig.domain
            executorNameField.text = sessionConfig.executorName
            usernameField.text = sessionConfig.username
            passwordField.text = sessionConfig.password
            loginRememberCheckBox.isSelected = sessionConfig.loginRemember == true
            activeCheckBox.isSelected = sessionConfig.env == activeSession
            starButton.setStarButtonIcon(sessionConfig.star)
            add(formPanel)
        } else {
            add(emptyPanel)
        }
        checkAccess.icon = null
        revalidate();
        repaint();
    }

    fun saveSessionConfig() {
        currentConfiguration?.let {
            it.env = envField.text
            it.loginRemember = loginRememberCheckBox.isSelected
            it.domain = domainField.text
            it.password = String(passwordField.password)
            it.executorName = executorNameField.text
            it.username = usernameField.text
            it.star = starButton.icon == jobIcons.fullStar
        }
    }
}
