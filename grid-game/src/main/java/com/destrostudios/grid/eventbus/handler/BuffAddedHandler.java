package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.BuffAddedEvent;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.events.PropertiePointsChangedEvent;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class BuffAddedHandler implements EventHandler<BuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(BuffAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        List<Event> subevents = new ArrayList<>();
        HealthPointBuffComponent hpBuff = entityWorld.getComponent(event.getSpellEntity(), HealthPointBuffComponent.class);
        List<Integer> buffs = entityWorld.hasComponents(event.getTargetEntity(), BuffsComponent.class)
                ? entityWorld.getComponent(event.getTargetEntity(), BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        int buffEntity = entityWorld.createEntity();
        if (entityWorld.hasComponents(event.getSpellEntity(), AttackPointsBuffComponent.class)) {
            AttackPointsComponent attackPointsComponent = entityWorld.getComponent(event.getTargetEntity(), AttackPointsComponent.class);
            AttackPointsBuffComponent apBuff = entityWorld.getComponent(event.getSpellEntity(), AttackPointsBuffComponent.class);
            MaxAttackPointsComponent maxAp = entityWorld.getComponent(event.getTargetEntity(), MaxAttackPointsComponent.class);
            entityWorld.addComponent(buffEntity, new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration() + 1));
            buffs.add(buffEntity);
            subevents.add(new PropertiePointsChangedEvent.AttackPointsChangedEvent(event.getTargetEntity(), attackPointsComponent.getAttackPoints() + apBuff.getBuffAmount()));
            subevents.add(new PropertiePointsChangedEvent.MaxAttackPointsChangedEvent(event.getTargetEntity(), maxAp.getMaxAttackPoints() + apBuff.getBuffAmount()));
        }
        if (entityWorld.hasComponents(event.getSpellEntity(), MovementPointBuffComponent.class)) {
            MovementPointBuffComponent mpBuff = entityWorld.getComponent(event.getSpellEntity(), MovementPointBuffComponent.class);
            MovementPointsComponent movementPointsComponent = entityWorld.getComponent(event.getTargetEntity(), MovementPointsComponent.class);
            MaxMovementPointsComponent maxMpComponent = entityWorld.getComponent(event.getTargetEntity(), MaxMovementPointsComponent.class);
            entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() + 1));
            buffs.add(buffEntity);
            subevents.add(new PropertiePointsChangedEvent.MovementPointsChangedEvent(event.getTargetEntity(), movementPointsComponent.getMovementPoints() + mpBuff.getBuffAmount()));
            subevents.add(new PropertiePointsChangedEvent.MaxMovementPointsChangedEvent(event.getTargetEntity(), maxMpComponent.getMaxMovenemtPoints() + mpBuff.getBuffAmount()));
        }
        if (entityWorld.hasComponents(event.getSpellEntity(), HealthPointBuffComponent.class)) {
            HealthPointsComponent healthPointsComponent = entityWorld.getComponent(event.getTargetEntity(), HealthPointsComponent.class);
            MaxHealthComponent maxHpComponent = entityWorld.getComponent(event.getTargetEntity(), MaxHealthComponent.class);
            entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration() + 1));
            buffs.add(buffEntity);
            subevents.add(new PropertiePointsChangedEvent.HealthPointsChangedEvent(event.getTargetEntity(), healthPointsComponent.getHealth() + hpBuff.getBuffAmount()));
            subevents.add(new PropertiePointsChangedEvent.MaxHealthPointsChangedEvent(event.getTargetEntity(), maxHpComponent.getMaxHealth() + hpBuff.getBuffAmount()));
        }

        entityWorld.addComponent(event.getTargetEntity(), new BuffsComponent(buffs));
        eventbus.registerSubEvents(subevents);
    }
}
