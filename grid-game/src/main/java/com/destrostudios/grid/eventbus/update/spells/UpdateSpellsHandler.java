package com.destrostudios.grid.eventbus.update.spells;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.SpellsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.perturn.CastsPerTurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.function.Supplier;

public class UpdateSpellsHandler implements EventHandler<UpdateSpellsEvent> {

    @Override
    public void onEvent(UpdateSpellsEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        SpellsComponent spellsComponent = entityWorld.getComponent(event.getEntity(), SpellsComponent.class);

        for (Integer spellEntity : spellsComponent.getSpells()) {
            updateCooldown(entityWorld, spellEntity);
            updateCastsPerTurn(entityWorld, spellEntity);
            updateBuffs(entityWorld, spellEntity);
        }
    }

    private void updateBuffs(EntityWorld entityWorld, Integer spellEntity) {
        if (entityWorld.hasComponents(spellEntity, BuffsComponent.class)) {
            BuffsComponent buffs = entityWorld.getComponent(spellEntity, BuffsComponent.class);

            for (Integer buffEntity : buffs.getBuffEntities()) {
                for (Component buffComponent : entityWorld.getComponents(buffEntity)) {
                    updateBuff(entityWorld, buffComponent, buffEntity);
                }
            }
        }
    }

    private void updateBuff(EntityWorld entityWorld, Component component, Integer buffEntity) {
        if (component instanceof AttackPointsBuffComponent) {
            AttackPointsBuffComponent buffComp = (AttackPointsBuffComponent) component;
            if (buffComp.getBuffDuration() == 1) {
                entityWorld.remove(buffEntity, AttackPointsBuffComponent.class);
            } else {
                entityWorld.addComponent(buffEntity, new AttackPointsBuffComponent(buffComp.getBuffAmount(), buffComp.getBuffDuration() - 1, true));
            }
        }
        if (component instanceof HealBuffComponent) {
            HealBuffComponent healBuff = (HealBuffComponent) component;
            if (healBuff.getBuffDuration() == 1) {
                entityWorld.remove(buffEntity, HealBuffComponent.class);
            } else {
                entityWorld.addComponent(buffEntity, new AttackPointsBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration() - 1, true));
            }
        }
    }

    private void updateCooldown(EntityWorld entityWorld, Integer spellEntity) {
        if (entityWorld.hasComponents(spellEntity, OnCooldownComponent.class)) {
            OnCooldownComponent onCooldownComponent = entityWorld.getComponent(spellEntity, OnCooldownComponent.class);
            if (onCooldownComponent.getRemainingRounds() == 1) {
                entityWorld.remove(spellEntity, OnCooldownComponent.class);
            } else {
                entityWorld.addComponent(spellEntity, new OnCooldownComponent(onCooldownComponent.getRemainingRounds() - 1));
            }
        }
    }

    private void updateCastsPerTurn(EntityWorld entityWorld, Integer spellEntity) {
        CastsPerTurnComponent castsPerTurnComponent = entityWorld.getComponent(spellEntity, CastsPerTurnComponent.class);
        if (entityWorld.hasComponents(spellEntity, CastsPerTurnComponent.class)) {
            if (castsPerTurnComponent.getCastsThisTurn() == castsPerTurnComponent.getMaxCastsPerTurn() - 1) {
                entityWorld.remove(spellEntity, CastsPerTurnComponent.class);
            } else {
                entityWorld.addComponent(spellEntity, new CastsPerTurnComponent(castsPerTurnComponent.getMaxCastsPerTurn(), 0));
            }
        }
    }
}
