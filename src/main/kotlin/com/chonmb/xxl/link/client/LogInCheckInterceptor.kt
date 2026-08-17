package com.chonmb.xxl.link.client

import com.dtflys.forest.http.ForestRequest
import com.dtflys.forest.interceptor.ForestInterceptor

class LogInCheckInterceptor : ForestInterceptor {

    override fun beforeExecute(request: ForestRequest<*>?): Boolean {
        val reqDomain = request?.getVariableValue<String>("domain")
        val reqUser = request?.body?.nameValuesMap()["userName"].toString()
        val reqPassword = request?.body?.nameValuesMap()["password"].toString()
        return reqDomain?.isNotEmpty()?:false && reqUser.isNotEmpty() && reqPassword.isNotEmpty()
    }
}
