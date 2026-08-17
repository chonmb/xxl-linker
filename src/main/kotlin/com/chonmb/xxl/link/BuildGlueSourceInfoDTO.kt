package com.chonmb.xxl.link

data class BuildGlueSourceInfoDTO(
    val source: String,
    val warningInfo:List<String>,
    val success:Boolean,
)
