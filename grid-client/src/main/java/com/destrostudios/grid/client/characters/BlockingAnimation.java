package com.destrostudios.grid.client.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BlockingAnimation {
    private String name;
    private float blockDuration;
    private float totalDuration;
}
