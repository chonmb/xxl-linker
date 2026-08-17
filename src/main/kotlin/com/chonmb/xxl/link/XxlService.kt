package com.chonmb.xxl.link

import com.chonmb.xxl.link.client.DefaultXxlSession
import com.chonmb.xxl.link.client.XxlSession
import com.chonmb.xxl.link.client.dto.JobGroupDTO
import com.chonmb.xxl.link.client.dto.JobInfoDTO
import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.client.dto.JobLogDetailDTO
import com.chonmb.xxl.link.config.PluginSettings
import com.chonmb.xxl.link.config.globalConfig
import com.chonmb.xxl.link.config.globalConstant
import com.chonmb.xxl.link.fragment.line.XxlFunctionLineMarkerInfo
import com.intellij.lang.jvm.types.JvmPrimitiveTypeKind
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiImportStatementBase
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import java.util.function.Consumer
import com.intellij.util.application
import java.util.concurrent.TimeUnit
import kotlin.collections.orEmpty

/**
 *@author chonmb
 *@date 2026/3/18 10:38
 */

@Service
class XxlService {
    val session: XxlSession = DefaultXxlSession()

    var group: JobGroupDTO? = null;
    val jobs: MutableMap<String, JobInfoDTO> = HashMap<String, JobInfoDTO>()

    private fun getGroup(executorName: String): JobGroupDTO? {
        if (group == null || group!!.appname == executorName) {
            group = session.jobGroup(executorName, null)[0]
        } else {
            group = null;
        }
        return group;
    }

    fun getJob(executorName: String, jobHandlerName: String): JobInfoDTO? {
        if (jobs.containsKey(jobHandlerName)) {
            return jobs.get(jobHandlerName)
        }
        val rst = session.jobInfo(getGroup(executorName)!!.id, jobHandlerName)
        if (rst.isEmpty()) {
            return null
        }
        val job = rst[0]
        jobs[job.executorHandler] = job
        return jobs[jobHandlerName]
    }

    fun clearAllCache() {
        group = null;
        jobs.clear();
    }

    fun existJob(executorName: String, jobHandlerName: String): Boolean {
        return jobs.containsKey(jobHandlerName) || getJob(executorName, jobHandlerName) != null
    }

    fun getLatestLogDetail(executorName: String, jobHandlerName: String): String {
        val logs = session.jobLogs(getJob(executorName, jobHandlerName)!!.id, getGroup(executorName)!!.id)
        if (logs.isEmpty()) {
            return "no logs"
        }
        val latestLog = logs[0]
        return session.logDetails(latestLog).replace("<br>", "\n")
    }

    fun triggerJobOnce(
        executorName: String,
        jobHandlerName: String,
        params: String? = null,
        address: String? = null
    ): JobLogDTO? {
        val job = getJob(executorName, jobHandlerName)
        session.triggerJob(job!!.id, params, address)
        waitJobSchedule()
        val logs = session.jobLogs(job.id, getGroup(executorName)!!.id)
        return if (logs.isEmpty()) null else logs[0]
    }

    fun getProcessingJobLogDetailsAndThen(jobLogDTO: JobLogDTO?, consumer: Consumer<String>) {
        if (jobLogDTO == null) {
            consumer.accept("not found job, waiting for submitting job ...")
            return
        }
        session.logProcessedDetailsAndThen(jobLogDTO, consumer)
    }

    fun getJobLogDetails(jobLogDTO: JobLogDTO): String {
        return session.logDetails(jobLogDTO).replace("<br>", "\n")
    }

    fun getProcessingLogDetails(log: JobLogDTO, cursor: Int = 1): JobLogDetailDTO? {
        return session.logProcessingLogDetails(log, cursor)
    }

    fun getRegisteredInstance(executorName: String): List<String>? {
        return getGroup(executorName)?.let { session.getGroupInstance(it) }
    }

    fun triggerJobWithAllInstance(executorName: String, jobHandlerName: String): JobLogDTO? {
        val job = getJob(executorName, jobHandlerName)
        val instances = getRegisteredInstance(executorName)
        if (instances != null) {
            for (instance in instances) {
                session.triggerJob(job!!.id, null, instance)
            }
        }
        val logs = session.jobLogs(job!!.id, getGroup(executorName)!!.id)
        return logs[0]
//        if (logs.isEmpty()) {
//            return "job is submitting, waiting for log ..."
//        }
//        return session.logDetails(logs[0]).replace("<br>", "\n")
    }

    fun stopJob(executorName: String, jobHandlerName: String) {
        val job = getJob(executorName, jobHandlerName)
        session.stopJob(job!!.id)
    }

    fun startJob(executorName: String, jobHandlerName: String) {
        val job = getJob(executorName, jobHandlerName)
        session.startJob(job!!.id)
    }

    fun updateGlueJobSource(executorName: String, jobHandlerName: String, resource: String) {
        val job = getJob(executorName, jobHandlerName)
        session.saveJobSource(job!!.id, resource)
    }

    fun freshConfig() {
        session.freshConfig()
        clearAllCache()
    }

    fun createJob(
        executorName: String,
        jobHandlerName: String,
        glueType: String = "BEAN",
        glueSource: String = ""
    ): String? {
        val job = JobInfoDTO(
            jobGroup = getGroup(executorName)!!.id,
            jobDesc = executorName,
            author = executorName,
            executorHandler = jobHandlerName,
            scheduleType = "NONE",
            glueType = glueType,
            id = 0,
            triggerStatus = 0,
            scheduleConf = "",
            misfireStrategy = "DO_NOTHING",
            executorBlockStrategy = "SERIAL_EXECUTION",
            executorTimeout = 0,
            executorFailRetryCount = 0,
            executorRouteStrategy = "FIRST",
            glueSource = glueSource,
            glueRemark = if (glueSource.isEmpty()) "" else "初始化"
        );
        return session.createJob(job)
    }

    fun createJobAndTriggerIt(executorName: String, jobHandlerName: String): JobLogDTO {
        val id = Integer.getInteger(createJob(executorName, jobHandlerName))
        session.triggerJob(id, null, null)
        waitJobSchedule()
        return session.jobLogs(id, getGroup(executorName)!!.id)[0]
    }

    fun waitJobSchedule() {
        TimeUnit.MILLISECONDS.sleep(500)
    }

    fun createGlueCodeJobAndTriggerIt(executorName: String, info: XxlFunctionLineMarkerInfo): JobLogDTO? {
        convertGlueSource(info.method).let {
            return if (it.success) {
                val id = Integer.parseInt(createJob(executorName, info.jobHandlerName, "GLUE_GROOVY", it.source))
                session.triggerJob(id, null, null)
                waitJobSchedule()
                val logs = session.jobLogs(id, getGroup(executorName)!!.id)
                if (logs.isNotEmpty()) {
                    logs[0]
                } else {
                    null
                }
            } else {
                throw RuntimeException("update glue source failed")
            }
        }
    }

    fun updateGlueCodeJobAndTriggerIt(executorName: String, jobHandlerName: String, source: String): JobLogDTO? {
        updateGlueJobSource(executorName, jobHandlerName, source)
        jobs.remove(jobHandlerName)
        return triggerJobOnce(executorName, jobHandlerName)
    }

    fun checkLogin(domain: String, username: String, password: String): Boolean {
        return session.checkLogin(domain.removeSuffix("/"), username, password)
    }

    fun getActiveConfig(): PluginSettings.SessionState? {
        return globalConfig.activeConfig
    }

    fun setActiveConfig(env: String): Boolean {
        globalConfig.activeEnv = env
        freshConfig()
        return true
    }

    fun convertGlueSource(method: PsiMethod): BuildGlueSourceInfoDTO {
        val warnings = ArrayList<String>()
        if (!method.parameterList.isEmpty) {
            warnings.add("Parameter list must not be empty")
        }
        if (method.parent == null || method.parent !is PsiClass) {
            warnings.add("method parent must be a class")
        } else if (method.parent.parent == null || method.parent.parent !is PsiJavaFile) {
            warnings.add("method parent must be a java fileIcon")
        }
        if (warnings.isEmpty()) {
            val clazz = method.parent as PsiClass
            val classFile = clazz.parent as PsiJavaFile
            val imports = classFile.importList?.allImportStatements?.filter {
                globalConfig.source?.imports?.contains(it.importReference?.text)?.not() ?: false
            }.orEmpty()
            return BuildGlueSourceInfoDTO(
                buildGlueSource(imports, clazz, method),
                warnings,
                true
            )
        } else {
            return BuildGlueSourceInfoDTO(
                "",
                warnings,
                false
            )
        }
    }

    fun buildGlueSource(imports: List<PsiImportStatementBase>, clazz: PsiClass, method: PsiMethod): String {

        val fields = clazz.allFields
        val innerClasses = clazz.allInnerClasses
        val autowiredTypes = clazz.constructors.flatMap { m -> m.parameterList.parameters.map { p -> p.type } }.filter {
            JvmPrimitiveTypeKind.getKindByName(it.presentableText) == null || JvmPrimitiveTypeKind.getKindByFqn(it.presentableText) == null
        }
        val autowiredFields = fields.filter { autowiredTypes.contains(it.type) }
        val addAutowiredImport =
            autowiredFields.isNotEmpty() && imports.none { it.text == globalConfig.source?.springAutowiredClazz }
        val autoWiredAnnotation = "@${globalConfig.source?.springAutowiredClazz?.split(".")?.last() ?: "Autowired"}"

        val result = """package com.xxl.job.service.handler;

${globalConfig.source.imports.joinToString("\n") { "import ${it};" }}

${if (addAutowiredImport) "import ${globalConfig.source.springAutowiredClazz};" else ""}
${imports.joinToString(separator = "\n") { "${it.text}" }}

public class DemoGlueJobHandler extends IJobHandler {

${"\t" + fields.joinToString("\n\t") { if (autowiredTypes.contains(it.type)) "${autoWiredAnnotation}\n\t" + it.text else it.text }}

${"\t" + innerClasses.joinToString("\n\t") { it.text }}

	@Override
	public void execute() throws Exception {
		${method.name}()
	}

    ${"\t" + clazz.methods.filter { method -> !method.isConstructor }.joinToString("\n\n\t") { it.text }}
}"""
        return result
    }

    fun updateJob(updated: JobInfoDTO): Boolean {
        jobs.remove(updated.executorHandler)
        return session.updateJob(updated)
    }

    fun removeJob(executorName: String, jobHandlerName: String): Boolean {
        val job = getJob(executorName, jobHandlerName)
        jobs.remove(jobHandlerName)
        return session.removeJob(job!!.id)
    }

    fun getGlueSource(executorName: String, jobHandlerName: String): String {
        return getJob(executorName, jobHandlerName)?.glueSource ?: ""
    }

    fun createShellJobAndTriggerIt(executorName: String, jobHandlerName: String, script: String): JobLogDTO? {
        val id = Integer.parseInt(createJob(executorName, jobHandlerName, "GLUE_SHELL", script))
        session.triggerJob(id, null, null)
        waitJobSchedule()
        val logs = session.jobLogs(id, getGroup(executorName)!!.id)
        return if (logs.isNotEmpty()) logs[0] else null
    }

    fun getJobLogs(executorName: String, jobHandlerName: String): List<JobLogDTO> {
        val job = getJob(executorName, jobHandlerName) ?: return emptyList<JobLogDTO>()
        return session.jobLogs(job.id, getGroup(executorName)!!.id)
    }

    fun getJobLogs(job: JobInfoDTO): List<JobLogDTO> {
        return session.jobLogs(job.id, job.jobGroup)
    }

    fun killJob(jobLogDTO: JobLogDTO): Boolean {
        return session.killJob(jobLogDTO.id)
    }

    fun isOverLongGlueSource(executorName: String, jobHandlerName: String): Boolean {
        return getGlueSource(executorName, jobHandlerName).length > globalConstant.warningGlueSourceLength
    }
}

val xxlService by lazy { application.getService(XxlService::class.java)!! }
