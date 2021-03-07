package com.destrostudios.grid.eventbus.update.playerenchantments;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.DamageBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdatePlayerEnchantmentsHandler implements EventHandler<UpdatePlayerEnchantmentsEvent> {

    @Override
    public void onEvent(UpdatePlayerEnchantmentsEvent event, Supplier<EntityData> entityDataSupplier) {
        EntityData entityData = entityDataSupplier.get();

        updatePoisons(event, entityData);
        updateBuffs(event, entityData);
    }

    private void updatePoisons(UpdatePlayerEnchantmentsEvent event, EntityData entityData) {
        StatsPerRoundComponent poisons = entityData.getComponent(event.getTargetEntity(), StatsPerRoundComponent.class);
        List<Integer> newPoisons = new ArrayList<>(poisons.getStatsPerRoundEntites());
        for (Integer poisonsEntity : poisons.getStatsPerRoundEntites()) {
            boolean removed = updateOrRemove(poisonsEntity, entityData);
            if (removed) {
                newPoisons.remove(poisonsEntity);
            }
        }
        entityData.addComponent(event.getTargetEntity(), new StatsPerRoundComponent(newPoisons));
    }


    private void updateBuffs(UpdatePlayerEnchantmentsEvent event, EntityData entityData) {
        BuffsComponent buffs = entityData.getComponent(event.getTargetEntity(), BuffsComponent.class);
        List<Integer> newBuffs = new ArrayList<>(buffs.getBuffEntities());
        for (int buffEntity : buffs.getBuffEntities()) {
            boolean removed = updateOrRemove(buffEntity, entityData);
            if (removed) {
                newBuffs.remove(buffEntity);
            }
        }
        entityData.addComponent(event.getTargetEntity(), new BuffsComponent(newBuffs));
    }

    private boolean updateOrRemove(int entity, EntityData entityData) {
        Component componentToUpdate = null;
        boolean remove = false;
        if (entityData.hasComponents(entity, AttackPointsBuffComponent.class)) {
            AttackPointsBuffComponent apBuff = entityData.getComponent(entity, AttackPointsBuffComponent.class);
            remove = apBuff.getBuffDuration() == 1;
            componentToUpdate = new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration() - 1, apBuff.isSpellBuff());

        } else if (entityData.hasComponents(entity, HealthPointBuffComponent.class)) {
            HealthPointBuffComponent hpBuff = entityData.getComponent(entity, HealthPointBuffComponent.class);
            remove = hpBuff.getBuffDuration() == 1;
            componentToUpdate = new HealthPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration() - 1, hpBuff.isSpellBuff());

        } else if (entityData.hasComponents(entity, MovementPointBuffComponent.class)) {
            MovementPointBuffComponent mpBuff = entityData.getComponent(entity, MovementPointBuffComponent.class);
            remove = mpBuff.getBuffDuration() == 1;
            componentToUpdate = new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.isSpellBuff());

        } else if (entityData.hasComponents(entity, DamageBuffComponent.class)) {
            DamageBuffComponent mpBuff = entityData.getComponent(entity, DamageBuffComponent.class);
            remove = mpBuff.getBuffDuration() == 1;
            componentToUpdate = new DamageBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1, mpBuff.isSpellBuff());

        } else if (entityData.hasComponents(entity, HealBuffComponent.class)) {
            HealBuffComponent healBuff = entityData.getComponent(entity, HealBuffComponent.class);
            remove = healBuff.getBuffDuration() == 1;
            componentToUpdate = new HealBuffComponent(healBuff.getBuffAmount(), healBuff.getBuffDuration() - 1, healBuff.isSpellBuff());

        } else if (entityData.hasComponents(entity, AttackPointsPerTurnComponent.class)) {
            AttackPointsPerTurnComponent apPoison = entityData.getComponent(entity, AttackPointsPerTurnComponent.class);
            remove = apPoison.getPoisonDuration() == 1;
            componentToUpdate = new AttackPointsPerTurnComponent(apPoison.getPoisonMinValue(), apPoison.getPoisonMaxValue(),
                    apPoison.getPoisonDuration() - 1, apPoison.getSourceEntity());

        } else if (entityData.hasComponents(entity, MovementPointsPerTurnComponent.class)) {
            MovementPointsPerTurnComponent mpPoison = entityData.getComponent(entity, MovementPointsPerTurnComponent.class);
            remove = mpPoison.getPoisonDuration() == 1;
            componentToUpdate = new MovementPointsPerTurnComponent(mpPoison.getPoisonMinValue(), mpPoison.getPoisonMaxValue(),
                    mpPoison.getPoisonDuration() - 1, mpPoison.getSourceEntity());

        } else if (entityData.hasComponents(entity, DamagePerTurnComponent.class)) {
            DamagePerTurnComponent hpPoison = entityData.getComponent(entity, DamagePerTurnComponent.class);
            remove = hpPoison.getDuration() == 1;
            componentToUpdate = new DamagePerTurnComponent(hpPoison.getDamageMinValue(), hpPoison.getDamageMaxValue(),
                    hpPoison.getDuration() - 1, hpPoison.getSourceEntity());

        } else if (entityData.hasComponents(entity, HealPerTurnComponent.class)) {
            HealPerTurnComponent heal = entityData.getComponent(entity, HealPerTurnComponent.class);
            remove = heal.getDuration() == 1;
            componentToUpdate = new DamagePerTurnComponent(heal.getHealMinValue(), heal.getHealMaxValue(),
                    heal.getDuration() - 1, heal.getSourceEntity());
        }
        if (componentToUpdate != null) {
            if (remove) {
                entityData.remove(entity, componentToUpdate.getClass());
            } else {
                entityData.addComponent(entity, componentToUpdate);
            }
        }
        return remove;
    }
}
