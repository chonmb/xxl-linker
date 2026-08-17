package com.chonmb.xxl.link.client.dto

/**
 *@author chonmb
 *@date 2026/3/16 14:20
 */

data class XxlResponse<T>(
    val recordsFiltered: Int,
    val data: List<T>,
    val recordsTotal: Int,
)
