package com.chonmb.xxl.link.fragment.res

import com.chonmb.xxl.link.xxlService
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.jetbrains.rhizomedb.AllParts
import javax.swing.Icon

class Icons {
    val remoteExecutorIcon: Icon= AllIcons.Actions.Execute
    val startIcon:Icon= AllIcons.Debugger.ThreadRunning
    val stopIcon:Icon= AllIcons.Run.Stop
    val addIcon:Icon= AllIcons.Actions.AddFile
    val removeIcon: Icon=AllIcons.Vcs.Remove
    val success:Icon = AllIcons.Status.Success
    val error:Icon = AllIcons.General.Error
    val warning:Icon= AllIcons.General.Warning
    val fileIcon: Icon= AllIcons.FileTypes.AddAny
    val fullStar:Icon = AllIcons.Nodes.Favorite
    val emptyStar:Icon = AllIcons.Nodes.NotFavoriteOnHover
    @JvmField
    val executeBean:Icon= IconLoader.getIcon("/icons/run_bean.svg", javaClass)
    @JvmField
    val executeGlueJava:Icon= IconLoader.getIcon("/icons/run_glue_java.svg", javaClass)
    @JvmField
    val executeShell:Icon= IconLoader.getIcon("/icons/run_shell.svg", javaClass)
    @JvmField
    val processing:Icon= IconLoader.getIcon("/icons/processing.svg", javaClass)


    fun getLineMarkerIcon(jobHandlerName:String):Icon{
        if (xxlService.jobs.containsKey(jobHandlerName)) {
            xxlService.jobs[jobHandlerName]?.let {
                return when(it.glueType){
                    "BEAN"-> executeBean
                    "GLUE_GROOVY"-> executeGlueJava
                    "GLUE_SHELL"-> executeShell
                    else-> remoteExecutorIcon
                }
            }
        }
        return remoteExecutorIcon
    }

}

val jobIcons: Icons by lazy {
    Icons()
}


