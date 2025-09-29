package com.github.zimablue.pufftower.internal.annotations

import com.github.zimablue.devoutserver.plugin.annotation.AnnotationManager
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.ClassUtil.isSingleton
import com.github.zimablue.pufftower.PuffTower

object AutoRegistry {
    @Awake(PluginLifeCycle.NONE)
    fun register() {
        val classes = AnnotationManager.getTargets<AutoRegister>(PuffTower).third
        for (clazz in classes) {
            val registerMethod = clazz.methods.find { it.name == "register" && it.parameterCount == 0 }
            if(clazz.isSingleton()) {
                registerMethod?.invoke(clazz.kotlin.objectInstance)
            } else {
                registerMethod?.invoke(clazz.getDeclaredConstructor().newInstance())
            }
        }
    }
}