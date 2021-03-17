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
import com.destrostudios.grid.components.spells.buffs.ReflectionBuffComponent;
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
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerBuffAddedHandler implements EventHandler<PlayerBuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(PlayerBuffAddedEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        List<Event> subevents = new ArrayList<>();
        List<Integer> buffs = entityData.hasComponents(event.getTargetEntity(), BuffsComponent.class)
                ? entityData.getComponent(event.getTargetEntity(), BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        // create buff and add to buffs
        if (entityData.hasComponents(event.getSpellEntity(), AttackPointsBuffComponent.class)) {
            int buffEntity = entityData.createEntity();
            buffs.add(buffEntity);
            AttackPointsComponent apTarget = entityData.getComponent(event.getTargetEntity(), AttackPointsComponent.class);
            AttackPointsBuffComponent apBuff = entityData.getComponent(event.getSpellEntity(), AttackPointsBuffComponent.class);
            MaxAttackPointsComponent maxAp = entityData.getComponent(event.getTargetEntity(), MaxAttackPointsComponent.class);
            entityData.addComponent(buffEntity, new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration(), false));

            subevents.add(new AttackPointsChangedEvent(event.getTargetEntity(), apTarget.getAttackPoints() + apBuff.getBuffAmount()));
            subevents.add(new MaxAttackPointsChangedEvent(event.getTargetEntity(), maxAp.getMaxAttackPoints() + apBuff.getBuffAmount()));
        }
        if (entityData.hasComponents(event.getSpellEntity(), MovementPointBuffComponent.class)) {

            int buffEntity = entityData.createEntity();
            buffs.add(buffEntity);
            MovementPointBuffComponent mpBuff = entityData.getComponent(event.getSpellEntity(), MovementPointBuffComponent.class);
            MovementPointsComponent mpTarget = entityData.getComponent(event.getTargetEntity(), MovementPointsComponent.class);
            MaxMovementPointsComponent maxMpComponent = entityData.getComponent(event.getTargetEntity(), MaxMovementPointsComponent.class);
            entityData.addComponent(buffEntity, new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration(), false));

            subevents.add(new MovementPointsChangedEvent(event.getTargetEntity(), mpTarget.getMovementPoints() + mpBuff.getBuffAmount()));
            subevents.add(new MaxMovementPointsChangedEvent(event.getTargetEntity(), maxMpComponent.getMaxMovementPoints() + mpBuff.getBuffAmount()));
        }
        if (entityData.hasComponents(event.getSpellEntity(), HealthPointBuffComponent.class)) {
            int buffEntity = entityData.createEntity();
            buffs.add(buffEntity);

            HealthPointBuffComponent hpBuff = entityData.getComponent(event.getSpellEntity(), HealthPointBuffComponent.class);
            HealthPointsComponent healthPointsComponent = entityData.getComponent(event.getTargetEntity(), HealthPointsComponent.class);
            MaxHealthComponent maxHpComponent = entityData.getComponent(event.getTargetEntity(), MaxHealthComponent.class);
            entityData.addComponent(buffEntity, new MovementPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration(), false));

            subevents.add(new HealthPointsChangedEvent(event.getTargetEntity(), healthPointsComponent.getHealth() + hpBuff.getBuffAmount()));
            subevents.add(new MaxHealthPointsChangedEvent(event.getTargetEntity(), maxHpComponent.getMaxHealth() + hpBuff.getBuffAmount()));
        }
        if (entityData.hasComponents(event.getSpellEntity(), ReflectionBuffComponent.class)) {
            int buffEntity = entityData.createEntity();
            buffs.add(buffEntity);

            ReflectionBuffComponent reflectionBuff = entityData.getComponent(event.getSpellEntity(), ReflectionBuffComponent.class);
            entityData.addComponent(buffEntity, new ReflectionBuffComponent(reflectionBuff.getBuffAmount(), reflectionBuff.getBuffDuration(), false));
        }
        entityData.addComponent(event.getTargetEntity(), new BuffsComponent(buffs));
        eventbus.registerSubEvents(subevents);
    }
}
