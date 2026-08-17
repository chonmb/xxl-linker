package com.chonmb.xxl.link.client

import com.dtflys.forest.Forest
import com.dtflys.forest.exceptions.ForestRuntimeException
import com.dtflys.forest.http.ForestCookie
import com.dtflys.forest.http.ForestCookies
import com.dtflys.forest.http.ForestRequest
import com.dtflys.forest.http.ForestResponse
import com.dtflys.forest.interceptor.ForestInterceptor
import com.chonmb.xxl.link.config.globalConfig

/**
 *@author chonmb
 *@date 2026/3/16 08:49
 */

class DefaultXxlRequestInterceptor : ForestInterceptor {

    private val xxlCookies: MutableList<ForestCookie> = mutableListOf();
    private val logInClient: XxlLogInRequestClient = Forest.client(XxlLogInRequestClient::class.java)


    override fun onError(ex: ForestRuntimeException?, request: ForestRequest<*>?, response: ForestResponse<*>?) {
        if (response?.statusCode!! in 400..<500) {
            resetSessionCookie()
        }
        super.onError(ex, request, response)
    }

    fun resetSessionCookie(){
        xxlCookies.clear()
        globalConfig.activeConfig?.sessionCookie?.clear()
    }


    fun initSessionCookie() {
        if (globalConfig.activeConfig?.sessionCookie?.isNotEmpty() == true) {
            xxlCookies.clear()
            globalConfig.activeConfig.let { cookie ->
                cookie?.sessionCookie?.map { ForestCookie(it.key, it.value) }?.toList()?.let { xxlCookies.addAll(it) }
            }
        }
    }

    fun updateSessionCookies() {
        xxlCookies.forEach { globalConfig.activeConfig?.sessionCookie[it.name] = it.value }
    }

    override fun onLoadCookie(
        request: ForestRequest<*>?,
        cookies: ForestCookies?
    ) {
        if (xxlCookies.isEmpty() && globalConfig.activeConfig?.loginRemember == true && globalConfig.activeConfig?.sessionCookie?.isNotEmpty() == true) {
            initSessionCookie()
        }

        if (xxlCookies.isEmpty() || Forest.config().getVariableValue("freshConfig") == true) {
            resetSessionCookie()
            Forest.config().removeVariable("freshConfig")
            val response = logInClient.login(
                globalConfig.username,
                globalConfig.password,
                if (globalConfig.loginRemember) "on" else ""
            ) { _, cookies ->
                if (cookies != null && cookies.allCookies().isNotEmpty()) {
                    xxlCookies.addAll(cookies.allCookies())
                    updateSessionCookies()
                }
            }
            if (response == null || response.code != 200) {
                throw RuntimeException("login failed, code: ${response?.code}, msg: ${response?.msg}")
            }
        }
        cookies?.addAllCookies(this.xxlCookies)
    }


    override fun onRetry(
        request: ForestRequest<*>?,
        response: ForestResponse<*>?
    ) {
        resetSessionCookie()
    }
}
