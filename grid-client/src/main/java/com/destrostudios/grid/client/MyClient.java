package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import lombok.Getter;

public class MyClient {

    public static void main(String[] args) {
        System.out.println("Client.");
    }

    @Getter
    private MySharedSomething mySharedSomething;

    public MyClient(MySharedSomething mySharedSomething) {
        this.mySharedSomething = mySharedSomething;
    }
}
