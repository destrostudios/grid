package com.destrostudios.grid.client;

import com.destrostudios.grid.client.models.ModelObject;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.simsilica.lemur.ProgressBar;
import lombok.Getter;

public class PlayerModel {

    public PlayerModel(AssetManager assetManager) {
        modelObject = getRandomModel(assetManager);

        healthBar = new ProgressBar();
        healthBar.setPreferredSize(new Vector3f(100, 20, 1));
        healthBar.getLabel().setColor(ColorRGBA.White);
    }
    @Getter
    private ModelObject modelObject;
    @Getter
    private ProgressBar healthBar;

    private ModelObject getRandomModel(AssetManager assetManager) {
        switch ((int) (Math.random() * 8)) {
            case 0: return createCharacterModel("aland",  "idle", 11.267f, assetManager);
            case 1: return createCharacterModel("alice", "idle1", 1.867f, assetManager);
            case 2: return createCharacterModel("dosaz", "idle", 7.417f, assetManager);
            case 3: return createCharacterModel("dwarf_warrior", "idle1", 7.875f, assetManager);
            case 4: return createCharacterModel("elven_archer", "idle1", 5.1f, assetManager);
            case 5: return createCharacterModel("garmon", "idle2", 10, assetManager);
            case 6: return createCharacterModel("scarlet", "idle", 2, assetManager);
            case 7: return createCharacterModel("tristan", "idle1", 7.567f, assetManager);
        }
        return null;
    }

    private ModelObject createCharacterModel(String name, String idleAnimationName, float idleAnimationLoopDuration, AssetManager assetManager) {
        ModelObject modelObject = new ModelObject(assetManager, "models/" + name + "/skin_default.xml");
        modelObject.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        modelObject.playAnimation(idleAnimationName, idleAnimationLoopDuration);
        return modelObject;
    }
}
