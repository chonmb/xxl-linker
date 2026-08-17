package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/16 12:47
 */

data class JobInfoDTO (
    val id: Int,
    var jobDesc: String,
    val jobGroup: Int,
    var scheduleType:String,
    var scheduleConf:String,
    val triggerStatus: Int,
    val executorHandler:String,
    val author:String,
    val glueType:String,
    val executorRouteStrategy:String,
    val misfireStrategy:String,
    var executorBlockStrategy:String,
    val executorTimeout:Int,
    val executorFailRetryCount:Int,
    val glueSource:String?,
    val glueRemark: String?
)
