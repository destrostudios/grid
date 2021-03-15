package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.EntityVisual;

public class WalkAnimation extends MoveAnimation {

    public WalkAnimation(EntityVisual entityVisual, int targetX, int targetY, float speed) {
        super(entityVisual, targetX, targetY, speed);
    }

    @Override
    public void start() {
        super.start();
        entityVisual.playWalkAnimation(speed);
    }

    @Override
    public void end() {
        super.end();
        entityVisual.playIdleAnimation();
    }
}
