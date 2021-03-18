package com.destrostudios.grid.client.appstates;

import com.destrostudios.grid.client.ClientApplication;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;

abstract class MenuAppState extends BaseAppState<ClientApplication> {

    public MenuAppState() {
        guiNode = new Node();
    }
    protected Node guiNode;
    protected int totalWidth;
    protected int totalHeight;
    protected boolean autoEnabled;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        totalWidth = mainApplication.getContext().getSettings().getWidth();
        totalHeight = mainApplication.getContext().getSettings().getHeight();
        initMenu();
        setEnabled(autoEnabled);
    }

    protected abstract void initMenu();

    protected void addTitle(String title) {
        int titleMarginTop = 70;
        int titleWidth = 300;
        Label lblTitle = new Label(title);
        lblTitle.setFontSize(32);
        lblTitle.setLocalTranslation(new Vector3f((totalWidth / 2f) - (titleWidth / 2f), totalHeight - titleMarginTop, 0));
        lblTitle.setPreferredSize(new Vector3f(titleWidth, 0, 0));
        lblTitle.setTextHAlignment(HAlignment.Center);
        lblTitle.setColor(ColorRGBA.White);
        guiNode.attachChild(lblTitle);
    }

    protected void openMenu(Class<? extends MenuAppState> menuAppStateClass) {
        close();
        getAppState(menuAppStateClass).setEnabled(true);
    }

    public void close() {
        setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mainApplication != null) {
            onEnabled(enabled);
        } else if (!enabled) {
            autoEnabled = false;
        }
    }

    protected void onEnabled(boolean enabled) {
        if (enabled) {
            mainApplication.getGuiNode().attachChild(guiNode);
        } else {
            mainApplication.getGuiNode().detachChild(guiNode);
        }
    }
}
