package com.github.zimablue.pufftower.api.manager

import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

abstract class WeaponManager {
    companion object{
        val ITEM_TYPE = Tag.String("item_type")
        val ItemStack.itemType: String?
            get() = this.getTag(ITEM_TYPE)
    }
}