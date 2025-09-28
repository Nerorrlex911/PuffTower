package com.github.zimablue.pufftower.internal.nametag

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDespawnEvent
import net.minestom.server.event.entity.EntitySpawnEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.network.packet.server.play.SetPassengersPacket
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.minestom.server.tag.Tag
import java.util.function.Function


class NameTagManager(node: EventNode<Event>, private val teamCallback: Function<Entity, Team>) {
    init {
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

    /**
     * Creates a nametag for the provided entity and automatically keeps it attached.
     *
     * @param entity the entity to create a nametag for
     * @param transparentBackground whether the background of the nametag should be transparent or not
     * @return the created nametag for the entity, or current nametag if it already exists
     */
    /**
     * Creates a nametag for the provided entity with a transparent background and automatically keeps it attached.
     *
     * @param entity the entity to create a nametag for
     * @return the created nametag for the entity, or current nametag if it already exists
     */
    @JvmOverloads  // getNameTag() can't be null here due to hasNameTag check
    fun createNameTag(entity: Entity, transparentBackground: Boolean = true): NameTag {
        if (hasNameTag(entity)) return getNameTag(entity)!!
        val nameTag = NameTag(entity, transparentBackground)
        entity.setTag(NAME_TAG, nameTag)
        val nameTagTeam = teamCallback.apply(entity)
        nameTagTeam.nameTagVisibility = TeamsPacket.NameTagVisibility.NEVER
        if (entity.entityType === EntityType.PLAYER) {
            if (entity is Player) nameTagTeam.addMember(entity.username)
            else if (entity.hasTag(USERNAME_TAG)) nameTagTeam.addMember(entity.getTag(USERNAME_TAG))
        } else nameTagTeam.addMember(entity.uuid.toString())
        return nameTag
    }

    companion object {
        private val NAME_TAG: Tag<NameTag> = Tag.Transient("msnametags.name-tag")

        /**
         * Tag to be set on entities which type is a player, so it can be properly added to its team.
         */
        val USERNAME_TAG: Tag<String> = Tag.String("msnametags-username")

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
    }
}