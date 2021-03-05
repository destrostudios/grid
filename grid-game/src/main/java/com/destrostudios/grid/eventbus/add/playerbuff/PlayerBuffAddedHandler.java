package com.destrostudios.grid.eventbus.add.playerbuff;

import com.destrostudios.grid.components.properties.AttackPointsComponent;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxAttackPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
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
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerBuffAddedHandler implements EventHandler<PlayerBuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(PlayerBuffAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        List<Event> subevents = new ArrayList<>();
        HealthPointBuffComponent hpBuff = entityWorld.getComponent(event.getSpellEntity(), HealthPointBuffComponent.class);
        List<Integer> buffs = entityWorld.hasComponents(event.getTargetEntity(), BuffsComponent.class)
                ? entityWorld.getComponent(event.getTargetEntity(), BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        if (entityWorld.hasComponents(event.getSpellEntity(), AttackPointsBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            AttackPointsComponent attackPointsComponent = entityWorld.getComponent(event.getTargetEntity(), AttackPointsComponent.class);
            AttackPointsBuffComponent apBuff = entityWorld.getComponent(event.getSpellEntity(), AttackPointsBuffComponent.class);
            MaxAttackPointsComponent maxAp = entityWorld.getComponent(event.getTargetEntity(), MaxAttackPointsComponent.class);
            entityWorld.addComponent(buffEntity, new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration() + 1, false));
            buffs.add(buffEntity);
            subevents.add(new AttackPointsChangedEvent(event.getTargetEntity(), attackPointsComponent.getAttackPoints() + apBuff.getBuffAmount()));
            subevents.add(new MaxAttackPointsChangedEvent(event.getTargetEntity(), maxAp.getMaxAttackPoints() + apBuff.getBuffAmount()));
        }
        if (entityWorld.hasComponents(event.getSpellEntity(), MovementPointBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            MovementPointBuffComponent mpBuff = entityWorld.getComponent(event.getSpellEntity(), MovementPointBuffComponent.class);
            MovementPointsComponent movementPointsComponent = entityWorld.getComponent(event.getTargetEntity(), MovementPointsComponent.class);
            MaxMovementPointsComponent maxMpComponent = entityWorld.getComponent(event.getTargetEntity(), MaxMovementPointsComponent.class);
            entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() + 1, false));
            buffs.add(buffEntity);
            subevents.add(new MovementPointsChangedEvent(event.getTargetEntity(), movementPointsComponent.getMovementPoints() + mpBuff.getBuffAmount()));
            subevents.add(new MaxMovementPointsChangedEvent(event.getTargetEntity(), maxMpComponent.getMaxMovementPoints() + mpBuff.getBuffAmount()));
        }
        if (entityWorld.hasComponents(event.getSpellEntity(), HealthPointBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            HealthPointsComponent healthPointsComponent = entityWorld.getComponent(event.getTargetEntity(), HealthPointsComponent.class);
            MaxHealthComponent maxHpComponent = entityWorld.getComponent(event.getTargetEntity(), MaxHealthComponent.class);
            entityWorld.addComponent(buffEntity, new MovementPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration() + 1, false));
            buffs.add(buffEntity);
            subevents.add(new HealthPointsChangedEvent(event.getTargetEntity(), healthPointsComponent.getHealth() + hpBuff.getBuffAmount()));
            subevents.add(new MaxHealthPointsChangedEvent(event.getTargetEntity(), maxHpComponent.getMaxHealth() + hpBuff.getBuffAmount()));
        }

        entityWorld.addComponent(event.getTargetEntity(), new BuffsComponent(buffs));
        eventbus.registerSubEvents(subevents);
    }
}
