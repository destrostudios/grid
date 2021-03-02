package com.destrostudios.grid.eventbus.update.spell;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.properties.BuffsComponent;
import com.destrostudios.grid.components.properties.PoisonsComponent;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.destrostudios.grid.components.spells.poison.AttackPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.HealthPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.MovementPointsPoisonComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpdateActiveDurationSpellsHandler implements EventHandler<UpdateAcitveDurationSpellsEvent> {

    @Override
    public void onEvent(UpdateAcitveDurationSpellsEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();

        updatePoisons(event, entityWorld);
        updateBuffs(event, entityWorld);
    }

    private void updatePoisons(UpdateAcitveDurationSpellsEvent event, EntityWorld entityWorld) {
        PoisonsComponent poisons = entityWorld.getComponent(event.getTargetEntity(), PoisonsComponent.class);
        List<Integer> newPoisons = new ArrayList<>(poisons.getPoisonsEntities());
        for (Integer poisonsEntity : poisons.getPoisonsEntities()) {
            boolean removed = updateOrRemove(poisonsEntity, entityWorld);
            if (removed) {
                newPoisons.remove(poisonsEntity);
            }
        }
        entityWorld.addComponent(event.getTargetEntity(), new PoisonsComponent(newPoisons));
    }

    private void updateBuffs(UpdateAcitveDurationSpellsEvent event, EntityWorld entityWorld) {
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
            componentToUpdate = new AttackPointsBuffComponent(apBuff.getBuffAmount(), apBuff.getBuffDuration() - 1);

        } else if (entityWorld.hasComponents(entity, HealthPointBuffComponent.class)) {
            HealthPointBuffComponent hpBuff = entityWorld.getComponent(entity, HealthPointBuffComponent.class);
            remove = hpBuff.getBuffDuration() == 1;
            componentToUpdate = new HealthPointBuffComponent(hpBuff.getBuffAmount(), hpBuff.getBuffDuration() - 1);

        } else if (entityWorld.hasComponents(entity, MovementPointBuffComponent.class)) {
            MovementPointBuffComponent mpBuff = entityWorld.getComponent(entity, MovementPointBuffComponent.class);
            remove = mpBuff.getBuffDuration() == 1;
            componentToUpdate = new MovementPointBuffComponent(mpBuff.getBuffAmount(), mpBuff.getBuffDuration() - 1);

        } else if (entityWorld.hasComponents(entity, AttackPointsPoisonComponent.class)) {
            AttackPointsPoisonComponent apPoison = entityWorld.getComponent(entity, AttackPointsPoisonComponent.class);
            remove = apPoison.getPoisonDuration() == 1;
            componentToUpdate = new AttackPointsPoisonComponent(apPoison.getPoisonMinValue(), apPoison.getPoisonMaxValue(),
                    apPoison.getPoisonDuration() - 1, apPoison.getSourceEntity());

        } else if (entityWorld.hasComponents(entity, MovementPointsPoisonComponent.class)) {
            MovementPointsPoisonComponent mpPoison = entityWorld.getComponent(entity, MovementPointsPoisonComponent.class);
            remove = mpPoison.getPoisonDuration() == 1;
            componentToUpdate = new MovementPointsPoisonComponent(mpPoison.getPoisonMinValue(), mpPoison.getPoisonMaxValue(),
                    mpPoison.getPoisonDuration() - 1, mpPoison.getSourceEntity());

        } else if (entityWorld.hasComponents(entity, HealthPointsPoisonComponent.class)) {
            HealthPointsPoisonComponent hpPoison = entityWorld.getComponent(entity, HealthPointsPoisonComponent.class);
            remove = hpPoison.getPoisonDuration() == 1;
            componentToUpdate = new HealthPointsPoisonComponent(hpPoison.getPoisonMinValue(), hpPoison.getPoisonMaxValue(),
                    hpPoison.getPoisonDuration() - 1, hpPoison.getSourceEntity());
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
