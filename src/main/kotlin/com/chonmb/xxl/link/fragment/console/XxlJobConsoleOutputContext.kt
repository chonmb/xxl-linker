package com.chonmb.xxl.link.fragment.console

import com.chonmb.xxl.link.client.dto.JobLogDetailDTO

/**
 *@author chonmb Email:weichonmb@foxmail.com
 *@date 2026/6/15 15:04
 */

class XxlJobConsoleOutputContext(val logDetail: JobLogDetailDTO? ) : AsyncConsoleOutputContext {

    override fun stop(): Boolean {
        return logDetail?.end ?: true
    }

    override fun message(): String {
        return logDetail?.logContent ?: ""
    }

    override fun cursor(): Int {
        return (logDetail?.toLineNum?.plus(1)) ?: 1
    }
}
