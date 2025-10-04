package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.BaseMap
import com.github.zimablue.pufftower.internal.core.skill.SkillData

abstract class ConditionManager : BaseMap<String, (SkillData,Target) -> Boolean>() {

}