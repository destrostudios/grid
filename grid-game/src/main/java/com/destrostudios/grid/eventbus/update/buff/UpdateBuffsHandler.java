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
import com.destrostudios.grid.entities.EntityData;
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
    public void onEvent(BuffsUpdateEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData data = entityDataSupplier.get();
        int entity = event.getEntity();

        BuffsComponent buffs = data.hasComponents(entity, BuffsComponent.class)
                ? data.getComponent(entity, BuffsComponent.class)
                : new BuffsComponent(Collections.emptyList());

        List<Integer> newBuffs = new ArrayList<>();
        int deltaAP = 0;
        int deltaMP = 0;
        int deltaHP = 0;

        for (int buff : buffs.getBuffEntities()) {
            if (data.hasComponents(buff, AttackPointsBuffComponent.class)) {
                deltaAP += data.getComponent(buff, AttackPointsBuffComponent.class).getBuffAmount();
            }
            if (data.hasComponents(buff, MovementPointBuffComponent.class)) {
                deltaMP += data.getComponent(buff, MovementPointBuffComponent.class).getBuffAmount();
            }
            if (data.hasComponents(buff, HealthPointBuffComponent.class)) {
                deltaHP += data.getComponent(buff, HealthPointBuffComponent.class).getBuffAmount();
            }
        }

        data.addComponent(entity, new BuffsComponent(newBuffs));
        eventbus.registerSubEvents(getSubEvents(data, entity, deltaAP, deltaMP, deltaHP));
    }

    private List<Event> getSubEvents(EntityData data, int entity, int deltaAP, int deltaMP, int deltaHP) {
        List<Event> subEvents = new ArrayList<>();
        if (deltaAP != 0) {
            AttackPointsComponent attackPointsComponent = data.getComponent(entity, AttackPointsComponent.class);
            MaxAttackPointsComponent apCompe = data.getComponent(entity, MaxAttackPointsComponent.class);
            subEvents.add(new AttackPointsChangedEvent(entity, attackPointsComponent.getAttackPoints() + deltaAP));
            subEvents.add(new MaxAttackPointsChangedEvent(entity, apCompe.getMaxAttackPoints() + deltaAP));
        }
        if (deltaMP != 0) {
            MovementPointsComponent movementPointsComponent = data.getComponent(entity, MovementPointsComponent.class);
            MaxMovementPointsComponent maxMp = data.getComponent(entity, MaxMovementPointsComponent.class);
            subEvents.add(new MovementPointsChangedEvent(entity, movementPointsComponent.getMovementPoints() + deltaMP));
            subEvents.add(new MaxMovementPointsChangedEvent(entity, maxMp.getMaxMovementPoints() + deltaMP));
        }
        if (deltaHP != 0) {
            HealthPointsComponent healthPointsComponent = data.getComponent(entity, HealthPointsComponent.class);
            MaxHealthPointsChangedEvent maxHp = data.getComponent(entity, MaxHealthPointsChangedEvent.class);
            subEvents.add(new HealthPointsChangedEvent(entity, healthPointsComponent.getHealth() + deltaHP));
            subEvents.add(new MaxHealthPointsChangedEvent(entity, maxHp.getNewPoints() + deltaHP));
        }
        return subEvents;
    }

}
