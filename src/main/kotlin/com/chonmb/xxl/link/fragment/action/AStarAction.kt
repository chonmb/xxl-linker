package com.chonmb.xxl.link.fragment.action

import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.fragment.res.jobIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.NlsActions
import java.util.function.Supplier
import javax.swing.Icon

abstract class AStarAction : AnAction {
    constructor(icon: Icon?) : super(icon)
    constructor(text: @NlsActions.ActionText String?) : super(text)
    constructor() : super()
    constructor(dynamicText: Supplier<@NlsActions.ActionText String?>) : super(dynamicText)
    constructor(
        text: @NlsActions.ActionText Supplier<String?>,
        description: @NlsActions.ActionDescription Supplier<String?>?,
        icon: Supplier<out Icon?>?
    ) : super(text, description, icon)

    constructor(dynamicText: Supplier<@NlsActions.ActionText String?>, icon: Icon?) : super(dynamicText, icon)


    override fun actionPerformed(p0: AnActionEvent) {
        if (globalConfig.activeConfig?.star == true) {
            val msg = showMsg()
            if (Messages.showYesNoCancelDialog(
                    p0.project,
                    if (msg.isEmpty()) msg else defaultShowMsg(),
                    "星标环境",
                    jobIcons.fullStar
                ) == 0
            ) {
                doAction(p0)
            }
        } else {
            doAction(p0)
        }
    };

    abstract fun showMsg(): String
    fun defaultShowMsg(): String {
        return "目标环境：${globalConfig.activeConfig?.env}\n目标域名：${globalConfig.activeConfig?.domain}\n" +
//                "执行任务：${}\n任务描述：${}\n" +
                "是否确认执行?"
    }

    abstract fun doAction(p0: AnActionEvent)
}
