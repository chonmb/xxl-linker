package com.chonmb.xxl.link.config

import com.intellij.openapi.components.*
import com.intellij.util.application

/**
 *@author chonmb
 *@date 2026/4/16 15:12
 */

@Service
@State(
    name = "XxlLinkerPluginSettings",
    storages = [Storage("XxlLinkerPluginSettings.xml")] // 存储在 .idea 文件夹下
)
class PluginSettings : SimplePersistentStateComponent<PluginSettings.State>(State()) {
    class State : BaseState() {
        var activeEnv by string()
        var fragment by property(FragmentState())
        var debug by property(false)
        var sessions by list<SessionState>()
        var source by property(SourceState())
    }

    class SourceState : BaseState() {
        val imports by list<String>()
        val springAutowiredClazz by string("org.springframework.beans.factory.annotation.Autowired")
        init {
            imports.add("com.xxl.job.core.context.XxlJobHelper")
            imports.add("com.xxl.job.core.handler.IJobHandler")
        }
    }

    class FragmentState : BaseState() {
        val targetMethodAnnotation by string("com.xxl.job.core.handler.annotation.XxlJob")
        val targetMethodAnnotationField by string("value")
        val ignoreMethod by string("execute")
    }

    class SessionState : BaseState() {
        var star by property(false)
        var sessionCookie by map<String, String>()
        var domain by string()
        var username by string()
        var password by string()
        var loginRemember by property(true)
        var executorName by string()
        var env by string()
    }

    val domain: String
        get() = activeConfig?.domain ?: ""
    val username: String
        get() = activeConfig?.username ?: ""
    val password: String
        get() = activeConfig?.password ?: ""
    val loginRemember: Boolean
        get() = activeConfig?.loginRemember ?: true
    val executorName: String
        get() = activeConfig?.executorName ?: ""

    var debug
        get() = state.debug
        set(value) {
            state.debug = value
        }

    var activeEnv: String
        get() = state.activeEnv ?: ""
        set(value) {
            state.activeEnv = value
        }

    val source = state.source

    val activeConfig
        get() = state.sessions.find { it.env == state.activeEnv }
}


val pluginSettings by lazy { application.getService(PluginSettings::class.java)!! }
