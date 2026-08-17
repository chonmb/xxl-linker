package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/17 09:15
 */

data class JobLogDTO(
    val id: Int,
    var triggerCode: Int,
    var handleCode: Int,
    var handleMsg: String?,
    val jobId: Int,
    val jobGroup: Int,
    val executorHandler: String?,
    var executorAddress: String?,
    var triggerTime: String?,
    var triggerMsg: String?,
    var alarmStatus: Int
)
