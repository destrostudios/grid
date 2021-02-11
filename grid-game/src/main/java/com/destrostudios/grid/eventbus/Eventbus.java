package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Queues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public class Eventbus {

    private final ArrayBlockingQueue<Event> eventQueue;

    private final Multimap<Class<?>, EventHandler<Event>> preHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> instantHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> resolvedHandlers;

    private final Supplier<EntityWorld> entityWorldSupplier;

    public Eventbus(Supplier<EntityWorld> entityWorldSupplier) {
        this.entityWorldSupplier = entityWorldSupplier;
        this.eventQueue = Queues.newArrayBlockingQueue(1000);
        this.preHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.instantHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.resolvedHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
    }

    public boolean eventsInQueue() {
        return !eventQueue.isEmpty();
    }

    public void addEvent(Event e) {
        eventQueue.add(e);
    }

    public void triggerNextEvent() {
        Event event = eventQueue.poll();
        if (event != null) {
            triggerEvent(event);
        }
    }

    public void triggerAllEvents() {
        while (!eventQueue.isEmpty()) {
            triggerNextEvent();
        }
    }

    private <E extends Event> void triggerEvent(E e) {
        for (EventHandler<Event> ev : preHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
        for (EventHandler<Event> ev : instantHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
        for (EventHandler<Event> ev : resolvedHandlers.get(e.getClass())) {
            ev.onEvent(e, entityWorldSupplier);
        }
    }

    public <E extends Event> void addInstantHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.instantHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public <E extends Event> void removeInstantHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.instantHandlers.remove(eventClass, handler);
    }

    public <E extends Event> void addPreHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.preHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public <E extends Event> void addResolveHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.resolvedHandlers.put(eventClass, (EventHandler<Event>) handler);
    }


}
