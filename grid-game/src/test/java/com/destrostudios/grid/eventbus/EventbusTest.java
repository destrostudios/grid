package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class EventbusTest {
    private Eventbus eventbus;

    @BeforeEach
    void before() {
        EntityWorld entityWorld = Mockito.mock(EntityWorld.class);
        eventbus = new Eventbus(() -> entityWorld);
    }

    @Test
    @DisplayName("Testing the order of how the handlers get executed")
    void testEventQueue() {
        AtomicReference<String> eventTracker = new AtomicReference<>("");

        eventbus.addInstantHandler(SimpleEvent.class, (EventHandler<SimpleEvent>) (event, entityWorldSupplier) -> {
            eventTracker.set(event.getName());
            System.out.println(event.getName());
            if (event.name.equals("A")) {
                eventbus.registerSubEvents(new SimpleEvent("A1"), new SimpleEvent("A2"), new SimpleEvent("A3"));
            } else if (event.name.equals("B")) {
                eventbus.registerSubEvents(new SimpleEvent("B1"), new SimpleEvent("B2"));
            } else if (event.name.equals("C")) {
                eventbus.registerSubEvents(new SimpleEvent("C1"));
            } else if (event.name.equals("A2")) {
                eventbus.registerSubEvents(new SimpleEvent("A21"), new SimpleEvent("A22"));
            }
        });
        eventbus.registerMainEvents(new SimpleEvent("A"));
        eventbus.registerMainEvents(new SimpleEvent("B"));
        eventbus.registerMainEvents(new SimpleEvent("C"));

        eventbus.triggerNextHandler();
        Assertions.assertEquals("A", eventTracker.get(), "Should be A");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("A1", eventTracker.get(), "Should be A1");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("A2", eventTracker.get(), "Should be A2");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("A21", eventTracker.get(), "Should be A21");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("A22", eventTracker.get(), "Should be A22");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("A3", eventTracker.get(), "Should be A3");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("B", eventTracker.get(), "Should be B");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("B1", eventTracker.get(), "Should be B1");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("B2", eventTracker.get(), "Should be B2");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("C", eventTracker.get(), "Should be C");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("C1", eventTracker.get(), "Should be C1");
    }


    @Test
    @DisplayName("Testing triggering the handlers in the queue")
    void triggeredHandlersInQueue() {
        AtomicReference<String> eventTracker = new AtomicReference<>("");

        EventHandler<Event> preHandler = (event, entityWorldSupplier) -> {
            eventTracker.set("Pre");
        };
        EventHandler<Event> instantHandler = (event, entityWorldSupplier) -> {
            eventTracker.set("Instant");
        };
        EventHandler<Event> resolvedHandler = (event, entityWorldSupplier) -> {
            eventTracker.set("Resolved");
        };

        eventbus.addPreHandler(SimpleEvent.class, preHandler);
        eventbus.addPreHandler(SimpleEvent.class, instantHandler);
        eventbus.addPreHandler(SimpleEvent.class, resolvedHandler);
        eventbus.registerMainEvents(new SimpleEvent(""));

        eventbus.triggerNextHandler();
        Assertions.assertEquals("Pre", eventTracker.get(), "Should be Pre");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("Instant", eventTracker.get(), "Should be Instant");
        eventbus.triggerNextHandler();
        Assertions.assertEquals("Resolved", eventTracker.get(), "Should be Resolved");


    }

    @Test
    @DisplayName("Testing execution of 2 event, where one gets invalid after the other event")
    public void testEventRemovedWhenTriggering() {
        AtomicInteger valueTracker = new AtomicInteger(0);

        eventbus.addInstantHandler(SimpleValueEvent.class, (EventHandler<SimpleValueEvent>) (event, entityWorldSupplier) -> {
            valueTracker.addAndGet(event.getValue());
        });

        eventbus.addEventValidator(SimpleValueEvent.class, (EventValidator<SimpleValueEvent>) (event, supplier) -> valueTracker.get() < 5);

        SimpleValueEvent eventA = new SimpleValueEvent("A", 5);
        SimpleValueEvent eventB = new SimpleValueEvent("B", 6);

        eventbus.registerMainEvents(eventA, eventB);
        while (eventbus.triggeredHandlersInQueue()) {
            eventbus.triggerNextHandler();
        }

        Assertions.assertEquals(eventA.getValue(), valueTracker.get(), "Event B should not get execuuted, as its invalid" +
                " after executing event A");
    }

    @AllArgsConstructor
    @Getter
    private static class SimpleValueEvent implements Event {
        private final String name;
        private final int value;
    }

    @AllArgsConstructor
    @Getter
    private static class SimpleEvent implements Event {
        private final String name;
    }
}