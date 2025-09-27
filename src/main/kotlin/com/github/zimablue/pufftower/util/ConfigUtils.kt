package com.github.zimablue.pufftower.util

import net.minestom.server.coordinate.Pos
import taboolib.common5.cfloat

fun List<Double>.toPos() = Pos(this[0], this[1], this[2], this[3].cfloat, this[4].cfloat)