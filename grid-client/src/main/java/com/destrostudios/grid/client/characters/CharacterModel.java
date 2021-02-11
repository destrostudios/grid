package com.destrostudios.grid.client.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CharacterModel {
    private String modelName;
    private String idleAnimationName;
    private float idleAnimationLoopDuration;
    private String walkAnimationName;
    private float walkStepDistance;
}
