package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.PluginSettings
import com.chonmb.xxl.link.config.SessionConfig
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.line.freshEditFileMarker
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.chonmb.xxl.link.xxlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.Icon

class EnvironmentSelectActionGroup : DefaultActionGroup {

    constructor(icon: Icon? = null) : super() {
        templatePresentation.text = "当前环境：" + globalConfig.activeEnv
        icon?.let { templatePresentation.icon = icon }
        if (globalConfig.activeConfig?.star == true && templatePresentation.icon ==null) {
            templatePresentation.icon = jobIcons.fullStar
        }
        isPopup = true
        globalConfig.state.sessions.let {
            if (it.isNotEmpty()) {
                for (session in it) {
                    add(EnvironmentSelectItemAction(session!!))
                }
                addSeparator()
            }
        }
        add(XxlSessionConfigAction())
    }

    class EnvironmentSelectItemAction : AnAction {
        val env: String?

        constructor(session: PluginSettings.SessionState) : super(
            { session.env },
            if (globalConfig.activeEnv == session.env) jobIcons.success else null
        ) {
            this.env = session.env
            this.templatePresentation.description =
                "域名：${session.domain}\n执行器：${session.executorName}"
        }

        override fun actionPerformed(e: AnActionEvent) {
            env?.let { env -> xxlService.setActiveConfig(env) }
            freshEditFileMarker(e.project)
        }
    }

}


