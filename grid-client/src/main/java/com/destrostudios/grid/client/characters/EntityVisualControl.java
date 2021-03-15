package com.destrostudios.grid.client.characters;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class EntityVisualControl extends AbstractControl {

    private EntityVisual entityVisual;
    private Camera camera;

    public EntityVisualControl(EntityVisual entityVisual, Camera camera) {
        this.entityVisual = entityVisual;
        this.camera = camera;
    }

    @Override
    protected void controlUpdate(float tpf) {
        entityVisual.updateAnimation();
        entityVisual.updateGuiControlPositions(camera);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
}
