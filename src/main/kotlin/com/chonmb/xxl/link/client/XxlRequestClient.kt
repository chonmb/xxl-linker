package com.chonmb.xxl.link.client

import com.dtflys.forest.annotation.*;
import com.chonmb.xxl.link.client.dto.JobGroupDTO
import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.client.dto.JobLogDetailDTO
import com.chonmb.xxl.link.client.dto.SourceUpdateDTO
import com.chonmb.xxl.link.client.dto.XxlObjectResponse
import com.chonmb.xxl.link.client.dto.XxlResponse

/**
 * @author chonmb
 * @date 2026/3/13 11:18
 */

@BaseRequest(
    baseURL = "{domain}",
    interceptor = [DefaultXxlRequestInterceptor::class]
)
@Retry(maxRetryCount = "2", maxRetryInterval = "500")
interface XxlRequestClient : XxlLogInRequestClient {

    @Post(value = "jobinfo/pageList")
    fun jobInfo(
        @Body("jobGroup") jobGroup: Int,
        @Body("triggerStatus") triggerStatus: String,
        @Body("executorHandler") executorHandler: String?,
        @Body("start") start: String,
        @Body("length") length: String
    ): XxlResponse<JobInfoDTO>;

    @Get(value = "jobgroup/pageList")
    fun jobGroup(
        @Query("start") start: Int,
        @Query("length") length: Int,
        @Query("appname") appname: String?,
        @Query("title") title: String?
    ): XxlResponse<JobGroupDTO>;

    @Post(value = "jobgroup/loadById")
    fun jobGroup(
        @Body("id") id: Int
    ): XxlObjectResponse<JobGroupDTO>;

    @Post(value = "joblog/getJobsByGroup")
    fun jobGroupLogs(
        @Body("jobGroup") jobGroup: Int
    ): XxlResponse<JobLogDTO>;

    @Post(value = "joblog/pageList")
    fun jobLogs(
        @Query("start") start: Int,
        @Query("length") length: Int,
        @Body("jobGroup") jobGroup: Int,
        @Body("jobId") jobId: Int,
        @Body("logStatus") logStatus: Int
    ): XxlResponse<JobLogDTO>;

    @Post(value = "joblog/logDetailCat")
    fun jobLogDetail(
        @Body("logId") logId: Int?,
        @Body("executorAddress") executorAddress: String?,
        @Body("triggerTime") triggerTime: Long?,
        @Body("fromLineNum") fromLineNum: Int
    ): XxlObjectResponse<JobLogDetailDTO>;

    @Post("joblog/logKill")
    fun jobKill(@Body("id") id: Int?): XxlObjectResponse<JobLogDetailDTO>

    @Post("/jobcode/save", contentType = "application/x-www-form-urlencoded; charset=UTF-8")
    fun saveJobCode(
        @Body updated: SourceUpdateDTO
    );

    @Post("/jobinfo/{operation}")
    fun operateJob(
        @Var("operation") operation: String,
        @Body("id") id: Int,
        @Body("executorParam") executorParam: String?,
        @Body("addressList") addressList: String?
    ): XxlObjectResponse<String>

    @Post("/jobinfo/add")
    fun addJob(
        @Body jobInfoDTO: JobInfoDTO
    ): XxlObjectResponse<String>

    @Post("/jobinfo/update")
    fun updateJob(@Body updated: JobInfoDTO): XxlObjectResponse<String>

}
