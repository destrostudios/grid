package com.destrostudios.grid.eventbus.add.poison;

import com.destrostudios.grid.components.properties.StatsPerRoundComponent;
import com.destrostudios.grid.components.spells.perturn.AttackPointsPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.DamagePerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.HealPerTurnComponent;
import com.destrostudios.grid.components.spells.perturn.MovementPointsPerTurnComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.EventHandler;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class StatsPerTurnHandler implements EventHandler<StatsPerTurnEvent> {

    @Override
    public void onEvent(StatsPerTurnEvent event, Supplier<EntityWorld> entityWorldSupplier) {
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
        if (entityWorld.hasComponents(spell, DamagePerTurnComponent.class)) {
            int activePoison = entityWorld.createEntity();
            DamagePerTurnComponent hpPoison = entityWorld.getComponent(event.getSpellEntity(), DamagePerTurnComponent.class);
            entityWorld.addComponent(activePoison, new DamagePerTurnComponent(hpPoison.getDamageMinValue(), hpPoison.getDamageMaxValue(),
                    hpPoison.getDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }
        if (entityWorld.hasComponents(spell, HealPerTurnComponent.class)) {
            int activePoison = entityWorld.createEntity();
            HealPerTurnComponent healPerTurn = entityWorld.getComponent(event.getSpellEntity(), HealPerTurnComponent.class);
            entityWorld.addComponent(activePoison, new DamagePerTurnComponent(healPerTurn.getHealMinValue(), healPerTurn.getHealMaxValue(),
                    healPerTurn.getDuration(), event.getSourceEntity()));
            poisons.add(activePoison);
        }

        entityWorld.addComponent(event.getTargetEntity(), new StatsPerRoundComponent(poisons));
    }

    private List<Integer> getPoisons(StatsPerTurnEvent event, EntityWorld entityWorld) {
        return entityWorld.hasComponents(event.getTargetEntity(), StatsPerRoundComponent.class)
                ? entityWorld.getComponent(event.getTargetEntity(), StatsPerRoundComponent.class).getStatsPerRoundEntites()
                : new ArrayList<>();
    }

}
