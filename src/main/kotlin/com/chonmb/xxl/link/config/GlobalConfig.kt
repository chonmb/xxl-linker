package com.chonmb.xxl.link.config


/**
 *@author chonmb
 *@date 2026/3/17 11:13
 */


data class SessionConfig(
    var domain: String,
    var username: String,
    var password: String,
    var loginRemember: Boolean? = true,
    var executorName: String,
    var env: String?,
    var star:Boolean = false,
)

val globalConfig by lazy { pluginSettings }

val fragmentConfig by lazy { pluginSettings.state.fragment }

