package com.destrostudios.grid.eventbus.add.poison;

import com.destrostudios.grid.components.properties.PoisonsComponent;
import com.destrostudios.grid.components.spells.poison.AttackPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.HealthPointsPoisonComponent;
import com.destrostudios.grid.components.spells.poison.MovementPointsPoisonComponent;
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

        if (entityWorld.hasComponents(spell, AttackPointsPoisonComponent.class)) {
            int activePoison = entityWorld.createEntity();
            AttackPointsPoisonComponent apPoison = entityWorld.getComponent(event.getSpellEntity(), AttackPointsPoisonComponent.class);
            entityWorld.addComponent(activePoison, new AttackPointsPoisonComponent(apPoison.getPoisonMinValue(), apPoison.getPoisonMaxValue(),
                    apPoison.getPoisonDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }
        if (entityWorld.hasComponents(spell, MovementPointsPoisonComponent.class)) {
            int activePoison = entityWorld.createEntity();
            MovementPointsPoisonComponent mpPoison = entityWorld.getComponent(event.getSpellEntity(), MovementPointsPoisonComponent.class);
            entityWorld.addComponent(activePoison, new MovementPointsPoisonComponent(mpPoison.getPoisonMinValue(), mpPoison.getPoisonMaxValue(),
                    mpPoison.getPoisonDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }
        if (entityWorld.hasComponents(spell, HealthPointsPoisonComponent.class)) {
            int activePoison = entityWorld.createEntity();
            HealthPointsPoisonComponent hpPoison = entityWorld.getComponent(event.getSpellEntity(), HealthPointsPoisonComponent.class);
            entityWorld.addComponent(activePoison, new HealthPointsPoisonComponent(hpPoison.getPoisonMinValue(), hpPoison.getPoisonMaxValue(),
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
