package com.destrostudios.grid.client.animations;

import lombok.Getter;

public abstract class Animation {

    @Getter
    private boolean blocking;
    @Getter
    private boolean finished;

    public void start() {
        blocking = true;
    }

    public void update(float tpf) {

    }

    protected void finish() {
        unblock();
        finished = true;
    }

    protected void unblock() {
        blocking = false;
    }

    public void end() {

    }
}
