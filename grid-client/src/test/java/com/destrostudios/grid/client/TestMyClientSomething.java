package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestMyClientSomething {

    private MyClientSomething myClientSomething;
    private MySharedSomething mySharedSomethingMock = mock(MySharedSomething.class);

    @Before
    public void setup() {
        myClientSomething = new MyClientSomething(mySharedSomethingMock);
    }

    @Test
    public void fieldAssigned() {
        assertNotNull(myClientSomething.getMySharedSomething());
    }
}
