package com.destrostudios.grid.client.appstates;

import com.destrostudios.grid.shared.Util;
import com.jme3.math.*;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingsMenuAppState extends MenuAppState {

    private int buttonHeight = 50;
    private HashMap<Button, Integer> selectedIndices = new HashMap<>();

    @Override
    public void recreateMenu() {
        super.recreateMenu();
        addTitle("Settings");

        JmeContext context = mainApplication.getContext();
        AppSettings settings = context.getSettings();

        float buttonY = (totalHeight - 200);
        addButton_Boolean(buttonY, "Fullscreen", settings.isFullscreen(), isFullscreen -> {
            settings.setFullscreen(isFullscreen);
            context.restart();
        });
        buttonY -= buttonHeight;
        addButton_IntArray(
            buttonY,
            new int[][] {
                new int[] { 1280, 720 },
                new int[] { 1600, 900 },
                new int[] { 1920, 1080 }
            },
            new int[] { settings.getWidth(), settings.getHeight() },
            values -> "Resolution: " + values[0] + " x " + values[1],
            values -> {
                settings.setWidth(values[0]);
                settings.setHeight(values[1]);
                context.restart();
                recreateMenus();
            }
        );
        buttonY -= buttonHeight;
        addButton_Boolean(buttonY, "VSync", settings.isVSync(), vSync -> {
            settings.setVSync(vSync);
            context.restart();
        });

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

    private void addButton_Boolean(float buttonY, String text, boolean defaultValue, Consumer<Boolean> apply) {
        addButton(buttonY, new Boolean[] { true, false }, (defaultValue ? 0 : 1), value -> text + ": " + (value ? "Yes" : "No"), apply);
    }

    private void addButton_IntArray(float buttonY, int[][] values, int[] defaultValue, Function<int[], String> getText, Consumer<int[]> apply) {
        addButton(buttonY, values, Util.getIndexOfEquals(values, defaultValue), getText, apply);
    }

    private <T> void addButton(float buttonY, T[] values, int defaultIndex, Function<T, String> getText, Consumer<T> apply) {
        int buttonWidth = 400;
        int buttonX = ((totalWidth / 2) - (buttonWidth / 2));
        Button button = new Button(getText.apply(values[defaultIndex]));
        button.setLocalTranslation(buttonX, buttonY, 0);
        button.setPreferredSize(new Vector3f(buttonWidth, buttonHeight, 0));
        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);
        button.setFontSize(20);
        button.setColor(ColorRGBA.White);
        button.addCommands(Button.ButtonAction.Up, source -> {
            int newSelectedIndex = ((selectedIndices.get(button) + 1) % values.length);
            T newValue = values[newSelectedIndex];
            apply.accept(newValue);
            selectedIndices.put(button, newSelectedIndex);
            button.setText(getText.apply(newValue));
        });
        selectedIndices.put(button, defaultIndex);
        guiNode.attachChild(button);
    }

    private void recreateMenus() {
        getAppState(MainMenuAppState.class).recreateMenu();
        recreateMenu();
    }
}
