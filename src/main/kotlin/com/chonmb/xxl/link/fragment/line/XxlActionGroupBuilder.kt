package com.chonmb.xxl.link.fragment.line

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import java.util.function.BooleanSupplier
import java.util.function.Supplier

/**
 *@author chonmb
 *@date 2026/4/9 15:21
 */

class XxlActionGroupBuilder {
    val group = DefaultActionGroup()

    fun build(): DefaultActionGroup {
        return group
    }

    fun menu(action: AnAction): XxlActionGroupBuilder {
        group.add(action)
        return this
    }

    fun menu(action: AnAction, condition: BooleanSupplier): XxlActionGroupBuilder {
        if (condition.asBoolean) {
            group.add(action)
        }
        return this
    }


    fun menu(action: Supplier<AnAction>, condition: BooleanSupplier): XxlActionGroupBuilder {
        if (condition.asBoolean) {
            group.add(action.get())
        }
        return this
    }

    fun separator(): XxlActionGroupBuilder {
        group.addSeparator()
        return this
    }

    fun separator(condition: BooleanSupplier): XxlActionGroupBuilder {
        if (condition.asBoolean) {
            group.addSeparator()
        }
        return this
    }
}
