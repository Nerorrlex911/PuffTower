package com.github.zimablue.pufftower.util

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.time.TimeUnit
import java.time.Duration

object EffectUtil {
    fun playThunder(
        instance: Instance,
        pos: Point,
        sound: Boolean = false,
        viewers: Collection<Player>? = null,
    ) {
        val entity = Entity(EntityType.LIGHTNING_BOLT)
        entity.isSilent = !sound
        entity.isAutoViewable = viewers.isNullOrEmpty() //若为空或者null，设为自动可见
        viewers?.forEach { player ->
            entity.addViewer(player)
        }
        entity.setInstance(instance, pos).thenRun {
            entity.scheduleRemove(Duration.of(20, TimeUnit.SERVER_TICK))
        }
    }
}
