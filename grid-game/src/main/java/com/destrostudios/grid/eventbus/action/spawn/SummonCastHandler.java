package com.destrostudios.grid.eventbus.action.spawn;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.glyphs.SpellOnTouchComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.EventHandler;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.SummonContainer;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SummonCastHandler implements EventHandler<SummonCastEvent> {

  @Override
  @SneakyThrows
  public void onEvent(SummonCastEvent event, Supplier<EntityData> entityDataSupplier) {
    EntityData entityData = entityDataSupplier.get();
    int castedSummon = entityData.createEntity();
    SummonContainer summonContainer =
        ComponentsContainerSerializer.readSeriazableFromRessources(
            event.getSummonFile(), SummonContainer.class);

    for (Component component : summonContainer.getProperties()) {
      if (component instanceof MaxHealthComponent) {
        entityData.addComponent(
            castedSummon,
            new HealthPointsComponent(((MaxHealthComponent) component).getMaxHealth()));
        entityData.addComponent(castedSummon, component);
      } else if (component instanceof SpellOnTouchComponent) {
        SpellOnTouchComponent spell = (SpellOnTouchComponent) component;
        int spellEntity = entityData.createEntity();
        entityData.addComponent(castedSummon, new SpellOnTouchComponent(spellEntity));
        summonContainer
            .getComponents()
            .get(spell.getSpell())
            .forEach(c -> entityData.addComponent(spellEntity, c));

      } else {
        entityData.addComponent(castedSummon, component);
      }
    }

    TeamComponent team = entityData.getComponent(event.getSummonerEntity(), TeamComponent.class);
    entityData.addComponent(castedSummon, event.getSpawnPosition());
    entityData.addComponent(castedSummon, new TeamComponent(team.getTeam()));
    entityData.addComponent(castedSummon, new BuffsComponent(Lists.newArrayList()));
    entityData.addComponent(castedSummon, new SummonComponent(event.getSummonerEntity()));

    Set<Integer> newActiveSummons =
        entityData.hasComponents(event.getSummonerEntity(), ActiveSummonsComponent.class)
            ? new LinkedHashSet<>(entityData.getComponent(event.getSummonerEntity(), ActiveSummonsComponent.class).getActiveSummons())
            : new LinkedHashSet<>();

    newActiveSummons.add(castedSummon);
    entityData.addComponent(event.getSummonerEntity(), new ActiveSummonsComponent(newActiveSummons));
  }
}
