package com.chonmb.xxl.link.fragment.console

/**
 *@author chonmb Email:weichonmb@foxmail.com
 *@date 2026/6/15 10:41
 */

interface AsyncConsoleOutputContext : AsyncConsoleOutputCursor {
    fun stop(): Boolean
    fun message(): String
}
