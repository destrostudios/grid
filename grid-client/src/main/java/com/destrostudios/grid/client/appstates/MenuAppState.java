package com.destrostudios.grid.client.appstates;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.client.ClientApplication;
import com.destrostudios.grid.client.gameproxy.ClientGameProxy;
import com.destrostudios.grid.client.gui.GuiColors;
import com.destrostudios.grid.shared.Characters;
import com.destrostudios.grid.shared.Maps;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.grid.shared.StartGameInfo;
import com.destrostudios.turnbasedgametools.network.client.modules.game.ClientGameData;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.GameStartClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.game.LobbyClientModule;
import com.destrostudios.turnbasedgametools.network.client.modules.jwt.JwtClientModule;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MenuAppState extends BaseAppState<ClientApplication> {

    private int buttonHeight = 40;

    private LinkedList<JwtAuthenticationUser> lobbyPlayers;
    private StartGameInfo startGameInfo;
    private PlayerInfo ownPlayerInfo;
    private Node guiNode = new Node();
    private int containerWidth;
    private Container buttonContainerPlayers;
    private Container buttonContainerGames;
    private HashMap<Long, Button> buttonsPlayers = new HashMap<>();
    private HashMap<UUID, Button> buttonsGames = new HashMap<>();
    private LinkedList<Object> tmpButtonKeysToRemove = new LinkedList<>();
    private Button btnStartLobbyGame;

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        initializeLobby();

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

        btnStartLobbyGame = createButton();
        btnStartLobbyGame.setText("");
        btnStartLobbyGame.setLocalTranslation(new Vector3f(containerX1 + 1, containerY - containerHeight + 1 + buttonHeight, 1));
        btnStartLobbyGame.addCommands(Button.ButtonAction.Up, source -> startLobbyGame());
        guiNode.attachChild(btnStartLobbyGame);

        int containerButtonY = (containerY - 40);
        buttonContainerPlayers = addSectionButtonContainer(containerX1, containerButtonY);
        buttonContainerGames = addSectionButtonContainer(containerX2, containerButtonY);

        int characterAndMapContainerY = (containerMarginOutside - 25);
        addCharacterContainer(containerMarginOutside, characterAndMapContainerY);
        addMapContainer(characterAndMapContainerY, (totalWidth - containerMarginOutside));

        mainApplication.getGuiNode().attachChild(guiNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(new Vector3f(0, 10, 0));
        camera.setRotation(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y));
    }

    private void initializeLobby() {
        JwtAuthenticationUser ownJwtAuthenticationUser = mainApplication.getJwtAuthenticationUser();
        lobbyPlayers = new LinkedList<>();
        lobbyPlayers.add(ownJwtAuthenticationUser);

        startGameInfo = new StartGameInfo();
        startGameInfo.setTeam1(new LinkedList<>());
        startGameInfo.setTeam2(new LinkedList<>());
        startGameInfo.setMapName(Maps.getRandomMapName());

        ownPlayerInfo = new PlayerInfo(ownJwtAuthenticationUser.id, ownJwtAuthenticationUser.login, Characters.getRandomCharacterName());
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

    private Container addSectionButtonContainer(int containerX, int y) {
        Container buttonContainer = new Container();
        buttonContainer.setLocalTranslation(containerX + 1, y, 0);
        buttonContainer.setBackground(null);
        guiNode.attachChild(buttonContainer);
        return buttonContainer;
    }

    public void addCharacterContainer(int containerX, int containerY) {
        Container characterContainer = addSelectionContainer(
            "Character", HAlignment.Left, Characters.CHARACTER_NAMES,
            characterName -> "textures/characters/" + characterName + ".png", false,
            () -> ownPlayerInfo.getCharacterName(), characterName -> ownPlayerInfo.setCharacterName(characterName)
        );
        characterContainer.setLocalTranslation(containerX, containerY, 0);
        guiNode.attachChild(characterContainer);
    }

    public void addMapContainer(int containerY, int containerWidth) {
        Container mapContainerWrapper = new Container();
        mapContainerWrapper.setLayout(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.None));
        mapContainerWrapper.setLocalTranslation(0, containerY, 0);
        mapContainerWrapper.setPreferredSize(new Vector3f(containerWidth, 0, 0));
        mapContainerWrapper.setBackground(null);

        Panel placeholder = new Panel();
        placeholder.setBackground(null);
        mapContainerWrapper.addChild(placeholder);

        Container mapContainer = addSelectionContainer(
            "Map", HAlignment.Right, Maps.MAP_NAMES,
            mapName -> "textures/maps/" + mapName + ".png", true,
            () -> startGameInfo.getMapName(), mapName -> startGameInfo.setMapName(mapName)
        );
        mapContainerWrapper.addChild(mapContainer);

        guiNode.attachChild(mapContainerWrapper);
    }

    private Container addSelectionContainer(String title, HAlignment titleHAlignment, String[] names, Function<String, String> getIconPath, boolean nearestMagFilter, Supplier<String> getSelectedName, Consumer<String> setSelectedName) {
        Container container = new Container();
        container.setBackground(null);

        Label lblTitle = new Label("");
        lblTitle.setPreferredSize(new Vector3f(200, 40, 0));
        float insetLeft = 0;
        float insetRight = 0;
        if (titleHAlignment == HAlignment.Left) {
            insetLeft = 10;
        } else {
            insetRight = 10;
        }
        lblTitle.setInsets(new Insets3f(0, insetLeft, 0, insetRight));
        lblTitle.setTextHAlignment(titleHAlignment);
        lblTitle.setTextVAlignment(VAlignment.Center);
        lblTitle.setFontSize(20);
        lblTitle.setColor(ColorRGBA.White);
        container.addChild(lblTitle);

        Runnable updateVisualSelection = () -> {
            String name = getSelectedName.get();
            lblTitle.setText(title + ": " + name);
        };

        int iconSize = 80;
        Container iconsRow = new Container();
        iconsRow.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
        for (String name : names) {
            Button button = new Button("");
            IconComponent icon = new IconComponent(getIconPath.apply(name));
            icon.setIconSize(new Vector2f(iconSize, iconSize));
            icon.setHAlignment(HAlignment.Center);
            icon.setVAlignment(VAlignment.Center);
            if (nearestMagFilter) {
                icon.getImageTexture().setMagFilter(Texture.MagFilter.Nearest);
            }
            button.setBackground(icon);
            button.addCommands(Button.ButtonAction.Up, source -> {
                setSelectedName.accept(name);
                updateVisualSelection.run();
            });
            iconsRow.addChild(button);
        }
        container.addChild(iconsRow);

        updateVisualSelection.run();

        return container;
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

        updateButtons(
            buttonContainerPlayers,
            buttonsPlayers,
            players,
            player -> player.id,
            player -> player.login,
            player -> lobbyPlayers.contains(player),
            player -> {
                if (player.id != ownPlayerInfo.getId()) {
                    if (lobbyPlayers.contains(player)) {
                        lobbyPlayers.remove(player);
                    } else {
                        lobbyPlayers.add(player);
                    }
                }
            },
            playerId -> lobbyPlayers.removeIf(lobbyPlayer -> lobbyPlayer.id == playerId)
        );

        boolean isLobbyBigEnough = (lobbyPlayers.size() > 1);
        btnStartLobbyGame.setEnabled(isLobbyBigEnough);
        btnStartLobbyGame.setText(isLobbyBigEnough ? "Start Game" : "Select at least 2 players to start a game");
        markButtonAsActive(btnStartLobbyGame, isLobbyBigEnough);
    }

    private void updateGamesContainer() {
        LobbyClientModule<StartGameInfo> lobbyClientModule = getLobbyClientModule();
        Set<UUID> games = lobbyClientModule.getListedGames().keySet();

        updateButtons(
            buttonContainerGames,
            buttonsGames,
            games,
            Function.identity(),
            gameUUID -> {
                StartGameInfo startGameInfo = lobbyClientModule.getListedGames().get(gameUUID);
                return startGameInfo.getTeam1().get(0).getLogin() + " vs " + startGameInfo.getTeam2().get(0).getLogin();
            },
            gameUUID -> false,
            gameUUID -> getGameClientModule().join(gameUUID),
            gameUUID -> {}
        );
    }

    private <K, O> void updateButtons(
        Container buttonContainer,
        HashMap<K, Button> buttons,
        Collection<O> objects,
        Function<O, K> getKey,
        Function<O, String> getText,
        Function<O, Boolean> isActive,
        Consumer<O> action,
        Consumer<K> onRemove
    ) {
        for (Map.Entry<K, Button> entry : buttons.entrySet()) {
            if (objects.stream().noneMatch(object -> getKey.apply(object) == entry.getKey())) {
                tmpButtonKeysToRemove.add(entry.getKey());
            }
        }
        for (Object key : tmpButtonKeysToRemove) {
            Button button = buttons.remove(key);
            buttonContainer.removeChild(button);
            onRemove.accept((K) key);
        }
        tmpButtonKeysToRemove.clear();
        for (O object : objects) {
            K key = getKey.apply(object);
            Button button = buttons.get(key);
            if (button == null) {
                button = createButton();
                button.setText(getText.apply(object));
                button.addCommands(Button.ButtonAction.Up, source -> {
                    action.accept(object);
                });
                buttonContainer.addChild(button);
                buttons.put(key, button);
            }
            markButtonAsActive(button, isActive.apply(object));
        }
    }

    private Button createButton() {
        Button button = new Button("");
        button.setPreferredSize(new Vector3f(containerWidth - 2, buttonHeight, 0));
        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);
        button.setFontSize(20);
        button.setColor(ColorRGBA.White);
        return button;
    }

    private void markButtonAsActive(Button button, boolean isActive) {
        ColorRGBA backgroundColor = (isActive ? GuiColors.COLOR_ACTIVE : GuiColors.COLOR_DEFAULT);
        TbtQuadBackgroundComponent buttonBackground = (TbtQuadBackgroundComponent) button.getBackground();
        if (!buttonBackground.getColor().equals(backgroundColor)) {
            buttonBackground.setColor(backgroundColor);
        }
    }

    private void startLobbyGame() {
        boolean isTeam1Or2 = true;
        for (JwtAuthenticationUser lobbyPlayer : lobbyPlayers) {
            PlayerInfo playerInfo;
            if (lobbyPlayer == mainApplication.getJwtAuthenticationUser()) {
                playerInfo = ownPlayerInfo;
            } else {
                playerInfo = new PlayerInfo(lobbyPlayer.id, lobbyPlayer.login, Characters.getRandomCharacterName());
            }
            if (isTeam1Or2) {
                startGameInfo.getTeam1().add(playerInfo);
            } else {
                startGameInfo.getTeam2().add(playerInfo);
            }
            isTeam1Or2 = (!isTeam1Or2);
        }

        GameStartClientModule<StartGameInfo> gameStartModule = mainApplication.getToolsClient().getModule(GameStartClientModule.class);
        gameStartModule.startNewGame(startGameInfo);
    }

    private void checkIfJoinedGame() {
        GameClientModule gameClientModule = getGameClientModule();
        List<ClientGameData<?, ?>> joinedGames = gameClientModule.getJoinedGames();
        if (joinedGames.size() > 0) {
            UUID gameUUID = joinedGames.get(0).getId();
            ClientGameProxy clientGameProxy = new ClientGameProxy(gameUUID, mainApplication.getJwtAuthenticationUser(), gameClientModule, getLobbyClientModule());
            mainApplication.startGame(clientGameProxy);
        }
    }

    private GameClientModule<?, ?> getGameClientModule() {
        return mainApplication.getToolsClient().getModule(GameClientModule.class);
    }

    private LobbyClientModule<StartGameInfo> getLobbyClientModule() {
        return mainApplication.getToolsClient().getModule(LobbyClientModule.class);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getGuiNode().detachChild(guiNode);
    }
}
