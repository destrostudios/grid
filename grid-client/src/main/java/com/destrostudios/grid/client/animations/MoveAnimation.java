package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.PositionUtil;
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.models.ModelObject;
import com.jme3.math.Vector3f;

public class MoveAnimation extends Animation {

    protected PlayerVisual playerVisual;
    protected Vector3f targetPosition;
    protected float speed;

    public MoveAnimation(PlayerVisual playerVisual, int targetX, int targetY, float speed) {
        this.playerVisual = playerVisual;
        this.targetPosition = new Vector3f(PositionUtil.get3dCoordinate(targetX), PositionUtil.CHARACTER_Y, PositionUtil.get3dCoordinate(targetY));
        this.speed = speed;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        ModelObject modelObject = playerVisual.getModelObject();
        Vector3f oldPosition = modelObject.getLocalTranslation();
        float oldDistanceSquared = oldPosition.distanceSquared(targetPosition);
        Vector3f directionToTarget = targetPosition.subtract(oldPosition).normalizeLocal();
        Vector3f newPosition = oldPosition.add(directionToTarget.mult(tpf * speed));
        float newDistanceSquared = newPosition.distanceSquared(targetPosition);
        if (newDistanceSquared < oldDistanceSquared) {
            JMonkeyUtil.lookAtDirection(modelObject, directionToTarget);
        } else {
            newPosition.set(targetPosition);
            finish();
        }
        modelObject.setLocalTranslation(newPosition);
    }
}
