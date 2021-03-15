package com.destrostudios.grid.client;

import com.destrostudios.authtoken.JwtAuthenticationUser;
import com.destrostudios.grid.client.appstates.GameAppState;
import com.destrostudios.grid.client.appstates.GameGuiAppState;
import com.destrostudios.grid.client.appstates.MapAppState;
import com.destrostudios.grid.client.appstates.MenuAppState;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import lombok.Getter;

public class ClientApplication extends BaseApplication {

    @Getter
    private final ToolsClient toolsClient;
    @Getter
    private final JwtAuthenticationUser jwtAuthenticationUser;

    public ClientApplication(ToolsClient toolsClient, JwtAuthenticationUser jwtAuthenticationUser) {
        this.toolsClient = toolsClient;
        this.jwtAuthenticationUser = jwtAuthenticationUser;
    }

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        stateManager.attach(new MenuAppState());
    }

    public void startGame(GameProxy gameProxy) {
        stateManager.detach(stateManager.getState(MenuAppState.class));
        stateManager.attach(new MapAppState(gameProxy.getStartGameInfo().getMapName(), gameProxy.getGame().getData(), gameProxy.getPlayerEntity()));
        stateManager.attach(new GameGuiAppState());
        stateManager.attach(new GameAppState(gameProxy));
    }

    public void closeGame() {
        stateManager.detach(stateManager.getState(MapAppState.class));
        stateManager.detach(stateManager.getState(GameGuiAppState.class));
        stateManager.detach(stateManager.getState(GameAppState.class));
        stateManager.attach(new MenuAppState());
    }
}
