package com.destrostudios.grid.client.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AnimationInfo {

    private String name;
    private float loopDuration;
    private boolean isLoop;

    public AnimationInfo(String name, float loopDuration) {
        this(name, loopDuration, true);
    }
}
