package com.github.zimablue.pufftower.internal.core.mob

import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.colored
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.internal.core.nametag.NameTagManager
import com.github.zimablue.pufftower.internal.core.nametag.NameTagText
import com.github.zimablue.pufftower.internal.manager.PTConfig.debug
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.entity.EntityDeathEvent
import net.minestom.server.event.entity.EntitySpawnEvent
import kotlin.math.floor

open class Mob(entityType: EntityType, val id: String, val name: String) : EntityCreature(entityType) {

    private lateinit var healthBarTagText: NameTagText

    init {
        isCustomNameVisible = false
        //set(DataComponents.CUSTOM_NAME, "&e$name".colored())
    }

    val displayName = "&e$name".colored()

    companion object {
        val BLOCK_LENGTH: Int = 6
        val CHARACTERS: List<String> = listOf(
            "", "▏", "▎", "▍",
            "▌", "▋", "▊", "▉"
        )
        val FULL_BLOCK_CHAR: String = "█"

        private fun generateHealthBar(health: Float, maxHealth: Float): Component {
            // Converts the health percentage into a number from 0-{blockLength} -- only 0 if the mob's health is 0
            val charHealth: Double =
                ((health / maxHealth) * BLOCK_LENGTH).toDouble()
            return Component.text()
                .append(Component.text("[", NamedTextColor.DARK_GRAY))
                .append(
                    Component.text(
                        FULL_BLOCK_CHAR.repeat(floor(charHealth).toInt()),
                        NamedTextColor.RED
                    )
                ).append(
                    Component.text(
                        CHARACTERS[Math.round(
                            (charHealth - floor(charHealth)) // number from 0-1
                                    * (CHARACTERS.size - 1) // indexes start at 0
                        ).toInt()], NamedTextColor.YELLOW
                    )
                )
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .build()
        }

        fun isMob(entity: Entity) = entity is Mob
        val mobNode = EventNode.event("PuffTower-Mob", EventFilter.ENTITY) {
            isMob(it.entity)
        }.setPriority(0)

        @Awake(PluginLifeCycle.ENABLE)
        fun onEnable() {
            mobNode.addListener(EntitySpawnEvent::class.java) { event ->
                val mob = event.entity as Mob
                val healthTagText = NameTagText(mob,
                    generateHealthBar(mob.health, mob.getAttributeValue(Attribute.MAX_HEALTH).toFloat())
                )
                mob.healthBarTagText = healthTagText
                val nameTagText = NameTagText(mob,mob.displayName)
                val tag = NameTagManager.createNameTag(
                    mob
                ).apply {
                    add(healthTagText)
                    add(nameTagText)
                }
            }.addListener(EntityDamageEvent::class.java) { event ->
                val mob = event.entity as Mob
                mob.healthBarTagText.text = generateHealthBar(mob.health-event.damage.amount, mob.getAttributeValue(Attribute.MAX_HEALTH).toFloat())
            }.addListener(EntityDeathEvent::class.java) { event ->
                val mob = event.entity as Mob
                mob.healthBarTagText.text = generateHealthBar(mob.health, 0.0f)
            }
            PuffTower.puffTowerEventNode.addChild(mobNode)
        }

        fun spawnMob(id: String): Mob? {
            val mob = when (id.lowercase()) {
                "zombie" -> ZombieMob()
                "train_dummy" -> TrainDummyMob()
                else -> null
            }
            return mob
        }


    }
}