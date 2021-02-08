package com.destrostudios.grid.client.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.BaseStyles;

public class GuiAppState extends BaseAppState {

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        GuiGlobals.initialize(mainApplication);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        int marginLeft = 100;
        int marginBottom = 50;
        int containerHeight = 80;
        int buttonCount = 10;
        int totalWidth = mainApplication.getContext().getSettings().getWidth();

        Container container = new Container();
        container.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        container.setLocalTranslation(marginLeft, marginBottom + containerHeight, 0);
        container.setPreferredSize(new Vector3f(totalWidth - (2 * marginLeft), containerHeight, 0));
        for (int i = 0; i < buttonCount; i++) {
            Button button = new Button("" + (i + 1));
            button.setTextHAlignment(HAlignment.Center);
            button.setTextVAlignment(VAlignment.Center);
            button.setFontSize(20);
            int buttonIndex = i;
            button.addCommands(Button.ButtonAction.Up, source -> {
                getAppState(GameAppState.class).onButtonClicked(buttonIndex);
            });
            container.addChild(button);
        }
        mainApplication.getGuiNode().attachChild(container);
    }
}
