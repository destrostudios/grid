package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import lombok.Getter;

public class MyClient {

    @Getter
    private MySharedSomething mySharedSomething;

    public MyClient(MySharedSomething mySharedSomething) {
        this.mySharedSomething = mySharedSomething;
    }
}
