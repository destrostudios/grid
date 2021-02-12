package com.destrostudios.grid.eventbus;

import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.handler.EventHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class Eventbus {

    private final Multimap<Class<?>, EventHandler<? extends Event>> preHandlers;
    private final Multimap<Class<?>, EventHandler<? extends Event>> instantHandlers;
    private final Multimap<Class<?>, EventHandler<? extends Event>> resolvedHandlers;

    private final Deque<TriggeredEventHandler> triggeredHandlers;

    private final Supplier<EntityWorld> entityWorldSupplier;

    public Eventbus(Supplier<EntityWorld> entityWorldSupplier) {
        this.entityWorldSupplier = entityWorldSupplier;
        this.preHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.instantHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.resolvedHandlers = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.triggeredHandlers = new LinkedList<>();
    }

    private void calculateHandlerForEvent(Event e, boolean isSubevent) {
        List<TriggeredEventHandler> handler = new ArrayList<>();
        handler.addAll(calculateHandlerForEvent(e, preHandlers));
        handler.addAll(calculateHandlerForEvent(e, instantHandlers));
        handler.addAll(calculateHandlerForEvent(e, resolvedHandlers));

        if (!isSubevent) {
            // add on tail of Dequeue, if main event
            triggeredHandlers.addAll(handler);
        } else {
            // add on head in reversed order, if sub event
            for (TriggeredEventHandler triggeredEventHandler : Lists.reverse(handler)) {
                triggeredHandlers.addFirst(triggeredEventHandler);
            }
        }
    }

    /**
     * Register initial / main events
     *
     * @param events
     */
    public void registerMainEvents(Event... events) {
        for (Event event : events) {
            calculateHandlerForEvent(event, false);
        }
    }

    /**
     * Register subevents, fired from another event
     *
     * @param subevents
     */
    public void registerSubEvents(Event... subevents) {
        for (Event event : Lists.reverse(List.of(subevents))) {
            calculateHandlerForEvent(event, true);
        }
    }

    private <E extends Event> List<TriggeredEventHandler> calculateHandlerForEvent(E e, Multimap<Class<?>, EventHandler<? extends Event>> handlers) {
        List<TriggeredEventHandler> handler = new ArrayList<>();
        for (Class<?> eventClass : handlers.keySet()) {
            if (eventClass.isAssignableFrom(e.getClass())) {
                for (EventHandler<? extends Event> ev : handlers.get(eventClass)) {
                    handler.add(new TriggeredEventHandler(e, ev));
                }
            }
        }
        return handler;
    }

    public boolean triggeredHandlersInQueue() {
        return !triggeredHandlers.isEmpty();
    }

    public void triggerNextHandler() {
        TriggeredEventHandler triggeredEventHandler = triggeredHandlers.pollFirst();
        triggeredEventHandler.getEventHandler().onEvent(triggeredEventHandler.getEvent(), entityWorldSupplier);
    }

    public void addInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.put(eventClass, handler);
    }

    public void removeInstantHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.instantHandlers.remove(eventClass, handler);
    }

    public void addPreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.preHandlers.put(eventClass, handler);
    }

    public void removePreHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.preHandlers.remove(eventClass, handler);
    }

    public void addResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.resolvedHandlers.put(eventClass, handler);
    }

    public void removeResolvedHandler(Class<? extends Event> eventClass, EventHandler<? extends Event> handler) {
        this.resolvedHandlers.remove(eventClass, handler);
    }
}
