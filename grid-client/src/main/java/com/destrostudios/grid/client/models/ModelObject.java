package com.destrostudios.grid.client.models;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;

public class ModelObject extends Node {

    // Should later probably be read from a settings file
    public static boolean HARDWARE_SKINNING = true;

    public ModelObject(AssetManager assetManager, String skinPath) {
        this.assetManager = assetManager;
        skin = ModelSkin.get(skinPath);
        loadAndRegisterModel();
    }
    private AssetManager assetManager;
    private ModelSkin skin;
    private ArrayList<RegisteredModel> registeredModels = new ArrayList<>();

    public RegisteredModel loadAndRegisterModel() {
        Node node = skin.load(assetManager);
        RegisteredModel registeredModel = new RegisteredModel(node);
        registeredModels.add(registeredModel);
        registeredModel.initialize(this);
        attachChild(node);
        for (ModelModifier modelModifier : skin.getModelModifiers()) {
            modelModifier.modify(registeredModel, assetManager);
        }
        return registeredModel;
    }

    public void unregisterModel(Spatial spatial) {
        for (int i = 0; i < registeredModels.size(); i++) {
            RegisteredModel registeredModel = registeredModels.get(i);
            if (registeredModel.getNode() == spatial) {
                registeredModels.remove(i);
                detachChild(spatial);
                break;
            }
        }
    }

    public void playAnimation(String animationName, float loopDuration) {
        playAnimation(animationName, loopDuration, true);
    }

    public void playAnimation(String animationName, float loopDuration, boolean restartIfAlreadySet) {
        playAnimation(animationName, loopDuration, restartIfAlreadySet, true);
    }

    public void playAnimation(String animationName, float loopDuration, boolean restartIfAlreadySet, boolean isLoop) {
        setAnimationName(animationName, restartIfAlreadySet);
        setAnimationProperties(loopDuration, isLoop);
    }

    public void setAnimationName(String animationName, boolean restartIfAlreadySet) {
        registeredModels.forEach(registeredModel -> registeredModel.setAnimationName(animationName, restartIfAlreadySet));
    }

    public void setAnimationProperties(float loopDuration, boolean isLoop) {
        registeredModels.forEach(registeredModel -> registeredModel.setAnimationProperties(loopDuration, isLoop));
    }

    public void stopAndRewindAnimation() {
        registeredModels.forEach(RegisteredModel::stopAndRewindAnimation);
    }

    public ModelSkin getSkin() {
        return skin;
    }

    public RegisteredModel getOriginalRegisteredModel() {
        return registeredModels.get(0);
    }

    public Spatial getModelNode() {
        return getOriginalRegisteredModel().getNode();
    }
}
