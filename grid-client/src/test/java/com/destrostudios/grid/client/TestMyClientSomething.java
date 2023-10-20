package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class TestMyClientSomething {

    private MyClientSomething myClientSomething;
    private MySharedSomething mySharedSomethingMock = mock(MySharedSomething.class);

    @BeforeEach
    public void setup() {
        myClientSomething = new MyClientSomething(mySharedSomethingMock);
    }

    @Test
    public void fieldAssigned() {
        assertNotNull(myClientSomething.getMySharedSomething());
    }
}
