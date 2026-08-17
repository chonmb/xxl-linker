package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/30 10:47
 */

data class XxlObjectResponse<T>(
    val code: Int,
    val content: T? = null,
    val msg: String?=null,
)


