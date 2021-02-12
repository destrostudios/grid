package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.PlayerVisual;
import com.jme3.math.FastMath;
import com.simsilica.lemur.RangedValueModel;

public class HealthAnimation extends Animation {

    private static final double DURATION = 0.5;

    private PlayerVisual playerVisual;
    private int targetHealth;
    private float initialHealth;
    private float progress;

    public HealthAnimation(PlayerVisual playerVisual, int targetHealth) {
        this.playerVisual = playerVisual;
        this.targetHealth = targetHealth;
    }

    @Override
    public void start() {
        super.start();
        RangedValueModel healthBarModel = playerVisual.getHealthBar().getModel();
        initialHealth = (float) healthBarModel.getValue();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        progress += (tpf / DURATION);
        float health = FastMath.interpolateLinear(progress, initialHealth, targetHealth);
        playerVisual.setCurrentHealth(health);
        if (progress >= 1) {
            finish();
        }
    }
}
