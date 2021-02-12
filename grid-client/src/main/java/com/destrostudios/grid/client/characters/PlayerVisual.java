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

public class PlayerVisual {

    public PlayerVisual(Camera camera, AssetManager assetManager) {
        characterModel = getRandomModel();
        modelObject = new ModelObject(assetManager, "models/" + characterModel.getModelName() + "/skin_default.xml");
        modelObject.addControl(new PlayerVisualControl(this, camera));
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        playIdleAnimation();
        // Calculate it here (in t-pose) before animations start changing the bounding box
        height = JMonkeyUtil.getWorldSize(modelObject).getY();

        lblName = new Label("");
        lblName.setFontSize(14);
        lblName.setColor(ColorRGBA.White);

        healthBar = new ProgressBar();
        healthBar.setPreferredSize(new Vector3f(100, 20, 1));
        healthBar.getLabel().setColor(ColorRGBA.White);
    }
    @Getter
    private CharacterModel characterModel;
    @Getter
    private ModelObject modelObject;
    @Getter
    private float height;
    @Getter
    private Label lblName;
    @Getter
    private ProgressBar healthBar;

    private CharacterModel getRandomModel() {
        switch ((int) (Math.random() * 8)) {
            case 0: return new CharacterModel("aland",  "idle", 11.267f, "run", 6);
            case 1: return new CharacterModel("alice", "idle1", 1.867f, "run", 6);
            case 2: return new CharacterModel("dosaz", "idle", 7.417f, "run2", 7);
            case 3: return new CharacterModel("dwarf_warrior", "idle1", 7.875f, "run2", 6);
            case 4: return new CharacterModel("elven_archer", "idle1", 5.1f, "run1", 6);
            case 5: return new CharacterModel("garmon", "idle2", 10, "walk2", 6);
            case 6: return new CharacterModel("scarlet", "idle", 2, "run3", 7);
            case 7: return new CharacterModel("tristan", "idle1", 7.567f, "run1", 7.8f);
        }
        return null;
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

    public void playIdleAnimation() {
        modelObject.playAnimation(characterModel.getIdleAnimationName(), characterModel.getIdleAnimationLoopDuration());
    }

    public void playWalkAnimation(float walkSpeed) {
        modelObject.playAnimation(characterModel.getWalkAnimationName(), (characterModel.getWalkStepDistance() / walkSpeed));
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
