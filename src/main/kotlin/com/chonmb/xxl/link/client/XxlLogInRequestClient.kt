package com.chonmb.xxl.link.client

import com.alibaba.fastjson2.JSONObject
import com.dtflys.forest.annotation.BaseRequest
import com.dtflys.forest.annotation.Body
import com.dtflys.forest.annotation.Post
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponse
import com.dtflys.forest.callback.OnLoadCookie
import com.dtflys.forest.callback.OnSaveCookie
import com.dtflys.forest.http.ForestResponse
import com.chonmb.xxl.link.client.dto.XxlObjectResponse

/**
 *@author chonmb
 *@date 2026/3/17 10:10
 */

@BaseRequest(
    baseURL = "{domain}"
)
interface XxlLogInRequestClient {
    @Post("login", interceptor = [LogInCheckInterceptor::class])
    fun login(
        @Body("userName") userName: String?,
        @Body("password") password: String?,
        @Body("ifRemember") ifRemember: String,
        onSaveCookie: OnSaveCookie
    ): XxlObjectResponse<String>?;
}
