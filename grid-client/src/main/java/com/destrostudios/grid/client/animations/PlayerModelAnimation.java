package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.BlockingAnimation;
import com.destrostudios.grid.client.characters.PlayerVisual;

public class PlayerModelAnimation extends ModelAnimation {

    private PlayerVisual playerVisual;

    public PlayerModelAnimation(PlayerVisual playerVisual, BlockingAnimation blockingAnimation) {
        super(playerVisual.getModelObject(), blockingAnimation);
        this.playerVisual = playerVisual;
    }

    @Override
    public void end() {
        super.end();
        playerVisual.playIdleAnimation();
    }
}
