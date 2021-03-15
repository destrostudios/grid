package com.destrostudios.grid.client.animations;

import com.destrostudios.grid.client.characters.EntityVisual;
import com.jme3.math.FastMath;
import com.simsilica.lemur.RangedValueModel;

public class HealthAnimation extends Animation {

    private static final double DURATION = 0.5;

    private EntityVisual entityVisual;
    private int targetHealth;
    private float initialHealth;
    private float progress;

    public HealthAnimation(EntityVisual entityVisual, int targetHealth) {
        this.entityVisual = entityVisual;
        this.targetHealth = targetHealth;
    }

    @Override
    public void start() {
        super.start();
        RangedValueModel healthBarModel = entityVisual.getHealthBar().getModel();
        initialHealth = (float) healthBarModel.getValue();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        progress += (tpf / DURATION);
        float health = FastMath.interpolateLinear(progress, initialHealth, targetHealth);
        entityVisual.setCurrentHealth(health);
        if (progress >= 1) {
            finish();
        }
    }
}
