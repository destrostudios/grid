package com.destrostudios.grid.client.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomBlockingAnimationInfo {

    private AnimationInfo animationInfo;
    private float blockDuration;
    private float totalDuration;

    public CustomBlockingAnimationInfo(String animationName, float blockDuration, float totalDuration) {
        this(new AnimationInfo(animationName, totalDuration), blockDuration, totalDuration);
    }
}
