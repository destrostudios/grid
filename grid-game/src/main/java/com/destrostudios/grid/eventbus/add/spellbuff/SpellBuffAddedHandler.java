package com.destrostudios.grid.eventbus.add.spellbuff;

import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.buffs.*;
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
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class SpellBuffAddedHandler implements EventHandler<SpellBuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(SpellBuffAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        List<Event> subevents = new ArrayList<>();

        List<Integer> buffs = entityWorld.hasComponents(event.getSpellEntity(), BuffsComponent.class)
                ? entityWorld.getComponent(event.getSpellEntity(), BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        if (entityWorld.hasComponents(event.getSpellEntity(), DamageBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            DamageBuffComponent dmgBuff = entityWorld.getComponent(event.getSpellEntity(), DamageBuffComponent.class);
            entityWorld.addComponent(buffEntity, new DamageBuffComponent(dmgBuff.getBuffAmount(), dmgBuff.getBuffDuration() + 1, true));
            buffs.add(buffEntity);
        }

        if (entityWorld.hasComponents(event.getSpellEntity(), HealBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            HealBuffComponent healBuff = entityWorld.getComponent(event.getSpellEntity(), HealBuffComponent.class);
            entityWorld.addComponent(buffEntity, new HealBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration() + 1, true));
            buffs.add(buffEntity);
        }

        entityWorld.addComponent(event.getSpellEntity(), new BuffsComponent(buffs));
        eventbus.registerSubEvents(subevents);
    }
}
