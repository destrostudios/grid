package com.destrostudios.grid.eventbus.update.buff;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxMovementPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.update.ap.AttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.hp.HealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxap.MaxAttackPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxhp.MaxHealthPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.maxmp.MaxMovementPointsChangedEvent;
import com.destrostudios.grid.eventbus.update.mp.MovementPointsChangedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateBuffsHandler implements EventHandler<BuffsUpdateEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(BuffsUpdateEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld world = entityWorldSupplier.get();
        int entity = event.getEntity();

        BuffsComponent buffs = world.hasComponents(entity, BuffsComponent.class)
                ? world.getComponent(entity, BuffsComponent.class)
                : new BuffsComponent(Collections.emptyList());

        List<Integer> newBuffs = new ArrayList<>();
        int deltaAP = 0;
        int deltaMP = 0;
        int deltaHP = 0;

        for (int buff : buffs.getBuffEntities()) {
            if (world.hasComponents(buff, AttackPointsBuffComponent.class)) {
                deltaAP += world.getComponent(buff, AttackPointsBuffComponent.class).getBuffAmount();
            }
            if (world.hasComponents(buff, MovementPointBuffComponent.class)) {
                deltaMP += world.getComponent(buff, MovementPointBuffComponent.class).getBuffAmount();
            }
            if (world.hasComponents(buff, HealthPointBuffComponent.class)) {
                deltaHP += world.getComponent(buff, HealthPointBuffComponent.class).getBuffAmount();
            }
        }

        world.addComponent(entity, new BuffsComponent(newBuffs));
        eventbus.registerSubEvents(getSubEvents(world, entity, deltaAP, deltaMP, deltaHP));
    }

    private List<Event> getSubEvents(EntityWorld world, int entity, int deltaAP, int deltaMP, int deltaHP) {
        List<Event> subEvents = new ArrayList<>();
        if (deltaAP != 0) {
            AttackPointsComponent attackPointsComponent = world.getComponent(entity, AttackPointsComponent.class);
            MaxAttackPointsComponent apCompe = world.getComponent(entity, MaxAttackPointsComponent.class);
            subEvents.add(new AttackPointsChangedEvent(entity, attackPointsComponent.getAttackPoints() + deltaAP));
            subEvents.add(new MaxAttackPointsChangedEvent(entity, apCompe.getMaxAttackPoints() + deltaAP));
        }
        if (deltaMP != 0) {
            MovementPointsComponent movementPointsComponent = world.getComponent(entity, MovementPointsComponent.class);
            MaxMovementPointsComponent maxMp = world.getComponent(entity, MaxMovementPointsComponent.class);
            subEvents.add(new MovementPointsChangedEvent(entity, movementPointsComponent.getMovementPoints() + deltaMP));
            subEvents.add(new MaxMovementPointsChangedEvent(entity, maxMp.getMaxMovementPoints() + deltaMP));
        }
        if (deltaHP != 0) {
            HealthPointsComponent healthPointsComponent = world.getComponent(entity, HealthPointsComponent.class);
            MaxHealthPointsChangedEvent maxHp = world.getComponent(entity, MaxHealthPointsChangedEvent.class);
            subEvents.add(new HealthPointsChangedEvent(entity, healthPointsComponent.getHealth() + deltaHP));
            subEvents.add(new MaxHealthPointsChangedEvent(entity, maxHp.getNewPoints() + deltaHP));
        }
        return subEvents;
    }

}
