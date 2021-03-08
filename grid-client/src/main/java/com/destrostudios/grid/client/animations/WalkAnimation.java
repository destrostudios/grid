package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.PlayerVisual;

public class WalkAnimation extends MoveAnimation {

    public WalkAnimation(PlayerVisual playerVisual, int targetX, int targetY, float speed) {
        super(playerVisual, targetX, targetY, speed);
    }

    @Override
    public void start() {
        super.start();
        playerVisual.playWalkAnimation(speed);
    }

    @Override
    public void end() {
        super.end();
        playerVisual.playIdleAnimation();
    }
}
