package com.github.zimablue.pufftower.internal.core.nametag

import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.pufftower.PuffTower
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.event.entity.EntitySpawnEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.network.packet.server.play.SetPassengersPacket
import net.minestom.server.tag.Tag


object NameTagManager {
    private fun init(node: EventNode<Event>) {
        val respawnListener = EventListener.builder(
            PlayerRespawnEvent::class.java
        )
            .handler { event: PlayerRespawnEvent ->
                val player = event.player
                if (!hasNameTag(player)) return@handler
                MinecraftServer.getSchedulerManager().scheduleNextTick {
                    val nameTag = getNameTag(player)
                    nameTag?.mount()
                }
            }
            .build()
        node.addListener(respawnListener)
        val spawnListener = EventListener.builder(
            EntitySpawnEvent::class.java
        )
            .handler { event: EntitySpawnEvent ->
                val entity = event.entity
                val instance = event.instance
                val nameTag = getNameTag(entity)
                if (nameTag != null) {
                    if (instance !== nameTag.instance) nameTag.mount()
                }
                // passengers fix for this player joining to see passengers of online players
                if (entity !is Player) return@handler
                for (e in instance.entities) {
                    entity.sendPacket(getPassengersPacket(e))
                }
            }
            .build()
        node.addListener(spawnListener)
        val despawnListener = EventListener.builder(
            EntityDespawnEvent::class.java
        )
            .handler { event: EntityDespawnEvent ->
                val entity = event.entity
                if (hasNameTag(entity)) entity.getTag(NAME_TAG).remove()
            }
            .build()
        node.addListener(despawnListener)
        val deathListener = EventListener.builder(
            EntityDeathEvent::class.java
        )
            .handler{ event ->
                val entity = event.entity
                if (hasNameTag(entity)) entity.getTag(NAME_TAG).remove()
            }
            .build()
        node.addListener(deathListener)
    }

    /**
     * @param entity the entity to check to see if it has a nametag
     * @return whether the provided entity has a nametag or not
     */
    fun hasNameTag(entity: Entity): Boolean {
        return entity.hasTag(NAME_TAG)
    }

    /**
     * @param entity the entity to try to get the nametag of
     * @return the nametag of the provided entity, or null
     */
    fun getNameTag(entity: Entity): NameTag? {
        if (hasNameTag(entity)) return entity.getTag(NAME_TAG)
        return null
    }

    fun createNameTag(entity: Entity): NameTag {
        val nameTag = NameTag()
        entity.setTag(NAME_TAG, nameTag)
        return nameTag
    }

    fun createNameTag(entity: Entity, texts: List<Component>, transparent: Boolean=true):NameTag {
        val nameTag = createNameTag(entity)
        texts.forEach {
            nameTag.add(NameTagText(entity,it,transparent))
        }
        return nameTag
    }

    private val NAME_TAG: Tag<NameTag> = Tag.Transient("pufftower.name-tag")

    /**
     * [Entity.getPassengersPacket]
     *
     * @param entity the entity to get the passengers of
     * @return passengers packet of the provided entity
     */
    fun getPassengersPacket(entity: Entity): SetPassengersPacket {
        return SetPassengersPacket(
            entity.entityId,
            entity.passengers.stream().map { obj: Entity -> obj.entityId }.toList()
        )
    }
    private val eventNode: EventNode<Event> = EventNode.all("PuffTower-NameTagManager").setPriority(1)

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        PuffTower.puffTowerEventNode.addChild(eventNode)
        init(eventNode)
    }

}