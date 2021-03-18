package com.destrostudios.grid.client.appstates;

import com.jme3.math.*;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;

public class SettingsMenuAppState extends MenuAppState {

    @Override
    protected void initMenu() {
        int titleMarginTop = 70;
        int titleWidth = 300;
        Label lblTitle = new Label("Settings");
        lblTitle.setFontSize(32);
        lblTitle.setLocalTranslation(new Vector3f((totalWidth / 2f) - (titleWidth / 2f), totalHeight - titleMarginTop, 0));
        lblTitle.setPreferredSize(new Vector3f(titleWidth, 0, 0));
        lblTitle.setTextHAlignment(HAlignment.Center);
        lblTitle.setColor(ColorRGBA.White);
        guiNode.attachChild(lblTitle);

        int backMargin = 20;
        int backIconSize = 30;
        Button btnBack = new Button("");
        btnBack.setLocalTranslation(backMargin, backMargin + backIconSize, 0);
        btnBack.setPreferredSize(new Vector3f(backIconSize, backIconSize, 0));
        IconComponent iconSettings = new IconComponent("textures/back.png");
        iconSettings.setIconSize(new Vector2f(backIconSize, backIconSize));
        btnBack.setBackground(iconSettings);
        btnBack.addCommands(Button.ButtonAction.Up, source -> openMenu(MainMenuAppState.class));
        guiNode.attachChild(btnBack);
    }
}
