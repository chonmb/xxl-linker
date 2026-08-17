package com.chonmb.xxl.link.fragment.console

import com.chonmb.xxl.link.client.dto.JobLogDTO
import com.chonmb.xxl.link.fragment.action.KillJobAction
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.ui.RunContentManager
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.function.Function

class XxlConsole {
    var consoleView: ConsoleView? = null
    var runContentDescriptor: RunContentDescriptor? = null
    val project: Project;
    val name: String
    var infoJob: Job? = null
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    constructor(project: Project, jobHandlerName: String = "XxlJobDisplay") {
        this.project = project;
        this.name = jobHandlerName;
    }

    private fun createConsoleView() {
//        consoleView = XxlConsoleView(this.project)
        consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console;
        runContentDescriptor = RunContentDescriptor(consoleView, null, consoleView?.component!!, name)
    }

    fun addConsoleAction(action: AnAction) {
//        (consoleView as? XxlConsoleView)?.addAction(action)
    }

    private fun showConsole() {
        RunContentManager.getInstance(project)
            .showRunContent(DefaultRunExecutor.getRunExecutorInstance(), runContentDescriptor!!)
    }

    fun initConsole() {
        clearJob()
        if (consoleView == null || consoleView?.component?.isValid == false) {
            createConsoleView()
            showConsole()
        }
        if (runContentDescriptor?.component?.isVisible == false) {
            showConsole()
        }
        clear()
    }

    fun processInfo(message: String) {
        if (message.isEmpty()) {
            return
        }
        if (consoleView?.component?.isValid == false) {
            throw RuntimeException()
        }
        consoleView?.print(
            if (message.endsWith("\n")) message else message + "\n",
            ConsoleViewContentType.NORMAL_OUTPUT
        )
    }

    fun info(message: String) {
        initConsole()
        consoleView?.print(message + "\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    fun asyncInfo(
        function: Function<AsyncConsoleOutputCursor, AsyncConsoleOutputContext>,
        maxInfoTime: Int = 120,
        intervalSeconds: Int = 1
    ) {
        initConsole()
        scope.launch {
            scope.launch(Dispatchers.IO) {
                var index = 0
                var cursor: AsyncConsoleOutputCursor = object : AsyncConsoleOutputCursor {
                    override fun cursor(): Int = 1
                };
                var result: AsyncConsoleOutputContext
                do {
                    try {
                        result = function.apply(cursor)
                    } catch (_: Exception) {
                        consoleView?.print("获取日志失败", ConsoleViewContentType.NORMAL_OUTPUT)
                        return@launch
                    }
                    if (result.message().isNotEmpty()) {
                        consoleView?.print(
                            if (result.message().endsWith("\n")) {
                                result.message()
                            } else result.message() + "\n", ConsoleViewContentType.NORMAL_OUTPUT
                        )
                    }
                    index++
                    cursor = result
                    delay(intervalSeconds * 1000L)
                    val isVisible = withContext(Dispatchers.EDT) {
                        runContentDescriptor?.component?.isVisible == true
                    }
//                    Log.info(cursor.cursor().toString() + "")
//                    Log.info(index.toString() + ":" + (!result.stop()).toString() + (index < maxInfoTime).toString() + (isActive).toString() + (runContentDescriptor?.component?.isVisible == true).toString())
                } while (!result.stop() && index < maxInfoTime && isActive && isVisible)
                withContext(Dispatchers.EDT) {
                    if (runContentDescriptor?.component?.isVisible == true) {
                        consoleView?.print("---xxl-linker---\n日志打印结束\n", ConsoleViewContentType.NORMAL_OUTPUT)
                    }
                }
            }
        }
    }

    fun clearJob() {
        runBlocking {
            infoJob?.cancel()
            infoJob?.join()
            infoJob = null
        }
    }

    fun clear() {
        consoleView?.clear()
    }
}


//class XxlConsoleView : ConsoleViewImpl {
//
//    var toolWindow: ToolWindow? = null
//
//    constructor(project: Project) : super(project, true) {
//    }
//
//    fun showToolWindow(jobLogDTO: JobLogDTO) {
//        // 若未激活，则激活窗口
//        if (toolWindow == null) {
////            val content: Content = ContentFactory.getInstance()
////                .createContent(component, "Output", false)
//            toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Run")
////            toolWindow?.contentManager?.addContent();
//            toolWindow?.setTitleActions(listOf(KillJobAction(jobLogDTO)))
//        }
//
//        if (toolWindow?.isVisible != true) {
//            toolWindow?.show();
//        }
//        // 若已激活，则将其置于最前
//        toolWindow?.activate(null);
//    }
//
//    fun hideToolWindow() {
//        toolWindow?.hide();
//    }
//}
