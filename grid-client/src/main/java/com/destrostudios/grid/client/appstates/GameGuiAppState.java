package com.destrostudios.grid.client.appstates;

import com.destrostudios.grid.client.ClientApplication;
import com.destrostudios.grid.client.gui.GuiColors;
import com.destrostudios.grid.client.gui.GuiNextPlayer;
import com.destrostudios.grid.client.gui.GuiSpell;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;

import java.util.List;

public class GameGuiAppState extends BaseAppState<ClientApplication> {

    public static final int DISPLAYED_NEXT_PLAYERS = 5;
    private static final ColorRGBA COLOR_SPELL_DISABLED = new ColorRGBA(0.3f, 0.3f, 0.3f, 1);
    private static final ColorRGBA COLOR_SPELL_TARGETING = new ColorRGBA(1.75f, 1.75f, 1.75f, 1);

    private int topElementsMargin = 30;
    private int nextPlayersInnerHeight;
    private int barMarginX = 100;
    private int barMarginBottom = 50;
    private int barHeight = 80;
    private int barY = (barMarginBottom + barHeight);
    private int leftAndRightPartWidth = 250;

    private int totalWidth;
    private int totalHeight;

    private Node guiNode;

    private Label lblActivePlayerName;
    private Label lblActivePlayerMP;
    private Label lblActivePlayerAP;

    private Panel[] pansNextPlayerIcon;
    private Label[] lblsNextPlayerName;

    private Node currentPlayerNode;

    private Label lblOwnPlayerHealth;
    private Label lblOwnPlayerMP;
    private Label lblOwnPlayerAP;

    private Container containerTooltip;
    private Label lblTooltip;

    private Container containerGameOver;
    private Label lblGameOver;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);

        AppSettings appSettings = mainApplication.getContext().getSettings();
        totalWidth = appSettings.getWidth();
        totalHeight = appSettings.getHeight();

        guiNode = new Node();

        Container containerLabels = new Container();
        containerLabels.setLocalTranslation(topElementsMargin, totalHeight - topElementsMargin, 0);
        TbtQuadBackgroundComponent containerLabelsBackground = (TbtQuadBackgroundComponent) containerLabels.getBackground();
        containerLabelsBackground.setMargin(20, 10);
        lblActivePlayerName = new Label("");
        lblActivePlayerName.setFontSize(20);
        lblActivePlayerName.setColor(ColorRGBA.White);
        containerLabels.addChild(lblActivePlayerName);
        lblActivePlayerMP = new Label("");
        lblActivePlayerMP.setFontSize(20);
        lblActivePlayerMP.setColor(ColorRGBA.White);
        containerLabels.addChild(lblActivePlayerMP);
        lblActivePlayerAP = new Label("");
        lblActivePlayerAP.setFontSize(20);
        lblActivePlayerAP.setColor(ColorRGBA.White);
        containerLabels.addChild(lblActivePlayerAP);
        guiNode.attachChild(containerLabels);

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
        lblTooltip.setColor(ColorRGBA.White);
        containerTooltip.addChild(lblTooltip);

        createContainersNextPlayers();

        currentPlayerNode = new Node();
        guiNode.attachChild(currentPlayerNode);

        int gameOverInsetsX = 400;
        int gameOverInsetsBorder = 200;
        int gameOverInsetsMiddle = 0;
        containerGameOver = new Container();
        containerGameOver.setLocalTranslation(0, totalHeight, 999);
        containerGameOver.setPreferredSize(new Vector3f(totalWidth, totalHeight, 0));
        TbtQuadBackgroundComponent containerGameOverBackground = (TbtQuadBackgroundComponent) containerGameOver.getBackground();
        containerGameOverBackground.setColor(new ColorRGBA(0, 0, 0, 0.8f));
        lblGameOver = new Label("");
        lblGameOver.setInsets(new Insets3f(gameOverInsetsBorder, gameOverInsetsX, gameOverInsetsMiddle, gameOverInsetsX));
        lblGameOver.setTextHAlignment(HAlignment.Center);
        lblGameOver.setTextVAlignment(VAlignment.Center);
        lblGameOver.setFontSize(40);
        lblGameOver.setColor(ColorRGBA.White);
        containerGameOver.addChild(lblGameOver);
        Button btnBackToMenu = new Button("Continue");
        btnBackToMenu.setInsets(new Insets3f(gameOverInsetsMiddle, gameOverInsetsX, gameOverInsetsBorder, gameOverInsetsX));
        btnBackToMenu.setTextHAlignment(HAlignment.Center);
        btnBackToMenu.setTextVAlignment(VAlignment.Center);
        btnBackToMenu.setFontSize(40);
        btnBackToMenu.setColor(ColorRGBA.White);
        btnBackToMenu.addCommands(Button.ButtonAction.Up, source -> mainApplication.closeGame());
        containerGameOver.addChild(btnBackToMenu);

        mainApplication.getGuiNode().attachChild(guiNode);
    }

    private void createContainersNextPlayers() {
        int containerWidth = 220;
        int containerHeight = 50;
        int containerPadding = 2;
        int iconAndNameGap = 7;
        int arrowIconWidth = 15;
        int arrowIconHeight = 10;
        int arrowIconMargin = 2;
        nextPlayersInnerHeight = (containerHeight - (2 * containerPadding));
        int containerX = (totalWidth - topElementsMargin - containerWidth);
        int containerY = (totalHeight - topElementsMargin);
        int playerIconX = (containerX + containerPadding);
        int playerNameX = (playerIconX + nextPlayersInnerHeight + iconAndNameGap);
        int playerNameWidth = (containerWidth - containerPadding - nextPlayersInnerHeight - iconAndNameGap - containerPadding);
        int arrowIconX = (containerX + (containerWidth / 2) - (arrowIconWidth / 2));
        pansNextPlayerIcon = new Panel[DISPLAYED_NEXT_PLAYERS];
        lblsNextPlayerName = new Label[DISPLAYED_NEXT_PLAYERS];
        for (int i = 0; i < DISPLAYED_NEXT_PLAYERS; i++) {
            int innerY = (containerY - containerPadding);
            int arrowIconY = (containerY - containerHeight - arrowIconMargin);

            Container containerNextPlayer = new Container();
            containerNextPlayer.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
            containerNextPlayer.setLocalTranslation(containerX, containerY, 0);
            containerNextPlayer.setPreferredSize(new Vector3f(containerWidth, containerHeight, 0));
            if (i == 0) {
                TbtQuadBackgroundComponent containerBackground = (TbtQuadBackgroundComponent) containerNextPlayer.getBackground();
                containerBackground.setColor(GuiColors.COLOR_ACTIVE);
            }
            guiNode.attachChild(containerNextPlayer);

            Panel panPlayerIcon = new Panel();
            panPlayerIcon.setLocalTranslation(playerIconX, innerY, 1);
            guiNode.attachChild(panPlayerIcon);
            pansNextPlayerIcon[i] = panPlayerIcon;

            Label lblPlayerName = new Label("");
            lblPlayerName.setLocalTranslation(playerNameX, innerY, 1);
            lblPlayerName.setPreferredSize(new Vector3f(playerNameWidth, nextPlayersInnerHeight, 0));
            lblPlayerName.setTextVAlignment(VAlignment.Center);
            lblPlayerName.setFontSize(16);
            lblPlayerName.setColor(ColorRGBA.White);
            guiNode.attachChild(lblPlayerName);
            lblsNextPlayerName[i] = lblPlayerName;

            Panel panArrowIcon = new Panel();
            panArrowIcon.setLocalTranslation(arrowIconX, arrowIconY, 1);
            IconComponent arrowIcon = new IconComponent("textures/arrow_down.png");
            arrowIcon.setIconSize(new Vector2f(arrowIconWidth, arrowIconHeight));
            panArrowIcon.setBackground(arrowIcon);
            guiNode.attachChild(panArrowIcon);

            containerY -= (containerHeight + arrowIconMargin + arrowIconHeight + arrowIconMargin);
        }

        Container containerRest = new Container();
        containerRest.setLocalTranslation(containerX, containerY, 0);
        containerRest.setPreferredSize(new Vector3f(containerWidth, 30, 0));

        Label lblRest = new Label("...");
        lblRest.setInsets(new Insets3f(-5, 0, 0, 0));
        lblRest.setTextHAlignment(HAlignment.Center);
        lblRest.setTextVAlignment(VAlignment.Center);
        lblRest.setFontSize(16);
        lblRest.setColor(ColorRGBA.White);
        containerRest.addChild(lblRest);

        guiNode.attachChild(containerRest);
    }

    public void setNextPlayers(GuiNextPlayer[] nextPlayers) {
        for (int i = 0; i < nextPlayers.length; i++) {
            IconComponent icon = new IconComponent("textures/characters/" + nextPlayers[i].getCharacterName() + ".png");
            icon.setIconSize(new Vector2f(nextPlayersInnerHeight, nextPlayersInnerHeight));
            icon.setVAlignment(VAlignment.Center);
            pansNextPlayerIcon[i].setBackground(icon);
            lblsNextPlayerName[i].setText(nextPlayers[i].getPlayerName());
        }
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
        lblOwnPlayerHealth.setColor(ColorRGBA.White);
        containerAttributes.addChild(lblOwnPlayerHealth);

        Container containerBottom = new Container();
        containerBottom.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        containerBottom.setBackground(null);

        lblOwnPlayerMP = new Label("");
        lblOwnPlayerMP.setTextHAlignment(HAlignment.Center);
        lblOwnPlayerMP.setTextVAlignment(VAlignment.Center);
        lblOwnPlayerMP.setFontSize(20);
        lblOwnPlayerMP.setColor(ColorRGBA.White);
        containerBottom.addChild(lblOwnPlayerMP);

        lblOwnPlayerAP = new Label("");
        lblOwnPlayerAP.setTextHAlignment(HAlignment.Center);
        lblOwnPlayerAP.setTextVAlignment(VAlignment.Center);
        lblOwnPlayerAP.setFontSize(20);
        lblOwnPlayerAP.setColor(ColorRGBA.White);
        containerBottom.addChild(lblOwnPlayerAP);

        containerAttributes.addChild(containerBottom);

        currentPlayerNode.attachChild(containerAttributes);
    }

    public void createSpellButtons(List<GuiSpell> spells) {
        int spellsContainerWidth = (totalWidth - (2 * leftAndRightPartWidth) - (2 * barMarginX));
        int spellsBackgroundX = (barMarginX + leftAndRightPartWidth);
        int iconSize = (barHeight - 4);

        Container spellsBackground = new Container();
        spellsBackground.setLocalTranslation(spellsBackgroundX, barY, 0);
        spellsBackground.setPreferredSize(new Vector3f(spellsContainerWidth, barHeight, 0));
        currentPlayerNode.attachChild(spellsBackground);

        int buttonX = spellsBackgroundX;
        for (GuiSpell spell : spells) {
            Button button = new Button("");
            button.setLocalTranslation(buttonX, barY, 0);
            button.setPreferredSize(new Vector3f(barHeight, barHeight, 0));
            button.setTextHAlignment(HAlignment.Center);
            button.setTextVAlignment(VAlignment.Center);
            button.setFontSize(20);
            button.setColor(ColorRGBA.White);
            IconComponent icon = new IconComponent("textures/spells/" + spell.getName() + ".png");
            icon.setIconSize(new Vector2f(iconSize, iconSize));
            icon.setHAlignment(HAlignment.Center);
            icon.setVAlignment(VAlignment.Center);
            button.setBackground(icon);
            button.addCommands(Button.ButtonAction.HighlightOn, source -> showTooltip(spell.getName() + ": " + spell.getTooltip()));
            button.addCommands(Button.ButtonAction.HighlightOff, source -> hideTooltip());
            boolean isButtonEnabled = true;
            if (spell.getRemainingCooldown() != null) {
                button.setText("" + spell.getRemainingCooldown());
                isButtonEnabled = false;
            } else if (!spell.isCastable()) {
                isButtonEnabled = false;
            }
            if (isButtonEnabled) {
                button.addCommands(Button.ButtonAction.Up, source -> spell.getCast().run());
                if (spell.isTargeting()) {
                    icon.setColor(COLOR_SPELL_TARGETING);
                }
            } else {
                icon.setColor(COLOR_SPELL_DISABLED);
            }
            currentPlayerNode.attachChild(button);
            buttonX += (barHeight - 2);
        }
    }

    public void showTooltip(String text) {
        lblTooltip.setText(text);
        guiNode.attachChild(containerTooltip);
    }

    public void hideTooltip() {
        guiNode.detachChild(containerTooltip);
    }

    public void createEndTurnButton(Runnable endTurn) {
        Container rightContainer = new Container();
        rightContainer.setLocalTranslation(totalWidth - barMarginX - leftAndRightPartWidth, barY, 0);
        rightContainer.setPreferredSize(new Vector3f(leftAndRightPartWidth, barHeight, 0));

        Button endTurnButton = new Button("End turn");
        endTurnButton.setTextHAlignment(HAlignment.Center);
        endTurnButton.setTextVAlignment(VAlignment.Center);
        endTurnButton.setFontSize(20);
        endTurnButton.setColor(ColorRGBA.White);
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

    public void onGameOver(String winner) {
        lblGameOver.setText("Game over - Winner: " + winner);
        guiNode.attachChild(containerGameOver);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getGuiNode().detachChild(guiNode);
    }
}
