package com.chonmb.xxl.link.config


data class Constant(
    val rpcErrorMsg: String = "xxl-rpc remoting error(Read timed out)",
    val warningGlueSourceLength: Int = 200000
)

val globalConstant by lazy { Constant() }
