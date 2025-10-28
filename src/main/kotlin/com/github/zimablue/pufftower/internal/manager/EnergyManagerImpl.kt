package com.github.zimablue.pufftower.internal.manager

import com.github.zimablue.attrsystem.api.AttrAPI
import com.github.zimablue.attrsystem.api.AttrAPI.getAttrData
import com.github.zimablue.attrsystem.api.attribute.Attribute
import com.github.zimablue.attrsystem.internal.feature.database.ASContainer
import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.pufftower.PuffTower
import com.github.zimablue.pufftower.api.manager.EnergyManager
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.tag.Tag
import taboolib.common5.cdouble

object EnergyManagerImpl : EnergyManager() {
    private val ENERGY_TAG: Tag<Double> = Tag.Double("pt_energy")
    private val enable: Boolean
        get() = PTConfig.config.getBoolean("energy.enable",true)

    private val maxEnergyAttr: Attribute by lazy { AttrAPI.attribute("MaxEnergy")?:error("MaxEnergy attribute not set") }

    private val dataEventNode: EventNode<Event> = EventNode.all("PuffTower-EnergyManager-Data").setPriority(1)
    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        PuffTower.puffTowerEventNode.addChild(dataEventNode)
        dataEventNode
            .addListener(PlayerSpawnEvent::class.java) { event ->
                if(event.isFirstSpawn) initData(event.player)
            }
            .addListener(PlayerDisconnectEvent::class.java) { event ->
                val player = event.player
                val energyData = getEnergy(player)
                ASContainer[player.uuid, ENERGY_TAG.key] = energyData.toString()
            }
            .addListener(PlayerRespawnEvent::class.java) { event ->
                val player = event.player
                setEnergy(player, getMaxEnergy(player))
            }
    }

    override fun getMaxEnergy(player: Player): Double {
        if(!enable) return 0.0
        return player.getAttrData()?.getAttrValue<Double>(maxEnergyAttr) ?: 0.0
    }

    override fun getEnergy(player: Player): Double {
        if(!enable) return 0.0
        // 如果energy为空，初始化数据，但理论上不会到达此处，因为PlayerSpawnEvent会初始化数据
        val energy = player.getTag(ENERGY_TAG)
            ?: return initData(player)
        return energy
    }

    override fun setEnergy(player: Player, energy: Double) {
        val currentEnergy = getEnergy(player)
        if (currentEnergy == energy) return
        // 不可以为负值
        if (energy<=0) {
            player.setTag(ENERGY_TAG, 0.0)
            return
        }
        player.setTag(ENERGY_TAG, energy)

    }
    private fun initData(player: Player) : Double{
        val newEnergy = ASContainer[player.uuid, ENERGY_TAG.key]?.cdouble ?: getMaxEnergy(player)
        player.setTag(ENERGY_TAG, newEnergy)
        return newEnergy
    }

    var Player.energy: Double
        get() = getEnergy(this)
        set(value) {
            setEnergy(this, value)
        }
}