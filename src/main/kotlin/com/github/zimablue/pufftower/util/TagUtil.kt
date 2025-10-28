package com.github.zimablue.pufftower.util

import net.minestom.server.entity.Entity
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule

fun <T> Entity.setTagExpire(tag: Tag<T>,value: T,expireTime: Int) {
    this.setTag(tag,value)
    this.scheduler().buildTask{
        this.removeTag(tag)
    }.delay(TaskSchedule.tick(expireTime)).schedule()
}