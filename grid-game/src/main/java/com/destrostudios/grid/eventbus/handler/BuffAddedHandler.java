package com.destrostudios.grid.eventbus.handler;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MovementPointsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Eventbus;
import com.destrostudios.grid.eventbus.events.BuffAddedEvent;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.events.properties.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
public class BuffAddedHandler implements EventHandler<BuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(BuffAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        List<Event> subevents = new ArrayList<>();
        Optional<AttackPointsBuffComponent> apBuff = entityWorld.getComponent(event.getSpellEntity(), AttackPointsBuffComponent.class);
        Optional<MovementPointBuffComponent> mpBuff = entityWorld.getComponent(event.getSpellEntity(), MovementPointBuffComponent.class);
        Optional<HealthPointBuffComponent> hpBuff = entityWorld.getComponent(event.getSpellEntity(), HealthPointBuffComponent.class);
        List<Integer> buffs = entityWorld.getComponent(event.getTargetEntity(), BuffsComponent.class).orElse(new BuffsComponent()).getBuffEntities();

        if (apBuff.isPresent() || mpBuff.isPresent() || hpBuff.isPresent()) {
            int buffEntity = entityWorld.createEntity();
            if (apBuff.isPresent()) {
                AttackPointsComponent attackPointsComponent = entityWorld.getComponent(event.getTargetEntity(), AttackPointsComponent.class).get();
                entityWorld.addComponent(buffEntity, new AttackPointsBuffComponent(apBuff.get().getBuffAmount(), apBuff.get().getBuffDuration() + 1));
                buffs.add(buffEntity);
                subevents.add(new AttackPointsChangedEvent(event.getTargetEntity(), attackPointsComponent.getAttackPoints() + apBuff.get().getBuffAmount()));
                subevents.add(new MaxAttackPointsChangedEvent(event.getTargetEntity(), attackPointsComponent.getAttackPoints() + apBuff.get().getBuffAmount()));
            }
            if (mpBuff.isPresent()) {
                MovementPointsComponent movementPointsComponent = entityWorld.getComponent(event.getTargetEntity(), MovementPointsComponent.class).get();
                entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(mpBuff.get().getBuffAmount(), mpBuff.get().getBuffDuration() + 1));
                buffs.add(buffEntity);
                subevents.add(new MovementPointsChangedEvent(event.getTargetEntity(), movementPointsComponent.getMovementPoints() + mpBuff.get().getBuffAmount()));
                subevents.add(new MaxMovementPointsChangedEvent(event.getTargetEntity(), movementPointsComponent.getMovementPoints() + mpBuff.get().getBuffAmount()));
            }
            if (hpBuff.isPresent()) {
                HealthPointsComponent healthPointsComponent = entityWorld.getComponent(event.getTargetEntity(), HealthPointsComponent.class).get();
                entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(hpBuff.get().getBuffAmount(), hpBuff.get().getBuffDuration() + 1));
                buffs.add(buffEntity);
                subevents.add(new HealthPointsChangedEvent(event.getTargetEntity(), healthPointsComponent.getHealth() + hpBuff.get().getBuffAmount()));
                subevents.add(new MaxHealthPointsChangedEvent(event.getTargetEntity(), healthPointsComponent.getHealth() + hpBuff.get().getBuffAmount()));
            }
        }
        entityWorld.addComponent(event.getTargetEntity(), new BuffsComponent(buffs));
        eventbus.registerSubEvents(subevents);
    }
}
