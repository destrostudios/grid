package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class Eventbus {

    private final Multimap<Class<?>, EventHandler<Event>> preHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> instantHandlers;
    private final Multimap<Class<?>, EventHandler<Event>> resolvedHandlers;

    private Queue<TriggeredEventHandler> triggeredHandlers;

    private final Supplier<EntityWorld> entityWorldSupplier;

    public Eventbus(Supplier<EntityWorld> entityWorldSupplier) {
        this.entityWorldSupplier = entityWorldSupplier;
        this.preHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.instantHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.resolvedHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        triggeredHandlers = new LinkedList<>();
    }

    public void triggerEvent(Event e) {
        triggerEvent(e, preHandlers);
        triggerEvent(e, instantHandlers);
        triggerEvent(e, resolvedHandlers);
    }

    private <E extends Event> void triggerEvent(E e, Multimap<Class<?>, EventHandler<Event>> handlers) {
        for (Class<?> eventClass : handlers.keySet()) {
            if (eventClass.isAssignableFrom(e.getClass())) {
                for (EventHandler<Event> ev : handlers.get(eventClass)) {
                    triggeredHandlers.add(new TriggeredEventHandler(e, ev));
                }
            }
        }
    }

    public boolean triggeredHandlersInQueue() {
        return !triggeredHandlers.isEmpty();
    }

    public void triggerNextHandler() {
        TriggeredEventHandler triggeredEventHandler = triggeredHandlers.poll();
        triggeredEventHandler.getEventHandler().onEvent(triggeredEventHandler.getEvent(), entityWorldSupplier);
    }

    public void addInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public void removeInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.remove(eventClass, handler);
    }

    public void addPreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.preHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.preHandlers.remove(eventClass, handler);
    }

    public void addResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.resolvedHandlers.put(eventClass, (EventHandler<Event>) handler);
    }

    public void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.resolvedHandlers.remove(eventClass, handler);
    }
}
