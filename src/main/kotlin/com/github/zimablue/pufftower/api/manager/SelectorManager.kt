package com.github.zimablue.pufftower.api.manager

import com.github.zimablue.devoutserver.util.map.BaseMap
import com.github.zimablue.pufftower.internal.core.skill.SkillData
import com.github.zimablue.pufftower.internal.core.skill.Target

abstract class SelectorManager : BaseMap<String, SkillData.() -> List<Target>>() {
    
}