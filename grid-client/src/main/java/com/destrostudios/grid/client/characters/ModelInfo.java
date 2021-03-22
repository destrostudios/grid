package com.destrostudios.grid.client.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ModelInfo {

    private String modelName;
    private AnimationInfo idleAnimation;
    // walkAnimation.loopDuration = walkStepDistance
    private AnimationInfo walkAnimation;
    private AnimationInfo deathAnimation;

    public ModelInfo(String modelName) {
        this(modelName, null, null, null);
    }
}
