package com.destrostudios.grid.client.animations;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;

public class AnnouncementAnimation extends Animation {

    private static final float DURATION_FADE_IN = 0.05f;
    private static final float DURATION_MIDDLE = 1;
    private static final float DURATION_FADE_OUT = 0.1f;

    private SimpleApplication application;
    private String message;
    private float passedTime;
    private Container container;

    public AnnouncementAnimation(SimpleApplication application, String message) {
        this.application = application;
        this.message = message;
    }

    @Override
    public void start() {
        super.start();
        container = new Container();
        int totalWidth = application.getContext().getSettings().getWidth();
        int totalHeight = application.getContext().getSettings().getHeight();
        int containerHeight = 150;
        int bottomUsedSpaceHeight = 130;
        float containerY = (bottomUsedSpaceHeight + ((totalHeight - bottomUsedSpaceHeight) / 2f) + (containerHeight / 2f));
        container.setPreferredSize(new Vector3f(totalWidth, containerHeight, 0));
        container.setLocalTranslation(new Vector3f(0, containerY, 0));
        Label label = new Label(message);
        label.setTextHAlignment(HAlignment.Center);
        label.setTextVAlignment(VAlignment.Center);
        label.setFontSize(30);
        label.setColor(ColorRGBA.White);
        container.addChild(label);
        application.getGuiNode().attachChild(container);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        passedTime += tpf;
        float alpha;
        if (passedTime < DURATION_FADE_IN) {
            alpha = (passedTime / DURATION_FADE_IN);
        } else if (passedTime < (DURATION_FADE_IN + DURATION_MIDDLE)) {
            alpha = 1;
        } else {
            alpha = (1 - ((passedTime - DURATION_FADE_IN - DURATION_MIDDLE) / DURATION_FADE_OUT));
        }
        container.setAlpha(alpha);
        if (passedTime > (DURATION_FADE_IN + DURATION_MIDDLE + DURATION_FADE_OUT)) {
            finish();
        }
    }

    @Override
    public void end() {
        super.end();
        application.getGuiNode().detachChild(container);
    }
}
