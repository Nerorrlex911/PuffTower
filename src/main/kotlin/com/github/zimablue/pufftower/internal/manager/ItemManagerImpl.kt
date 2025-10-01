package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.PuffTower.extractResource
import com.github.zimablue.pufftower.api.manager.ItemManager
import com.github.zimablue.pufftower.util.getAllFiles
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minestom.server.codec.Transcoder
import net.minestom.server.item.ItemStack

object ItemManagerImpl : ItemManager() {

    private val gson by lazy { Gson() }

    override fun getItemStack(id: String): ItemStack? {
        return ItemStack.CODEC.decode(Transcoder.JSON, get(id) ?: return null).orElse(null)
    }
    @Awake(PluginLifeCycle.LOAD)
    fun onLoad() {
        extractResource("items")
    }
    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        onReload()
    }
    @Awake(PluginLifeCycle.RELOAD)
    fun onReload() {
        clear()
        getAllFiles(PuffTower.dataDirectory.resolve("items").toFile()).forEach { file ->
            val items = gson.fromJson(file.readText(), JsonObject::class.java)
            putAll(items.asMap())
        }
    }

}