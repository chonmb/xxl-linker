package com.chonmb.xxl.link.client

import com.dtflys.forest.Forest
import com.dtflys.forest.reflection.ForestMethod
import com.dtflys.forest.reflection.ForestMethodVariable
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.chonmb.xxl.link.client.dto.JobGroupDTO
import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.client.dto.JobLogDetailDTO
import com.chonmb.xxl.link.client.dto.SourceUpdateDTO
import com.chonmb.xxl.link.config.globalConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


/**
 *@author chonmb
 *@date 2026/3/13 16:50
 */

class DefaultXxlSession : XxlSession {
    private val client: XxlRequestClient = Forest.client<XxlRequestClient>(XxlRequestClient::class.java)
    private val checkClient: XxlCheckLogInRequestClient =
        Forest.client<XxlCheckLogInRequestClient>(XxlCheckLogInRequestClient::class.java)
    private val configuration = Forest.config()
    private val log: Logger = LoggerFactory.getLogger(DefaultXxlSession::class.java)

    fun init() {
        configuration.setVariable(
            "domain",
            ForestMethodVariable { method: ForestMethod<*>? -> globalConfig.domain })
    }

    constructor() {
        init()
    }

    override fun freshConfig() {
        init()
        configuration.setVariable(
            "freshConfig",
            ForestMethodVariable { method: ForestMethod<*>? -> true })
    }

    override fun getGroupInstance(group: JobGroupDTO): List<String> {
        return client.jobGroup(group.id).content?.registryList ?: ArrayList();
    }

    override fun createJob(jobInfoDTO: JobInfoDTO): String? {
        return client.addJob(jobInfoDTO).content
    }

    override fun checkLogin(domain: String, username: String, password: String): Boolean {
        return try {
            checkClient.login(domain, username, password, "on").code == 200
        } catch (e: Exception) {
            false
        }
    }

    override fun updateJob(updated: JobInfoDTO): Boolean {
        return client.updateJob(updated).code == 200
    }

    override fun removeJob(jobId: Int): Boolean {
        return client.operateJob("remove", jobId, null, null).code == 200
    }

    override fun jobInfo(jobGroup: Int, exeHandler: String?): List<JobInfoDTO> {
        return client.jobInfo(jobGroup, "-1", exeHandler, "0", "10").data
    }

    override fun jobGroup(name: String?, title: String?): List<JobGroupDTO> {
        return client.jobGroup(0, 10, name, title).data
    }

    override fun jobLogs(jobId: Int, jobGroup: Int): List<JobLogDTO> {
        return client.jobLogs(0, 10, jobGroup, jobId, -1).data
    }

    override fun triggerJob(jobId: Int, params: String?, address: String?) {
        client.operateJob("trigger", jobId, params, address)
    }

    override fun startJob(jobId: Int) {
        client.operateJob("start", jobId, null, null)
    }

    override fun stopJob(jobId: Int) {
        client.operateJob("stop", jobId, null, null)
    }

    override fun logProcessingLogDetails(log: JobLogDTO, cursor: Int): JobLogDetailDTO? {
//        Log.info(log.toString())
        if (log.triggerCode == 500) {
            return JobLogDetailDTO(true, cursor, "调度失败:\n" + (log.triggerMsg ?: ""), cursor)
        }
        val logDetail = client.jobLogDetail(
            log.id,
            log.executorAddress,
            Instant.parse(log.triggerTime).toEpochMilli(),
            cursor
        ).content
//        Log.info(logDetail.toString())
        if (logDetail == null) {
            var logUpdated = true
            if (log.alarmStatus == 0 && log.triggerCode == 0) {
                logUpdated = freshJobLogStatus(log)
            }
            return if (logUpdated) {
                JobLogDetailDTO(false, cursor, "任务提交中，等待任务日志", cursor)
            } else {
                JobLogDetailDTO(true, cursor, "任务提交中，请稍后再试", cursor)
            }
        }
        return logDetail
    }

    private fun freshJobLogStatus(job: JobLogDTO): Boolean {
        val latest = getJobLog(job)
        latest?.let {
            job.alarmStatus = it.alarmStatus
            job.triggerCode = it.triggerCode
            job.handleCode = it.handleCode
            job.handleMsg = it.handleMsg
            job.triggerTime = it.triggerTime
            job.triggerMsg = it.triggerMsg
            job.executorAddress = it.executorAddress
            return true
        }
        return false
    }

    fun getJobLog(job: JobLogDTO): JobLogDTO? {
        return this.jobLogs(job.jobId, job.jobGroup).find { it.id == job.id }
    }

    override fun logProcessedDetailsAndThen(jobLogDTO: JobLogDTO, consumer: Consumer<String>) {
        if (jobLogDTO.triggerCode != 200) {
            if (jobLogDTO.handleCode == 0) {
                consumer.accept("正在执行，请稍后...")
                return
            }
            consumer.accept(jobLogDTO.triggerMsg ?: "调度失败")
            return
        }
        if (jobLogDTO.handleCode == 0) {
            var index = 0
            var cursor = 1
            var logResult: JobLogDetailDTO? = null;
            consumer.accept("任务正在执行，执行日志：")
            do {
                logResult = client.jobLogDetail(
                    jobLogDTO.id,
                    jobLogDTO.executorAddress,
                    Instant.parse(jobLogDTO.triggerTime).toEpochMilli(),
                    cursor
                ).content
                index++
                if (logResult == null) {
                    break;
                } else {
                    try {
                        consumer.accept(logResult.logContent.replace("<br>", "\n"))
                    } catch (_: Exception) {
                        return
                    }
                    if (logResult.end) {
                        break;
                    } else {
                        cursor = logResult.toLineNum + 1
                    }
                }
                TimeUnit.SECONDS.sleep(1)
            } while (index < 120)
            consumer.accept("任务执行完毕！")
            return
        }
        if (jobLogDTO.handleCode != 200) {
            consumer.accept(jobLogDTO.handleMsg ?: "执行失败")
            return
        }
        consumer.accept(
            client.jobLogDetail(
                jobLogDTO.id,
                jobLogDTO.executorAddress,
                Instant.parse(jobLogDTO.triggerTime).toEpochMilli(),
                1
            ).content!!.logContent.replace("<br>", "\n")
        )
    }

    override fun logDetails(jobLogDTO: JobLogDTO): String {
        val logDetail = client.jobLogDetail(
            jobLogDTO.id,
            jobLogDTO.executorAddress,
            Instant.parse(jobLogDTO.triggerTime).toEpochMilli(),
            1
        ).content
        return if (jobLogDTO.alarmStatus == 1) {
            "任务执行失败:\n失败原因:${
                if (jobLogDTO.triggerCode != 200) {
                    "调度失败"
                } else {
                    "执行失败"
                }
            }\n" +
                    "调度信息:${jobLogDTO.triggerMsg}\n执行信息:${jobLogDTO.handleMsg}\n" +
                    (logDetail?.let { "详细日志:\n${it.logContent}" } ?: "")
        } else {
            logDetail?.logContent ?: "没有详细日志"
        }
    }

    override fun saveJobSource(jobId: Int, jobSource: String) {
        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        client.saveJobCode(SourceUpdateDTO(jobId, jobSource, sf.format(Date())))
    }

    override fun killJob(jobLogId: Int): Boolean {
        return client.jobKill(jobLogId).code == 200
    }
}
