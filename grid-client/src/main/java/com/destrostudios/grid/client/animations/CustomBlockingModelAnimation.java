package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.AnimationInfo;
import com.destrostudios.grid.client.characters.CustomBlockingAnimationInfo;
import com.destrostudios.grid.client.characters.EntityVisual;

public class CustomBlockingModelAnimation extends Animation {

    private EntityVisual entityVisual;
    private CustomBlockingAnimationInfo customBlockingAnimationInfo;
    private float passedTime;

    public CustomBlockingModelAnimation(EntityVisual entityVisual, CustomBlockingAnimationInfo customBlockingAnimationInfo) {
        this.entityVisual = entityVisual;
        this.customBlockingAnimationInfo = customBlockingAnimationInfo;
    }

    @Override
    public void start() {
        super.start();
        AnimationInfo animationInfo = customBlockingAnimationInfo.getAnimationInfo();
        entityVisual.getModelObject().playAnimation(animationInfo.getName(), animationInfo.getLoopDuration(), animationInfo.isLoop(), true);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        passedTime += tpf;
        if (passedTime >= customBlockingAnimationInfo.getBlockDuration()) {
            if (isBlocking()) {
                unblock();
            }
            if (passedTime >= customBlockingAnimationInfo.getTotalDuration()) {
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
