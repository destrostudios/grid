package com.destrostudios.grid.client.replay;

import com.destrostudios.grid.shared.StartGameInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameReplay {
    private StartGameInfo startGameInfo;
    private String initial;
    private List<ActionReplay> actions;

    GameReplay() {
    }

    public GameReplay(StartGameInfo startGameInfo, String initial) {
        this.startGameInfo = startGameInfo;
        this.initial = initial;
        this.actions = new ArrayList<>();
    }

    public StartGameInfo getStartGameInfo() {
        return startGameInfo;
    }

    public String getInitial() {
        return initial;
    }

    public List<ActionReplay> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void append(ActionReplay action) {
        actions.add(action);
    }
}
