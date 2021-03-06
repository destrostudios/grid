package com.destrostudios.grid.eventbus.add.spellbuff;

import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.spells.buffs.DamageBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class SpellBuffAddedHandler implements EventHandler<SpellBuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(SpellBuffAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        int spellEntity = event.getSpellEntity();
        EntityWorld entityWorld = entityWorldSupplier.get();
        List<Event> subevents = new ArrayList<>();

        List<Integer> spellBuffs = entityWorld.hasComponents(spellEntity, BuffsComponent.class)
                ? entityWorld.getComponent(spellEntity, BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        // create buffs
        if (entityWorld.hasComponents(spellEntity, DamageBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            DamageBuffComponent dmgBuff = entityWorld.getComponent(spellEntity, DamageBuffComponent.class);
            entityWorld.addComponent(buffEntity, new DamageBuffComponent(dmgBuff.getBuffAmount(), dmgBuff.getBuffDuration() + 1, true));
            spellBuffs.add(buffEntity);
        }

        if (entityWorld.hasComponents(spellEntity, HealBuffComponent.class)) {
            int buffEntity = entityWorld.createEntity();
            HealBuffComponent healBuff = entityWorld.getComponent(spellEntity, HealBuffComponent.class);
            entityWorld.addComponent(buffEntity, new HealBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration(), true));
            spellBuffs.add(buffEntity);
        }

        entityWorld.addComponent(spellEntity, new BuffsComponent(spellBuffs));
        eventbus.registerSubEvents(subevents);
    }
}
