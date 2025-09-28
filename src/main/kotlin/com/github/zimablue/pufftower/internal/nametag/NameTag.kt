package com.github.zimablue.pufftower.internal.nametag

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta
import net.minestom.server.entity.metadata.display.TextDisplayMeta


class NameTag internal constructor(
    /**
     * @return the owner of this nametag (what this is riding)
     */
    val owningEntity: Entity, transparentBackground: Boolean
) :
    Entity(EntityType.TEXT_DISPLAY) {
    private val textMeta: TextDisplayMeta

    init {
        hasPhysics = false
        collidesWithEntities = false
        textMeta = getEntityMeta()
        textMeta.setNotifyAboutChanges(false)
        textMeta.billboardRenderConstraints = AbstractDisplayMeta.BillboardConstraints.VERTICAL
        textMeta.isSeeThrough = true
        if (transparentBackground) {
            textMeta.isUseDefaultBackground = false
            textMeta.backgroundColor = 0
        }
        textMeta.translation = DEFAULT_TRANSLATION
        textMeta.setNotifyAboutChanges(true)
        isAutoViewable = false
        mount()
    }

    var text: Component
        /**
         * @return the content of this nametag
         */
        get() = textMeta.text
        /**
         * @param text the new content of this nametag
         */
        set(text) {
            textMeta.text = text
        }

    var translation: Point
        /**
         * @return the translation from the passenger point of the owner of this nametag to display this at
         */
        get() = textMeta.translation
        /**
         * @param point the new translation from the passenger point of the owner of this nametag to display this at
         */
        set(point) {
            textMeta.translation = point
        }

    override fun getEntityMeta(): TextDisplayMeta {
        return super.getEntityMeta() as TextDisplayMeta
    }

    override fun tick(time: Long) { // don't do anything in super tick as it's unnecessary
        if (isRemoved) return
        for (player in getInstance().players) {
            if (player === owningEntity) continue
            val viewers = getViewers()
            if (!viewers.contains(player) && (owningEntity.isViewer(player) && !owningEntity.isSneaking)) {
                addViewer(player)
            } else if (viewers.contains(player) && (!owningEntity.isViewer(player) || owningEntity.isSneaking)) {
                removeViewer(player)
            }
        }
    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)
        player.sendPacket(NameTagManager.getPassengersPacket(owningEntity)) // necessary otherwise it's not a passenger visually
    }

    /**
     * Effectively teleports this [NameTag] to the player and mounts it to the player.<br></br>
     * Used during initialization and called if this [NameTag] isn't in the same
     * [net.minestom.server.instance.Instance] as the owning player.
     */
    fun mount() {
        val owningInstance = owningEntity.instance ?: return
        val ownerWasViewer = owningEntity is Player && isViewer(owningEntity)
        setInstance(
            owningInstance,
            owningEntity.position.withView(0f, 0f)
        ).whenComplete { unused: Void?, throwable: Throwable? ->
            if (throwable != null) throwable.printStackTrace()
            else {
                owningEntity.addPassenger(this)
                if (ownerWasViewer) addViewer(owningEntity as Player)
            }
        }
    }

    companion object {
        private val DEFAULT_TRANSLATION = Vec(0.0, 0.15, 0.0)
    }
}