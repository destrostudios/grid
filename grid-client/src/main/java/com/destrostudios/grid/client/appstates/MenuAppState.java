package com.destrostudios.grid.client.appstates;

import com.destrostudios.grid.shared.PlayerInfo;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MenuAppState extends BaseAppState {

    private Node guiNode = new Node();
    private int containerWidth;
    private Container buttonContainerPlayers;
    private Container buttonContainerGames;
    private HashMap<Integer, Button> buttonsPlayers = new HashMap<>();
    private HashMap<String, Button> buttonsGames = new HashMap<>();
    private LinkedList<Button> tmpButtonsToRemove = new LinkedList<>();

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);

        AppSettings appSettings = mainApplication.getContext().getSettings();
        int totalWidth = appSettings.getWidth();
        int totalHeight = appSettings.getHeight();

        int titleMarginTop = 70;
        int titleWidth = 300;
        Label lblTitle = new Label("Grid");
        lblTitle.setFontSize(32);
        lblTitle.setLocalTranslation(new Vector3f((totalWidth / 2f) - (titleWidth / 2f), totalHeight - titleMarginTop, 0));
        lblTitle.setPreferredSize(new Vector3f(titleWidth, 0, 0));
        lblTitle.setTextHAlignment(HAlignment.Center);
        lblTitle.setColor(ColorRGBA.White);
        guiNode.attachChild(lblTitle);

        int containerMarginOutside = 200;
        int containerMarginBetween = 100;
        int containerY = (totalHeight - containerMarginOutside);
        containerWidth = ((totalWidth - (2 * containerMarginOutside) - containerMarginBetween) / 2);
        int containerHeight = (containerY - containerMarginOutside);
        int containerX1 = containerMarginOutside;
        int containerX2 = (containerMarginOutside + containerWidth + containerMarginBetween);

        addSectionContainer("Players", containerX1, containerY, containerWidth, containerHeight);
        addSectionContainer("Games", containerX2, containerY, containerWidth, containerHeight);

        int containerButtonY = (containerY - 40);
        buttonContainerPlayers = addButtonContainer(containerX1, containerButtonY);
        buttonContainerGames = addButtonContainer(containerX2, containerButtonY);

        mainApplication.getGuiNode().attachChild(guiNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(0, 10, 0));
        camera.setRotation(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y));
    }

    private void addSectionContainer(String title, int containerX, int containerY, int containerWidth, int containerHeight) {
        int y = containerY;
        Container container = new Container();
        container.setLocalTranslation(containerX, y, 0);
        container.setPreferredSize(new Vector3f(containerWidth, containerHeight, 0));

        y -= 10;
        Label label = new Label(title);
        label.setLocalTranslation(new Vector3f(containerX, y, 1));
        label.setPreferredSize(new Vector3f(containerWidth, 20, 0));
        label.setInsets(new Insets3f(0, 10, 0, 0));
        label.setTextVAlignment(VAlignment.Center);
        label.setFontSize(20);
        guiNode.attachChild(label);

        guiNode.attachChild(container);
    }

    private Container addButtonContainer(int containerX, int y) {
        Container buttonContainer = new Container();
        buttonContainer.setLocalTranslation(containerX + 1, y, 0);
        guiNode.attachChild(buttonContainer);
        return buttonContainer;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        updatePlayersContainer();
        updateGamesContainer();
    }

    private void updatePlayersContainer() {
        // TODO: Get from tools
        List<PlayerInfo> players = new LinkedList<>();
        players.add(new PlayerInfo(1, "destroflyer"));
        players.add(new PlayerInfo(2, "Etherblood"));
        players.add(new PlayerInfo(3, "Icecold"));

        updateButtons(buttonContainerPlayers, buttonsPlayers, players, PlayerInfo::getId, PlayerInfo::getLogin, player -> {
            System.out.println("Start game with player #" + player.getId());
            startGame();
        });
    }

    private void updateGamesContainer() {
        // TODO: Get from tools
        List<String> games = new LinkedList<>();
        games.add("egoVidiTe vs Yalee");
        games.add("TeMMeZz vs ellimak1");

        updateButtons(buttonContainerGames, buttonsGames, games, Function.identity(), Function.identity(), game -> {
            System.out.println("Spectate game '" + game + "'");
            startGame();
        });
    }

    private void startGame() {
        mainApplication.getStateManager().detach(this);
        mainApplication.startGame();
    }

    private <K, O> void updateButtons(Container buttonContainer, HashMap<K, Button> buttons, List<O> objects, Function<O, K> getKey, Function<O, String> getText, Consumer<O> action) {
        for (Map.Entry<K, Button> entry : buttons.entrySet()) {
            if (objects.stream().noneMatch(object -> getKey.apply(object) == entry.getKey())) {
                tmpButtonsToRemove.add(entry.getValue());
            }
        }
        for (Button button : tmpButtonsToRemove) {
            buttonContainerPlayers.removeChild(button);
        }
        tmpButtonsToRemove.clear();
        for (O object : objects) {
            K key = getKey.apply(object);
            if (!buttons.containsKey(key)) {
                Button button = createButton();
                button.setText(getText.apply(object));
                button.addCommands(Button.ButtonAction.Up, source -> {
                    action.accept(object);
                });
                buttonContainer.addChild(button);
                buttons.put(key, button);
            }
        }
    }

    private Button createButton() {
        Button button = new Button("");
        button.setPreferredSize(new Vector3f(containerWidth - 4, 40, 0));
        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);
        button.setFontSize(20);
        return button;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getGuiNode().detachChild(guiNode);
    }
}
