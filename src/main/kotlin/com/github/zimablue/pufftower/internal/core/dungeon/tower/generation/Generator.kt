package com.github.zimablue.pufftower.internal.core.dungeon.tower.generation

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Generator<T: Any>(
    val chance: Double,
    val function: (GeneratorContext) -> T
) {
    val conditions: MutableList<(GeneratorContext) -> Boolean> = mutableListOf()
    val controllers: MutableList<(GeneratorContext) -> Control> = mutableListOf()
    val preferences: MutableList<Preference> = mutableListOf()

    private fun shouldGenerate(context: GeneratorContext): Boolean {
        // chance
        if(Random.nextDouble() <= chance) {
            return false
        }
        // conditions
        for(condition in conditions) {
            if(!condition(context)) {
                return false
            }
        }
        // controllers
        for(controller in controllers) {
            return when(controller(context)) {
                Control.ALLOW -> true
                Control.DISALLOW -> false
                Control.NEXT -> continue
            }
        }
        // preferences
        if(preferences.isNotEmpty()) {
            val score = preferences.sumOf { max(min(it.isPreferred(context), 0.0), 1.0) * it.weight }
            val chance = preferences.sumOf { it.weight }
            return Random.nextDouble() <= score / chance
        } else {
            return true
        }
    }

    fun generate(context: GeneratorContext) : T? {
        return if(shouldGenerate(context)) function(context) else null
    }

    fun condition(condition: (GeneratorContext) -> Boolean) : Generator<T> {
        conditions.add(condition)
        return this
    }

    fun controller(controller: (GeneratorContext) -> Control) : Generator<T> {
        controllers.add(controller)
        return this
    }

    fun preference(preference: (GeneratorContext) -> Double, weight: Double = 1.0) : Generator<T>{
        preferences.add(Preference(preference, weight))
        return this
    }

    enum class Control {
        ALLOW,
        DISALLOW,
        NEXT
    }

    class Preference(
        val isPreferred: (GeneratorContext) -> Double,
        val weight: Double,
    )

}