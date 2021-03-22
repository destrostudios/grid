package com.destrostudios.grid.client.characters;

import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.models.ModelObject;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.ProgressBar;
import lombok.Getter;

public class EntityVisual {

    public EntityVisual(Camera camera, AssetManager assetManager, ModelInfo modelInfo, ColorRGBA nameColor) {
        this.modelInfo = modelInfo;
        modelObject = new ModelObject(assetManager, "models/" + modelInfo.getModelName() + "/skin.xml");
        modelObject.addControl(new EntityVisualControl(this, camera));
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        // Calculate it here (in t-pose) before animations start changing the bounding box
        height = JMonkeyUtil.getWorldSize(modelObject).getY();
        playIdleAnimation();

        lblName = new Label("");
        lblName.setFontSize(14);
        lblName.setColor(nameColor);

        healthBar = new ProgressBar();
        healthBar.setPreferredSize(new Vector3f(100, 20, 1));
        healthBar.getLabel().setColor(ColorRGBA.White);
    }
    @Getter
    private ModelInfo modelInfo;
    @Getter
    private ModelObject modelObject;
    @Getter
    private float height;
    @Getter
    private Label lblName;
    @Getter
    private ProgressBar healthBar;
    private boolean hasNextAnimationInfo;
    private AnimationInfo nextAnimationInfo;

    public void updateAnimation() {
        if (hasNextAnimationInfo) {
            if (nextAnimationInfo != null) {
                modelObject.playAnimation(nextAnimationInfo.getName(), nextAnimationInfo.getLoopDuration(), nextAnimationInfo.isLoop(), false);
                nextAnimationInfo = null;
            } else {
                modelObject.stopAndRewindAnimation();
            }
            hasNextAnimationInfo = false;
        }
    }

    public void playIdleAnimation() {
        setNextAnimationInfo(modelInfo.getIdleAnimation());
    }

    public void playWalkAnimation(float walkSpeed) {
        AnimationInfo walkAnimation = modelInfo.getWalkAnimation();
        if (walkAnimation != null) {
            // walkAnimation.loopDuration = walkStepDistance
            setNextAnimationInfo(new AnimationInfo(walkAnimation.getName(), (walkAnimation.getLoopDuration() / walkSpeed)));
        } else {
            setNextAnimationInfo(null);
        }
    }

    public void playDeathAnimation() {
        setNextAnimationInfo(modelInfo.getDeathAnimation());
    }

    private void setNextAnimationInfo(AnimationInfo animationInfo) {
        nextAnimationInfo = animationInfo;
        hasNextAnimationInfo = true;
    }

    public void updateGuiControlPositions(Camera camera) {
        placeAboveModel(camera, healthBar, 0);
        placeAboveModel(camera, lblName, 20);
    }

    private void placeAboveModel(Camera camera, Panel panel, int additionalScreenY) {
        Vector3f screenPosition = camera.getScreenCoordinates(modelObject.getWorldTranslation().add(0, height + 1, 0));
        screenPosition.addLocal(panel.getPreferredSize().getX() / -2, additionalScreenY, 0);
        panel.setLocalTranslation(screenPosition);
    }

    public void setMaximumHealth(float maximumHealth) {
        healthBar.getModel().setMaximum(maximumHealth);
        updateHealthBarLabel();
    }

    public void setCurrentHealth(float health) {
        healthBar.setProgressValue(health);
        updateHealthBarLabel();
    }

    private void updateHealthBarLabel() {
        healthBar.setMessage(String.format("%s / %s", (int) healthBar.getProgressValue(), (int) healthBar.getModel().getMaximum()));
    }
}
