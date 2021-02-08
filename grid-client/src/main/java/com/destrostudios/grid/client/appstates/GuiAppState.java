package com.destrostudios.grid.client.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.style.BaseStyles;

public class GuiAppState extends BaseAppState {

    private Label lblCurrentPlayer;
    private Label lblMP;
    private Label lblAP;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        GuiGlobals.initialize(mainApplication);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        int totalWidth = mainApplication.getContext().getSettings().getWidth();
        int totalHeight = mainApplication.getContext().getSettings().getHeight();

        // Labels

        int labelsMargin = 30;

        Container containerLabels = new Container();
        containerLabels.setLocalTranslation(labelsMargin, totalHeight - labelsMargin, 0);
        TbtQuadBackgroundComponent containerLabelsBackground = (TbtQuadBackgroundComponent) containerLabels.getBackground();
        containerLabelsBackground.setMargin(20, 10);
        lblCurrentPlayer = new Label("");
        lblCurrentPlayer.setFontSize(20);
        containerLabels.addChild(lblCurrentPlayer);
        lblMP = new Label("");
        lblMP.setFontSize(20);
        containerLabels.addChild(lblMP);
        lblAP = new Label("");
        lblAP.setFontSize(20);
        containerLabels.addChild(lblAP);
        mainApplication.getGuiNode().attachChild(containerLabels);

        // Buttons

        int buttonsMarginLeft = 100;
        int buttonsMarginBottom = 50;
        int buttonContainerHeight = 80;
        int buttonCount = 10;

        Container containerButtons = new Container();
        containerButtons.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        containerButtons.setLocalTranslation(buttonsMarginLeft, buttonsMarginBottom + buttonContainerHeight, 0);
        containerButtons.setPreferredSize(new Vector3f(totalWidth - (2 * buttonsMarginLeft), buttonContainerHeight, 0));
        for (int i = 0; i < buttonCount; i++) {
            Button button = new Button("" + (i + 1));
            button.setTextHAlignment(HAlignment.Center);
            button.setTextVAlignment(VAlignment.Center);
            button.setFontSize(20);
            int buttonIndex = i;
            button.addCommands(Button.ButtonAction.Up, source -> {
                getAppState(GameAppState.class).onButtonClicked(buttonIndex);
            });
            containerButtons.addChild(button);
        }
        mainApplication.getGuiNode().attachChild(containerButtons);
    }

    public void setCurrentPlayer(String name) {
        lblCurrentPlayer.setText("Current player: " + name);
    }

    public void setMP(int mp) {
        lblMP.setText("Movement Points: " + mp);
    }

    public void setAP(int ap) {
        lblAP.setText("Action Points: " + ap);
    }
}
