package com.destrostudios.grid.eventbus.update.playerenchantments;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdatePlayerEnchantmentsHandler implements EventHandler<UpdatePlayerEnchantmentsEvent> {

    @Override
    public void onEvent(UpdatePlayerEnchantmentsEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();

        updatePoisons(event, entityWorld);
        updateBuffs(event, entityWorld);
    }

    private void updatePoisons(UpdatePlayerEnchantmentsEvent event, EntityWorld entityWorld) {
        StatsPerRoundComponent poisons = entityWorld.getComponent(event.getTargetEntity(), StatsPerRoundComponent.class);
        List<Integer> newPoisons = new ArrayList<>(poisons.getStatsPerRoundEntites());
        for (Integer poisonsEntity : poisons.getStatsPerRoundEntites()) {
            boolean removed = updateOrRemove(poisonsEntity, entityWorld);
            if (removed) {
                newPoisons.remove(poisonsEntity);
            }
        }
        entityWorld.addComponent(event.getTargetEntity(), new StatsPerRoundComponent(newPoisons));
    }


    private void updateBuffs(UpdatePlayerEnchantmentsEvent event, EntityWorld entityWorld) {
        BuffsComponent buffs = entityWorld.getComponent(event.getTargetEntity(), BuffsComponent.class);
        List<Integer> newBuffs = new ArrayList<>(buffs.getBuffEntities());
        for (int buffEntity : buffs.getBuffEntities()) {
            boolean removed = updateOrRemove(buffEntity, entityWorld);
            if (removed) {
                newBuffs.remove(buffEntity);
            }
        }
        entityWorld.addComponent(event.getTargetEntity(), new BuffsComponent(newBuffs));
    }

    private boolean updateOrRemove(int entity, EntityWorld entityWorld) {
        Component componentToUpdate = null;
        boolean remove = false;
        if (entityWorld.hasComponents(entity, AttackPointsBuffComponent.class)) {
            AttackPointsBuffComponent apBuff = entityWorld.getComponent(entity, AttackPointsBuffComponent.class);
            remove = apBuff.getBuffDuration() == 1;
            componentToUpdate = new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration() - 1, apBuff.isSpellBuff());

        } else if (entityWorld.hasComponents(entity, HealthPointBuffComponent.class)) {
            HealthPointBuffComponent hpBuff = entityWorld.getComponent(entity, HealthPointBuffComponent.class);
            remove = hpBuff.getBuffDuration() == 1;
            componentToUpdate = new HealthPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration() - 1, hpBuff.isSpellBuff());

        } else if (entityWorld.hasComponents(entity, MovementPointBuffComponent.class)) {
            MovementPointBuffComponent mpBuff = entityWorld.getComponent(entity, MovementPointBuffComponent.class);
            remove = mpBuff.getBuffDuration() == 1;
            componentToUpdate = new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.isSpellBuff());

        } else if (entityWorld.hasComponents(entity, DamageBuffComponent.class)) {
            DamageBuffComponent mpBuff = entityWorld.getComponent(entity, DamageBuffComponent.class);
            remove = mpBuff.getBuffDuration() == 1;
            componentToUpdate = new DamageBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.isSpellBuff());

        } else if (entityWorld.hasComponents(entity, HealBuffComponent.class)) {
            HealBuffComponent healBuff = entityWorld.getComponent(entity, HealBuffComponent.class);
            remove = healBuff.getBuffDuration() == 1;
            componentToUpdate = new HealBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration() - 1, healBuff.isSpellBuff());

        } else if (entityWorld.hasComponents(entity, AttackPointsPerTurnComponent.class)) {
            AttackPointsPerTurnComponent apPoison = entityWorld.getComponent(entity, AttackPointsPerTurnComponent.class);
            remove = apPoison.getPoisonDuration() == 1;
            componentToUpdate = new AttackPointsPerTurnComponent(apPoison.getPoisonMinValue(), apPoison.getPoisonMaxValue(),
                    apPoison.getPoisonDuration() - 1, apPoison.getSourceEntity());

        } else if (entityWorld.hasComponents(entity, MovementPointsPerTurnComponent.class)) {
            MovementPointsPerTurnComponent mpPoison = entityWorld.getComponent(entity, MovementPointsPerTurnComponent.class);
            remove = mpPoison.getPoisonDuration() == 1;
            componentToUpdate = new MovementPointsPerTurnComponent(mpPoison.getPoisonMinValue(), mpPoison.getPoisonMaxValue(),
                    mpPoison.getPoisonDuration() - 1, mpPoison.getSourceEntity());

        } else if (entityWorld.hasComponents(entity, DamagePerTurnComponent.class)) {
            DamagePerTurnComponent hpPoison = entityWorld.getComponent(entity, DamagePerTurnComponent.class);
            remove = hpPoison.getDuration() == 1;
            componentToUpdate = new DamagePerTurnComponent(hpPoison.getDamageMinValue(), hpPoison.getDamageMaxValue(),
                    hpPoison.getDuration() - 1, hpPoison.getSourceEntity());

        } else if (entityWorld.hasComponents(entity, HealPerTurnComponent.class)) {
            HealPerTurnComponent heal = entityWorld.getComponent(entity, HealPerTurnComponent.class);
            remove = heal.getDuration() == 1;
            componentToUpdate = new DamagePerTurnComponent(heal.getHealMinValue(), heal.getHealMaxValue(),
                    heal.getDuration() - 1, heal.getSourceEntity());
        }
        if (componentToUpdate != null) {
            if (remove) {
                entityWorld.remove(entity, componentToUpdate.getClass());
            } else {
                entityWorld.addComponent(entity, componentToUpdate);
            }
        }
        return remove;
    }
}
