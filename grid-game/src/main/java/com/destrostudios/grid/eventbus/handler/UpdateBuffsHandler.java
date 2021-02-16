package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.BuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.*;
import com.destrostudios.grid.eventbus.events.properties.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class UpdateBuffsHandler implements EventHandler<UpdateBuffsEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(UpdateBuffsEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld world = entityWorldSupplier.get();
        int entity = event.getEntity();

        BuffsComponent buffs = world.getComponent(entity, BuffsComponent.class).orElse(new BuffsComponent());

        List<Integer> newBuffs = new ArrayList<>(buffs.getBuffEntities());

        Optional<Integer> deltaAP = Optional.empty();
        Optional<Integer> deltaMP = Optional.empty();
        Optional<Integer> deltaHP = Optional.empty();

        for (int buff : buffs.getBuffEntities()) {
            deltaMP = handleBuff(world, buff, MovementPointBuffComponent.class);
            deltaAP = handleBuff(world, buff, AttackPointsBuffComponent.class);
            deltaHP = handleBuff(world, buff, HealthPointBuffComponent.class);

            if (!world.hasComponents(buff, AttackPointsBuffComponent.class) && !world.hasComponents(buff, MovementPointBuffComponent.class)
                    && !world.hasComponents(buff, HealthPointBuffComponent.class)) {

                newBuffs.remove(buff);
                world.removeEntity(buff);
            }
        }
        world.addComponent(entity, new BuffsComponent(newBuffs));
        eventbus.registerSubEvents(getSubEvents(world, entity, deltaAP, deltaMP, deltaHP));
    }

    private List<Event> getSubEvents(EntityWorld world, int entity, Optional<Integer> deltaAP, Optional<Integer> deltaMP, Optional<Integer> deltaHP) {
        List<Event> subEvents = new ArrayList<>();
        if (deltaAP.isPresent()) {
            AttackPointsComponent attackPointsComponent = world.getComponent(entity, AttackPointsComponent.class).get();
            subEvents.add(new AttackPointsChangedEvent(entity, attackPointsComponent.getAttackPoints() + deltaAP.get()));
            subEvents.add(new MaxAttackPointsChangedEvent(entity, attackPointsComponent.getAttackPoints() + deltaAP.get()));
        }
        if (deltaMP.isPresent()) {
            MovementPointsComponent movementPointsComponent = world.getComponent(entity, MovementPointsComponent.class).get();
            subEvents.add(new MovementPointsChangedEvent(entity, movementPointsComponent.getMovementPoints() + deltaMP.get()));
            subEvents.add(new MaxMovementPointsChangedEvent(entity, movementPointsComponent.getMovementPoints() + deltaMP.get()));
        }
        if (deltaHP.isPresent()) {
            HealthPointsComponent healthPointsComponent = world.getComponent(entity, HealthPointsComponent.class).get();
            subEvents.add(new HealthPointsChangedEvent(entity, healthPointsComponent.getHealth() + deltaHP.get()));
            subEvents.add(new MaxHealthPointsChangedEvent(entity, healthPointsComponent.getHealth() + deltaHP.get()));
        }
        return subEvents;
    }

    /**
     * returns empty optional empty, if no buff for class
     *
     * @param world
     * @param buff
     * @param classz
     * @param <E>
     * @return
     */
    private <E extends BuffComponent> Optional<Integer> handleBuff(EntityWorld world, Integer buff, Class<E> classz) {
        Optional<E> hpBuff = world.getComponent(buff, classz);
        int delta = 0;
        if (hpBuff.isPresent()) {
            E hpC = hpBuff.get();
            if (hpC.getBuffDuration() == 1) {
                delta = -hpC.getBuffAmount();
                world.remove(buff, classz);
            } else {
                world.addComponent(buff, getDecrementedBuff(classz, hpC));
            }
            return Optional.of(delta);
        }
        return Optional.empty();
    }

    private <E extends BuffComponent> E getDecrementedBuff(Class<E> classz, E buffComponent) {
        if (classz.equals(AttackPointsBuffComponent.class)) {
            return (E) new AttackPointsBuffComponent(buffComponent.getBuffAmount(), buffComponent.getBuffDuration() - 1);
        }
        if (classz.equals(HealthPointBuffComponent.class)) {
            return (E) new HealthPointBuffComponent(buffComponent.getBuffAmount(), buffComponent.getBuffDuration() - 1);
        }
        if (classz.equals(MovementPointBuffComponent.class)) {
            return (E) new MovementPointBuffComponent(buffComponent.getBuffAmount(), buffComponent.getBuffDuration() - 1);
        }
        return null;
    }
}
