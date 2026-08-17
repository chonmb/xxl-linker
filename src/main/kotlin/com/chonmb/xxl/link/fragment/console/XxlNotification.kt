package com.chonmb.xxl.link.fragment.console

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 *@author chonmb
 *@date 2026/4/9 17:37
 */


fun info(msg: String, project: Project?) {
    project?.let {
        NotificationGroupManager.getInstance().getNotificationGroup("xxl.notification")
            .createNotification(msg, NotificationType.INFORMATION)
            .notify(it)
    }
}

fun error(msg: String, project: Project?) {
    project?.let {
        NotificationGroupManager.getInstance().getNotificationGroup("xxl.notification")
            .createNotification(msg, NotificationType.ERROR)
            .notify(it)
    }
}

fun warning(msg: String, project: Project?) {
    project?.let {
        NotificationGroupManager.getInstance().getNotificationGroup("xxl.notification")
            .createNotification(msg, NotificationType.WARNING)
            .notify(it)
    }
}
