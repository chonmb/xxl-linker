package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/16 12:47
 */

data class JobGroupDTO(
    val id: Int,
    val title: String,
    val appname: String,
    val registryList: List<String>?
)
