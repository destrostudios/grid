package com.destrostudios.grid.eventbus.update.cooldown;

import com.destrostudios.grid.components.spells.OnCooldownComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class UpdateCooldownsHandler implements EventHandler<UpdateCooldownsUpdateEvent> {
    @Override
    public void onEvent(UpdateCooldownsUpdateEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        SpellsComponent spellsComponent = entityWorld.getComponent(event.getEntity(), SpellsComponent.class);

        for (Integer spellEntity : spellsComponent.getSpells()) {
            if (entityWorld.hasComponents(spellEntity, OnCooldownComponent.class)) {
                OnCooldownComponent onCooldownComponent = entityWorld.getComponent(spellEntity, OnCooldownComponent.class);
                if (onCooldownComponent.getRemainingRounds() == 1) {
                    entityWorld.remove(spellEntity, OnCooldownComponent.class);
                } else {
                    entityWorld.addComponent(spellEntity, new OnCooldownComponent(onCooldownComponent.getRemainingRounds() - 1));
                }
            }
        }
    }
}
