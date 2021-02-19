package com.destrostudios.grid.client.appstates;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.client.gameproxy.ClientGameProxy;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.ClientGameData;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameStartClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.jwt.JwtClientModule;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class MenuAppState extends BaseAppState {

    private Node guiNode = new Node();
    private int containerWidth;
    private Container buttonContainerPlayers;
    private Container buttonContainerGames;
    private HashMap<Long, Button> buttonsPlayers = new HashMap<>();
    private HashMap<UUID, Button> buttonsGames = new HashMap<>();
    private LinkedList<Object> tmpButtonKeysToRemove = new LinkedList<>();

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
        label.setColor(ColorRGBA.White);
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
        checkIfJoinedGame();
    }

    private void updatePlayersContainer() {
        JwtClientModule jwtClientModule = mainApplication.getToolsClient().getModule(JwtClientModule.class);
        List<JwtAuthenticationUser> players = jwtClientModule.getOnlineUsers();

        updateButtons(buttonContainerPlayers, buttonsPlayers, players, player -> player.id, player -> player.login, player -> {
            StartGameInfo startGameInfo = new StartGameInfo();
            PlayerInfo player1 = mainApplication.getPlayerInfo();
            PlayerInfo player2 = new PlayerInfo(player.id, player.login);
            LinkedList<PlayerInfo> team1 = new LinkedList<>();
            LinkedList<PlayerInfo> team2 = new LinkedList<>();
            team1.add(player1);
            team2.add(player2);
            startGameInfo.setTeam1(team1);
            startGameInfo.setTeam2(team2);
            String[] mapNames = new String[]{ "island", "desert", "arctic" };
            String mapName = mapNames[(int) (Math.random() * mapNames.length)];
            startGameInfo.setMapName(mapName);

            GameStartClientModule gameStartModule = mainApplication.getToolsClient().getModule(GameStartClientModule.class);
            gameStartModule.startNewGame(startGameInfo);
        });
    }

    private void updateGamesContainer() {
        LobbyClientModule<?> lobbyModule = mainApplication.getToolsClient().getModule(LobbyClientModule.class);
        Set<UUID> games = lobbyModule.getListedGames().keySet();

        GameClientModule<?, ?> gameModule = mainApplication.getToolsClient().getModule(GameClientModule.class);
        updateButtons(buttonContainerGames, buttonsGames, games, Function.identity(), UUID::toString, gameModule::join);
    }

    private <K, O> void updateButtons(Container buttonContainer, HashMap<K, Button> buttons, Collection<O> objects, Function<O, K> getKey, Function<O, String> getText, Consumer<O> action) {
        for (Map.Entry<K, Button> entry : buttons.entrySet()) {
            if (objects.stream().noneMatch(object -> getKey.apply(object) == entry.getKey())) {
                tmpButtonKeysToRemove.add(entry.getKey());
            }
        }
        for (Object key : tmpButtonKeysToRemove) {
            Button button = buttons.remove(key);
            buttonContainer.removeChild(button);
        }
        tmpButtonKeysToRemove.clear();
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
        button.setColor(ColorRGBA.White);
        return button;
    }

    private void checkIfJoinedGame() {
        GameClientModule gameClientModule = mainApplication.getToolsClient().getModule(GameClientModule.class);
        List<ClientGameData<?, ?>> joinedGames = gameClientModule.getJoinedGames();
        if (joinedGames.size() > 0) {
            UUID gameUUID = joinedGames.get(0).getId();
            LobbyClientModule<StartGameInfo> lobbyClientModule = mainApplication.getToolsClient().getModule(LobbyClientModule.class);
            ClientGameProxy clientGameProxy = new ClientGameProxy(gameUUID, mainApplication.getPlayerInfo(), gameClientModule, lobbyClientModule);
            mainApplication.startGame(clientGameProxy);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getGuiNode().detachChild(guiNode);
    }
}
