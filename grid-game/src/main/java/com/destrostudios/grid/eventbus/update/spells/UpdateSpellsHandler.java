package com.destrostudios.grid.eventbus.update.spells;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class UpdateSpellsHandler implements EventHandler<UpdateSpellsEvent> {

    @Override
    public void onEvent(UpdateSpellsEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();
        SpellsComponent spellsComponent = entityData.getComponent(event.getEntity(), SpellsComponent.class);

        for (Integer spellEntity : spellsComponent.getSpells()) {
            updateCooldown(entityData, spellEntity);
            updateCastsPerTurn(entityData, spellEntity);
            updateBuffs(entityData, spellEntity);
        }
    }

    private void updateBuffs(EntityData entityData, Integer spellEntity) {
        if (entityData.hasComponents(spellEntity, BuffsComponent.class)) {
            BuffsComponent buffs = entityData.getComponent(spellEntity, BuffsComponent.class);

            for (Integer buffEntity : buffs.getBuffEntities()) {
                for (Component buffComponent : entityData.getComponents(buffEntity)) {
                    updateBuff(entityData, buffComponent, buffEntity);
                }
            }
        }
    }

    private void updateBuff(EntityData entityData, Component component, Integer buffEntity) {
        if (component instanceof AttackPointsBuffComponent) {
            AttackPointsBuffComponent buffComp = (AttackPointsBuffComponent) component;
            if (buffComp.getBuffDuration() == 1) {
                entityData.remove(buffEntity, AttackPointsBuffComponent.class);
            } else {
                entityData.addComponent(buffEntity, new AttackPointsBuffComponent(buffComp.getBuffAmount(), buffComp.getBuffDuration() - 1, true));
            }
        }
        if (component instanceof HealBuffComponent) {
            HealBuffComponent healBuff = (HealBuffComponent) component;
            if (healBuff.getBuffDuration() == 1) {
                entityData.remove(buffEntity, HealBuffComponent.class);
            } else {
                entityData.addComponent(buffEntity, new AttackPointsBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration() - 1, true));
            }
        }
    }

    private void updateCooldown(EntityData entityData, Integer spellEntity) {
        if (entityData.hasComponents(spellEntity, OnCooldownComponent.class)) {
            OnCooldownComponent onCooldownComponent = entityData.getComponent(spellEntity, OnCooldownComponent.class);
            if (onCooldownComponent.getRemainingRounds() == 1) {
                entityData.remove(spellEntity, OnCooldownComponent.class);
            } else {
                entityData.addComponent(spellEntity, new OnCooldownComponent(onCooldownComponent.getRemainingRounds() - 1));
            }
        }
    }

    private void updateCastsPerTurn(EntityData entityData, Integer spellEntity) {
        CastsPerTurnComponent castsPerTurnComponent = entityData.getComponent(spellEntity, CastsPerTurnComponent.class);
        if (entityData.hasComponents(spellEntity, CastsPerTurnComponent.class)) {
            entityData.addComponent(spellEntity, new CastsPerTurnComponent(castsPerTurnComponent.getMaxCastsPerTurn(), 0));
        }
    }
}
