package com.destrostudios.grid.eventbus.add.poison;

import com.destrostudios.grid.components.properties.PoisonsComponent;
import com.destrostudios.grid.components.spells.poison.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.poison.HealthPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.poison.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class PoisonAddedHandler implements EventHandler<PoisonAddedEvent> {

    @Override
    public void onEvent(PoisonAddedEvent event, Supplier<EntityWorld> entityWorldSupplier) {
        EntityWorld entityWorld = entityWorldSupplier.get();
        int spell = event.getSpellEntity();
        List<Integer> poisons = getPoisons(event, entityWorld);

        if (entityWorld.hasComponents(spell, AttackPointsPerTurnComponent.class)) {
            int activePoison = entityWorld.createEntity();
            AttackPointsPerTurnComponent apPoison = entityWorld.getComponent(event.getSpellEntity(), AttackPointsPerTurnComponent.class);
            entityWorld.addComponent(activePoison, new AttackPointsPerTurnComponent(apPoison.getPoisonMinValue(), apPoison.getPoisonMaxValue(),
                    apPoison.getPoisonDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }
        if (entityWorld.hasComponents(spell, MovementPointsPerTurnComponent.class)) {
            int activePoison = entityWorld.createEntity();
            MovementPointsPerTurnComponent mpPoison = entityWorld.getComponent(event.getSpellEntity(), MovementPointsPerTurnComponent.class);
            entityWorld.addComponent(activePoison, new MovementPointsPerTurnComponent(mpPoison.getPoisonMinValue(), mpPoison.getPoisonMaxValue(),
                    mpPoison.getPoisonDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }
        if (entityWorld.hasComponents(spell, HealthPointsPerTurnComponent.class)) {
            int activePoison = entityWorld.createEntity();
            HealthPointsPerTurnComponent hpPoison = entityWorld.getComponent(event.getSpellEntity(), HealthPointsPerTurnComponent.class);
            entityWorld.addComponent(activePoison, new HealthPointsPerTurnComponent(hpPoison.getPoisonMinValue(), hpPoison.getPoisonMaxValue(),
                    hpPoison.getPoisonDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }

        entityWorld.addComponent(event.getTargetEntity(), new PoisonsComponent(poisons));
    }

    private List<Integer> getPoisons(PoisonAddedEvent event, EntityWorld entityWorld) {
        return entityWorld.hasComponents(event.getTargetEntity(), PoisonsComponent.class)
                ? entityWorld.getComponent(event.getTargetEntity(), PoisonsComponent.class).getPoisonsEntities()
                : new ArrayList<>();
    }

}
