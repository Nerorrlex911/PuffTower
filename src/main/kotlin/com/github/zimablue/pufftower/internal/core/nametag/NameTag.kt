package com.github.zimablue.pufftower.internal.core.nametag

import net.minestom.server.instance.Instance

class NameTag(private val nameTagTexts: MutableList<NameTagText> = mutableListOf()) {

    val instance: Instance?
        get() = nameTagTexts.firstOrNull()?.instance

    fun add(element: NameTagText) {
        nameTagTexts.add(element)
        update()
    }
    operator fun set(index: Int, element: NameTagText) {
        nameTagTexts[index] = element
        update()
    }
    operator fun get(index: Int): NameTagText {
        return nameTagTexts[index]
    }
    fun remove(index: Int) {
        nameTagTexts.removeAt(index)
        update()
    }

    fun update() {
        val lastIndex = nameTagTexts.lastIndex
        nameTagTexts.forEachIndexed { index, textTag ->
            textTag.translation = NameTagText.DEFAULT_TRANSLATION.add(0.0,0.3 * (lastIndex-index),0.0)
        }
    }

    fun mount() {
        nameTagTexts.forEach { it.mount() }
    }
    fun remove() {
        nameTagTexts.forEach { it.remove() }
    }
}