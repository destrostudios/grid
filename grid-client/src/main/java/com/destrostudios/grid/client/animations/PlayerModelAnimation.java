package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.ModelAnimationInfo;
import com.destrostudios.grid.client.characters.PlayerVisual;

public class PlayerModelAnimation extends ModelAnimation {

    private PlayerVisual playerVisual;

    public PlayerModelAnimation(PlayerVisual playerVisual, ModelAnimationInfo modelAnimationInfo) {
        super(playerVisual.getModelObject(), modelAnimationInfo);
        this.playerVisual = playerVisual;
    }

    @Override
    public void end() {
        super.end();
        playerVisual.playIdleAnimation();
    }
}
