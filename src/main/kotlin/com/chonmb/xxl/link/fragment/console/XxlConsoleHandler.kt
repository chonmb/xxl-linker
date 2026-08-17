package com.chonmb.xxl.link.fragment.console

import com.intellij.openapi.project.Project

interface XxlConsoleHandler {
    fun getConsole(project: Project?): XxlConsole
}
