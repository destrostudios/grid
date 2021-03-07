package com.destrostudios.grid.eventbus.add.spellbuff;

import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.spells.buffs.DamageBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.eventbus.Eventbus;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpellBuffAddedHandler implements EventHandler<SpellBuffAddedEvent> {
    private final Eventbus eventbus;

    @Override
    public void onEvent(SpellBuffAddedEvent event, Supplier<EntityData> entityDataSupplier) {
        int spellEntity = event.getSpellEntity();
        EntityData entityData = entityDataSupplier.get();
        List<Event> subevents = new ArrayList<>();

        List<Integer> spellBuffs = entityData.hasComponents(spellEntity, BuffsComponent.class)
                ? entityData.getComponent(spellEntity, BuffsComponent.class).getBuffEntities()
                : new ArrayList<>();

        // create buffs
        if (entityData.hasComponents(spellEntity, DamageBuffComponent.class)) {
            int buffEntity = entityData.createEntity();
            DamageBuffComponent dmgBuff = entityData.getComponent(spellEntity, DamageBuffComponent.class);
            entityData.addComponent(buffEntity, new DamageBuffComponent(dmgBuff.getBuffAmount(), dmgBuff.getBuffDuration() + 1, true));
            spellBuffs.add(buffEntity);
        }

        if (entityData.hasComponents(spellEntity, HealBuffComponent.class)) {
            int buffEntity = entityData.createEntity();
            HealBuffComponent healBuff = entityData.getComponent(spellEntity, HealBuffComponent.class);
            entityData.addComponent(buffEntity, new HealBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration(), true));
            spellBuffs.add(buffEntity);
        }

        entityData.addComponent(spellEntity, new BuffsComponent(spellBuffs));
        eventbus.registerSubEvents(subevents);
    }
}
