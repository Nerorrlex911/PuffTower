package com.github.zimablue.pufftower

import com.github.zimablue.attrsystem.AttributeSystem
import com.github.zimablue.devoutserver.DevoutServer
import com.github.zimablue.devoutserver.plugin.Plugin
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.plugin.script.PluginScriptManager
import com.github.zimablue.pufftower.api.manager.DungeonManager
import com.github.zimablue.pufftower.api.manager.FeatureManager
import com.github.zimablue.pufftower.api.manager.ItemManager
import com.github.zimablue.pufftower.api.manager.SkillManager
import com.github.zimablue.pufftower.internal.core.command.PuffTowerCommand
import com.github.zimablue.pufftower.internal.manager.DungeonManagerImpl
import com.github.zimablue.pufftower.internal.manager.FeatureManagerImpl
import com.github.zimablue.pufftower.internal.manager.ItemManagerImpl
import com.github.zimablue.pufftower.internal.manager.SkillManagerImpl
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.utils.chunk.ChunkSupplier

object PuffTower : Plugin() {

    val puffTowerEventNode = EventNode.all("PuffTower").setPriority(15)

    val dungeonManager: DungeonManager = DungeonManagerImpl
    val featureManager: FeatureManager = FeatureManagerImpl
    val scriptManager: PluginScriptManager = PluginScriptManager(this)
    val itemManager: ItemManager = ItemManagerImpl
    val skillManager: SkillManager = SkillManagerImpl

    lateinit var instance: Instance

    @Awake(PluginLifeCycle.NONE)
    fun onInit() {
        AttributeSystem.attributeManager.addSubPlugin(this)
    }


    override fun onLoad() {
        super.onLoad()
        scriptManager.init()
    }


    override fun onEnable() {
        super.onEnable()
        logger.info("PuffTower enabled")
        DevoutServer.lamp.register(PuffTowerCommand())
        MinecraftServer.getGlobalEventHandler().addChild(puffTowerEventNode)
        basicSetup()
    }

    fun basicSetup() {
        // Create the instance
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer(AnvilLoader("test2"))

        // Set the ChunkGenerator so that it knows how to generate the world
        instanceContainer.setGenerator { unit: GenerationUnit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK) }

        // Set the lighting system
        instanceContainer.chunkSupplier =
            ChunkSupplier { instance: Instance?, chunkX: Int, chunkZ: Int -> LightingChunk(instance, chunkX, chunkZ) }

        instance = instanceContainer

        // Add an event callback to specify the spawning instance (and the spawn position)
        val globalEventHandler = MinecraftServer.getGlobalEventHandler()
        globalEventHandler
            .addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
                val player: Player = event.player
                event.spawningInstance = instanceContainer
                player.respawnPoint = Pos(0.0, 42.0, 0.0)
            }
//            .addListener(EntityTickEvent::class.java) { event ->
//                val entity = event.entity
//                if(!entity.hasVelocity()) return@addListener
//                if(entity !is EntityCreature) return@addListener
//                if(!entity.hasEntityCollision()) return@addListener
//                entity.instance.getNearbyEntities(
//                    entity.position,
//                    max(entity.boundingBox.width(),entity.boundingBox.depth())+1.0
//                ).forEach { entityToCollide ->
//                    if(entityToCollide !is EntityCreature) return@forEach
//                    if(entityToCollide==event.entity) return@forEach
//                    if(entity.intersectEntity(entity.position, entityToCollide)) {
//                        logger.info("entity collision: ${entity.uuid} -> ${entityToCollide.uuid}")
//                        // push the other entity away
//                        val vec = entityToCollide.position.sub(entity.position).asVec()
//                        val extraVelocity = Vec(max(vec.x,0.1),max(vec.z,0.1)).normalize().mul(5.0)
//                        entityToCollide.velocity = entityToCollide.velocity.add(extraVelocity)
//                        logger.info("entity velocity: + $extraVelocity")
//                    }
//                }
//                val result = CollisionUtils.checkEntityCollisions(
//                    entity,
//                    entity.velocity,
//                    1.15,
//                    { collide: Entity -> collide != entity },
//                    null
//                )
//                for(collisionResult in result) {
//                    logger.info("collision: ${collisionResult.entity.uuid} dir:${collisionResult.direction} percent:${collisionResult.percentage}")
//                    val extraVelocity = collisionResult.direction.normalize().mul(collisionResult.percentage*3)
//                    collisionResult.entity.velocity = collisionResult.entity.velocity.sub(extraVelocity)
//                }

//            }
        // features testing
        featureManager.forEach { id, feat ->
            logger.info("feature: $id loaded to basic instance")
            feat.hook(instance.eventNode())
        }
    }

}