package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/17 09:15
 */


data class JobLogDetailDTO(
    val end: Boolean,
    val fromLineNum: Int,
    val logContent: String,
    val toLineNum: Int,
)

