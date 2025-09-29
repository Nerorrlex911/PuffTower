package com.github.zimablue.pufftower.internal.core.command

import com.github.zimablue.pufftower.internal.core.mob.ZombieMob
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.minestom.actor.MinestomCommandActor

@Command("pufftower", "pt")
class PuffTowerCommand {

    @Subcommand("mob spawn")
    @Description("/pufftower mob spawn <mobId> [<instance>] [<pos: x y z>] - 生成一个怪物")
    fun mob(
        actor: MinestomCommandActor,
        mobId: String, @Optional instance: Instance? = actor.asPlayer()?.instance,
        @Optional pos: Array<Double>? = actor.asPlayer()?.position?.let { arrayOf(it.x, it.y, it.z) }
    ) {
        actor.sender().sendMessage("spawn mob $mobId")
        if (instance == null || pos == null) {
            actor.sender().sendMessage("§c你必须在一个地图中")
            return
        }
        when(mobId) {
            "zombie" -> ZombieMob().setInstance(instance, Pos(pos[0], pos[1], pos[2]))
        }
    }
}