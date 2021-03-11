package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.BlockingAnimation;
import com.destrostudios.grid.client.models.ModelObject;

public class ModelAnimation extends Animation {

    private ModelObject modelObject;
    private BlockingAnimation blockingAnimation;
    private float passedTime;

    public ModelAnimation(ModelObject modelObject, BlockingAnimation blockingAnimation) {
        this.modelObject = modelObject;
        this.blockingAnimation = blockingAnimation;
    }

    @Override
    public void start() {
        super.start();
        modelObject.playAnimation(blockingAnimation.getName(), blockingAnimation.getTotalDuration());
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
        modelObject.stopAndRewindAnimation();
    }
}
