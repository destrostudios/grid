package com.destrostudios.grid.client.animations;

import lombok.Getter;

public abstract class Animation {

    @Getter
    private boolean finished;

    public void start() {

    }

    public void update(float tpf) {

    }

    protected void finish() {
        finished = true;
    }

    public void end() {

    }
}
