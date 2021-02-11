package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Stack;
import java.util.function.Supplier;

public class Eventbus {

    private final Stack<Event> eventStack;

    private final Multimap<Class<?>, EventHandler<Event>> preHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> instantHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> resolvedHandlers;

    private final Supplier<EntityWorld> entityWorldSupplier;

    public Eventbus(Supplier<EntityWorld> entityWorldSupplier) {
        this.entityWorldSupplier = entityWorldSupplier;
        this.eventStack = new Stack<>();
        this.preHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.instantHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.resolvedHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
    }

    public boolean eventsInQueue() {
        return !eventStack.isEmpty();
    }

    public void addEvent(Event e) {
        eventStack.push(e);
    }

    public void triggerNextEvent() {
        Event event = eventStack.pop();
        if (event != null) {
            triggerEvent(event);
        }
    }

    public void triggerAllEvents() {
        while (!eventStack.isEmpty()) {
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

    public void addInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public void removeInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.remove(eventClass, handler);
    }

    public <E extends Event> void addPreHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.preHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public <E extends Event> void addResolveHandler(Class<E> eventClass, EventHandler<E> handler) {
        this.resolvedHandlers.put(eventClass, (EventHandler<Event>) handler);
    }


}
