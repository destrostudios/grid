package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import lombok.Getter;

public class MyClientSomething {

    @Getter
    private MySharedSomething mySharedSomething;

    public MyClientSomething(MySharedSomething mySharedSomething) {
        this.mySharedSomething = mySharedSomething;
    }
}
