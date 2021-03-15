package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.BlockingAnimation;
import com.destrostudios.grid.client.characters.EntityVisual;

public class ModelAnimation extends Animation {

    private EntityVisual entityVisual;
    private BlockingAnimation blockingAnimation;
    private float passedTime;

    public ModelAnimation(EntityVisual entityVisual, BlockingAnimation blockingAnimation) {
        this.entityVisual = entityVisual;
        this.blockingAnimation = blockingAnimation;
    }

    @Override
    public void start() {
        super.start();
        entityVisual.getModelObject().playAnimation(blockingAnimation.getName(), blockingAnimation.getTotalDuration());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        passedTime += tpf;
        if (passedTime >= blockingAnimation.getBlockDuration()) {
            if (isBlocking()) {
                unblock();
            }
            if (passedTime >= blockingAnimation.getTotalDuration()) {
                finish();
            }
        }
    }

    @Override
    public void end() {
        super.end();
        entityVisual.playIdleAnimation();
    }
}
