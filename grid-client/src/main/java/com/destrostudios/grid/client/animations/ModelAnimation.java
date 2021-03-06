package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.ModelAnimationInfo;
import com.destrostudios.grid.client.models.ModelObject;

public class ModelAnimation extends Animation {

    private ModelObject modelObject;
    private ModelAnimationInfo modelAnimationInfo;
    private float passedTime;

    public ModelAnimation(ModelObject modelObject, ModelAnimationInfo modelAnimationInfo) {
        this.modelObject = modelObject;
        this.modelAnimationInfo = modelAnimationInfo;
    }

    @Override
    public void start() {
        super.start();
        modelObject.playAnimation(modelAnimationInfo.getName(), modelAnimationInfo.getTotalDuration());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        passedTime += tpf;
        if (passedTime >= modelAnimationInfo.getBlockDuration()) {
            if (isBlocking()) {
                unblock();
            }
            if (passedTime >= modelAnimationInfo.getTotalDuration()) {
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
