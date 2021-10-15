package com.destrostudios.grid.client.replay;

import com.destrostudios.grid.actions.Action;

public class ActionReplay {
    public Action action;
    public int[] randomHistory;

    public ActionReplay() {
    }

    public ActionReplay(Action action, int[] randomHistory) {
        this.action = action;
        this.randomHistory = randomHistory;
    }
}
