package com.destrostudios.grid.client.appstates;

import com.destrostudios.grid.client.gui.GuiSpell;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;

import java.util.List;

public class GameGuiAppState extends BaseAppState {

    private int barMarginX = 100;
    private int barMarginBottom = 50;
    private int barHeight = 80;
    private int barY = (barMarginBottom + barHeight);
    private int leftAndRightPartWidth = 250;

    private int totalWidth;
    private int totalHeight;

    private Label lblActivePlayerName;
    private Label lblActivePlayerMP;
    private Label lblActivePlayerAP;
    private Node currentPlayerNode;

    private Label lblOwnPlayerHealth;
    private Label lblOwnPlayerMP;
    private Label lblOwnPlayerAP;

    private Container containerTooltip;
    private Label lblTooltip;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);

        AppSettings appSettings = mainApplication.getContext().getSettings();
        totalWidth = appSettings.getWidth();
        totalHeight = appSettings.getHeight();

        int labelsMargin = 30;
        Container containerLabels = new Container();
        containerLabels.setLocalTranslation(labelsMargin, totalHeight - labelsMargin, 0);
        TbtQuadBackgroundComponent containerLabelsBackground = (TbtQuadBackgroundComponent) containerLabels.getBackground();
        containerLabelsBackground.setMargin(20, 10);
        lblActivePlayerName = new Label("");
        lblActivePlayerName.setFontSize(20);
        containerLabels.addChild(lblActivePlayerName);
        lblActivePlayerMP = new Label("");
        lblActivePlayerMP.setFontSize(20);
        containerLabels.addChild(lblActivePlayerMP);
        lblActivePlayerAP = new Label("");
        lblActivePlayerAP.setFontSize(20);
        containerLabels.addChild(lblActivePlayerAP);
        mainApplication.getGuiNode().attachChild(containerLabels);

        int containerTooltipWidth = (totalWidth - (2 * leftAndRightPartWidth) - (2 * barMarginX));
        int containerTooltipHeight = barHeight;
        int containerTooltipX = (barMarginX + leftAndRightPartWidth);
        int containerTooltipY = (barY + containerTooltipHeight);
        containerTooltip = new Container();
        containerTooltip.setLocalTranslation(containerTooltipX, containerTooltipY, 0);
        containerTooltip.setPreferredSize(new Vector3f(containerTooltipWidth, containerTooltipHeight, 0));
        lblTooltip = new Label("");
        lblTooltip.setTextHAlignment(HAlignment.Center);
        lblTooltip.setTextVAlignment(VAlignment.Center);
        lblTooltip.setFontSize(20);
        containerTooltip.addChild(lblTooltip);

        currentPlayerNode = new Node();
        mainApplication.getGuiNode().attachChild(currentPlayerNode);
    }

    public void removeAllCurrentPlayerElements() {
        currentPlayerNode.detachAllChildren();
    }

    public void createAttributes() {
        Container containerAttributes = new Container();
        containerAttributes.setLocalTranslation(barMarginX, barY, 0);
        containerAttributes.setPreferredSize(new Vector3f(leftAndRightPartWidth, barHeight, 0));

        lblOwnPlayerHealth = new Label("");
        lblOwnPlayerHealth.setTextHAlignment(HAlignment.Center);
        lblOwnPlayerHealth.setTextVAlignment(VAlignment.Center);
        lblOwnPlayerHealth.setFontSize(20);
        containerAttributes.addChild(lblOwnPlayerHealth);

        Container containerBottom = new Container();
        containerBottom.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        containerBottom.setBackground(null);

        lblOwnPlayerMP = new Label("");
        lblOwnPlayerMP.setTextHAlignment(HAlignment.Center);
        lblOwnPlayerMP.setTextVAlignment(VAlignment.Center);
        lblOwnPlayerMP.setFontSize(20);
        containerBottom.addChild(lblOwnPlayerMP);

        lblOwnPlayerAP = new Label("");
        lblOwnPlayerAP.setTextHAlignment(HAlignment.Center);
        lblOwnPlayerAP.setTextVAlignment(VAlignment.Center);
        lblOwnPlayerAP.setFontSize(20);
        containerBottom.addChild(lblOwnPlayerAP);

        containerAttributes.addChild(containerBottom);

        currentPlayerNode.attachChild(containerAttributes);
    }

    public void createSpellButtons(List<GuiSpell> spells) {
        int spellsContainerWidth = (totalWidth - (2 * leftAndRightPartWidth) - (2 * barMarginX));
        int spellsContainerX = (barMarginX + leftAndRightPartWidth);

        Container spellsContainer = new Container();
        spellsContainer.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        spellsContainer.setLocalTranslation(spellsContainerX, barY, 0);
        spellsContainer.setPreferredSize(new Vector3f(spellsContainerWidth, barHeight, 0));
        for (GuiSpell spell : spells) {
            Button button = new Button(spell.getName());
            button.setTextHAlignment(HAlignment.Center);
            button.setTextVAlignment(VAlignment.Center);
            button.setFontSize(20);
            button.addCommands(Button.ButtonAction.Up, source -> spell.getCast().run());
            button.addCommands(Button.ButtonAction.HighlightOn, source -> showTooltip(spell.getTooltip()));
            button.addCommands(Button.ButtonAction.HighlightOff, source -> hideTooltip());
            spellsContainer.addChild(button);
        }
        currentPlayerNode.attachChild(spellsContainer);
    }

    public void showTooltip(String text) {
        lblTooltip.setText(text);
        mainApplication.getGuiNode().attachChild(containerTooltip);
    }

    public void hideTooltip() {
        mainApplication.getGuiNode().detachChild(containerTooltip);
    }

    public void createEndTurnButton(Runnable endTurn) {
        Container rightContainer = new Container();
        rightContainer.setLocalTranslation(totalWidth - barMarginX - leftAndRightPartWidth, barY, 0);
        rightContainer.setPreferredSize(new Vector3f(leftAndRightPartWidth, barHeight, 0));

        Button endTurnButton = new Button("End turn");
        endTurnButton.setTextHAlignment(HAlignment.Center);
        endTurnButton.setTextVAlignment(VAlignment.Center);
        endTurnButton.setFontSize(20);
        endTurnButton.addCommands(Button.ButtonAction.Up, source -> endTurn.run());
        rightContainer.addChild(endTurnButton);

        currentPlayerNode.attachChild(rightContainer);
    }

    public void setActivePlayerName(String name) {
        lblActivePlayerName.setText("Current player: " + name);
    }

    public void setActivePlayerMP(int mp) {
        lblActivePlayerMP.setText("Movement Points: " + mp);
    }

    public void setActivePlayerAP(int ap) {
        lblActivePlayerAP.setText("Action Points: " + ap);
    }

    public void setOwnPlayerHealth(int currentHealth, int maximumHealth) {
        lblOwnPlayerHealth.setText("Health: " + currentHealth + " / " + maximumHealth);
    }

    public void setOwnPlayerMP(int mp) {
        lblOwnPlayerMP.setText("MP: " + mp);
    }

    public void setOwnPlayerAP(int ap) {
        lblOwnPlayerAP.setText("AP: " + ap);
    }
}
