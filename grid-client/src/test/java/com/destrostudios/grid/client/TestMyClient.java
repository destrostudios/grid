package com.destrostudios.grid.client;

import com.destrostudios.grid.shared.MySharedSomething;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestMyClient{

    private MyClient myClient;
    private MySharedSomething mySharedSomethingMock = mock(MySharedSomething.class);

    @Before
    public void setup() {
        myClient = new MyClient(mySharedSomethingMock);
    }

    @Test
    public void fieldAssigned() {
        assertNotNull(myClient.getMySharedSomething());
    }
}
