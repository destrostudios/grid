package com.destrostudios.grid.client.characters;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class PlayerVisualControl extends AbstractControl {

    private PlayerVisual playerVisual;
    private Camera camera;

    public PlayerVisualControl(PlayerVisual playerVisual, Camera camera) {
        this.playerVisual = playerVisual;
        this.camera = camera;
    }

    @Override
    protected void controlUpdate(float tpf) {
        playerVisual.updateAnimation();
        playerVisual.updateGuiControlPositions(camera);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
