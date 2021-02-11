package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.NewEvent;
import com.destrostudios.grid.eventbus.handler.NewEventHandler;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Queues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public class NewEventbus {

    private final ArrayBlockingQueue<NewEvent> eventQueue;

    private final Multimap<Class<?>, NewEventHandler<NewEvent>> preHandlers;
    private final Multimap<Class<?>, NewEventHandler<NewEvent>> instantHandlers;
    private final Multimap<Class<?>, NewEventHandler<NewEvent>> resolvedHandlers;

    private final Supplier<EntityWorld> entityWorldSupplier;

    public NewEventbus(Supplier<EntityWorld> entityWorldSupplier) {
        this.entityWorldSupplier = entityWorldSupplier;
        this.eventQueue = Queues.newArrayBlockingQueue(1000);
        this.preHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.instantHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.resolvedHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
    }

    public boolean eventsInQueue() {
        return !eventQueue.isEmpty();
    }

    public void addEvent(NewEvent e) {
        eventQueue.add(e);
    }

    public void triggerNextEvent() {
        NewEvent event = eventQueue.poll();
        if (event != null) {
            triggerEvent(event);
        }
    }

    public void triggerAllEvents() {
        while (!eventQueue.isEmpty()) {
            triggerNextEvent();
        }
    }

    private <E extends NewEvent> void triggerEvent(E e) {
        for (NewEventHandler<NewEvent> ev : preHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
        for (NewEventHandler<NewEvent> ev : instantHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
        for (NewEventHandler<NewEvent> ev : resolvedHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
    }

    public <E extends NewEvent> void addInstantHandler(Class<E> eventClass, NewEventHandler<E> handler) {
        this.instantHandlers.put(eventClass, (NewEventHandler<NewEvent>) handler);
    }

    public <E extends NewEvent> void removeInstantHandler(Class<E> eventClass, NewEventHandler<E> handler) {
        this.instantHandlers.remove(eventClass, handler);
    }

    public <E extends NewEvent> void addPreHandler(Class<E> eventClass, NewEventHandler<E> handler) {
        this.preHandlers.put(eventClass, (NewEventHandler<NewEvent>) handler);
    }

    public <E extends NewEvent> void addResolveHandler(Class<E> eventClass, NewEventHandler<E> handler) {
        this.resolvedHandlers.put(eventClass, (NewEventHandler<NewEvent>) handler);
    }


}
