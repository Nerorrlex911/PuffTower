package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.BaseMap
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minestom.server.item.ItemStack

abstract class ItemManager: BaseMap<String,JsonElement>() {
    abstract fun getItemStack(id: String): ItemStack?
}