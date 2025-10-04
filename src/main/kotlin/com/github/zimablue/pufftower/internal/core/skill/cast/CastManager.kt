package com.github.zimablue.pufftower.internal.core.skill.cast

import com.github.zimablue.devoutserver.plugin.lifecycle.Awake
import com.github.zimablue.devoutserver.plugin.lifecycle.PluginLifeCycle
import com.github.zimablue.devoutserver.util.colored
import com.github.zimablue.pufftower.PuffTower
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.item.PlayerBeginItemUseEvent
import net.minestom.server.event.item.PlayerCancelItemUseEvent
import net.minestom.server.event.item.PlayerFinishItemUseEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.tag.Tag
import net.minestom.server.timer.Task
import net.minestom.server.utils.time.TimeUnit

object CastManager {
    // 释放技能，检测蓝条等条件
    fun castSkill(player: Player, skill: String) {

    }

    val eventNode = EventNode.all("ShapeRecognizer")
    val shapeData = Tag.Transient<MutableList<Pair<Double,Double>>>("shapeData")
    val isDrawing = Tag.Transient<Boolean>("isDrawing").defaultValue(false)
    val startDirection = Tag.Transient<Vec>("startDirection")
    val shapeRecognizeTask = Tag.Transient<Task>("shapeRecognizeTask")

    @Awake(PluginLifeCycle.ENABLE)
    fun onEnable() {
        eventNode.addListener(PlayerBeginItemUseEvent::class.java) { event ->
            val player = event.player
            val item = event.itemStack
            if(item.material()!= Material.TRIDENT) return@addListener
            event.itemUseDuration = 60
            player.setTag(isDrawing,true)
            player.setTag(startDirection,player.position.direction().normalize())
            player.setTag(shapeRecognizeTask, player.scheduler().buildTask {
                if(player.getTag(isDrawing)) {
                    val data = player.getTag(shapeData)?:let{
                        val list = mutableListOf<Pair<Double, Double>>()
                        player.setTag(shapeData, list)
                        list
                    }
                    val startDir = player.getTag(startDirection)!!
                    val currentDir = player.position.direction().normalize()
                    val point = intersectionOnPlane(currentDir,startDir)
                    // 显示粒子
                    player.sendPacket(ParticlePacket(
                        Particle.END_ROD,
                        //player.position.add(currentDir.mul(3.0).add(0.0,player.eyeHeight,0.0))
                        player.position.add(point.mul(3.0).add(0.0,player.eyeHeight,0.0))
                        , Vec.ZERO, 0.0f, 1))
                    val coord = planeCoordinates(point, startDir)
                    //player.sendMessage("绘制点: ${"%.2f".format(coord.first)}, ${"%.2f".format(coord.second)}")
                    data.add(coord)
                } else {
                    val task = player.getTag(shapeRecognizeTask)
                    player.removeTag(shapeRecognizeTask)
                    task.cancel()
                }
            }.repeat(1,TimeUnit.SERVER_TICK).schedule())
        }.addListener(PlayerFinishItemUseEvent::class.java) { event ->
            val player = event.player
            val item = event.itemStack
            onStopUseItem(player, item)
        }.addListener(PlayerCancelItemUseEvent::class.java) { event ->
            val player = event.player
            val item = event.itemStack
            onStopUseItem(player, item)
        }
        PuffTower.puffTowerEventNode.addChild(eventNode)
    }
    private fun onStopUseItem(player: Player, item: ItemStack) {
        if(item.material()!= Material.TRIDENT) return
        player.setTag(isDrawing,false)
        player.getTag(shapeRecognizeTask).cancel()
        player.removeTag(shapeRecognizeTask)
        val data = player.getTag(shapeData)?: error("数据丢失")
        if(data.size<3) {
            player.sendMessage("轨迹点过少，无法识别")
            data.clear()
            return
        }
        val recognizer = ShapeRecognizer()
        val shape = recognizer.recognize(data)
        player.showTitle(Title.title("§6$shape".colored(),"§7(${data.size} points)".colored()))
        data.clear()
        player.removeTag(shapeData)
        player.removeTag(startDirection)
    }
    private fun intersectionOnPlane(a: Vec, b: Vec): Vec {
        return a.mul(b.lengthSquared()/a.dot(b))
    }

    private fun planeCoordinates(point: Vec, b: Vec): Pair<Double, Double> {
        // 构造平面上的两个正交基
        val n = b.normalize()
        val u = if (n.x != 0.0 || n.y != 0.0) Vec(-n.y, n.x, 0.0).normalize() else Vec(1.0, 0.0, 0.0)
        val v = n.cross(u).normalize()
        val d = point.sub(b)
        val x = d.dot(u)
        val y = d.dot(v)
        return x to y
    }

}
