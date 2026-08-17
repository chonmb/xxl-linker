package com.chonmb.xxl.link.client

import com.chonmb.xxl.link.client.dto.JobGroupDTO
import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.client.dto.JobLogDetailDTO
import java.util.function.Consumer

/**
 * @author chonmb
 * @date 2026/3/13 11:07
 */

interface XxlSession {
    fun triggerJob(jobId: Int, params: String?, address: String?);
    fun startJob(jobId: Int);
    fun stopJob(jobId: Int);
    fun logDetails(jobLogDTO: JobLogDTO): String;
    fun logProcessedDetailsAndThen(jobLogDTO: JobLogDTO, consumer: Consumer<String>)
    fun saveJobSource(jobId: Int, jobSource: String);
    fun jobInfo(jobGroup: Int, exeHandler: String?): List<JobInfoDTO>
    fun jobLogs(jobId: Int,jobGroup: Int): List<JobLogDTO>
    fun jobGroup(name: String?, title: String?): List<JobGroupDTO>
    fun freshConfig()
    fun getGroupInstance(group: JobGroupDTO): List<String>
    fun createJob(jobInfoDTO: JobInfoDTO):String?
    fun checkLogin(domain: String, username: String, password: String): Boolean
    fun updateJob(updated: JobInfoDTO): Boolean
    fun removeJob(jobId: Int): Boolean
    fun killJob(jobLogId: Int): Boolean
    fun logProcessingLogDetails(log: JobLogDTO, cursor: Int = 1): JobLogDetailDTO?
}
