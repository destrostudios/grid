package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.events.Event;
import com.destrostudios.grid.eventbus.events.MoveEvent;
import com.destrostudios.grid.eventbus.events.RoundSkippedEvent;
import com.destrostudios.grid.eventbus.events.SpellCastedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class ActionDispatcher {
    private final Supplier<EntityWorld> getEntityWorld;

    public Event dispatchAction(Action action) throws ActionNotAllowedException {
        int entity = Integer.parseInt(action.getPlayerIdentifier());
        Optional<RoundComponent> component = getEntityWorld.get().getComponent(entity, RoundComponent.class);

        if (component.isEmpty()) {
            throw new ActionNotAllowedException("not player turn");
        } else if (action instanceof PositionUpdateAction) {
            PositionUpdateAction posAction = (PositionUpdateAction) action;
            return new MoveEvent(entity, new PositionComponent(posAction.getNewX(), posAction.getNewY()));
        } else if (action instanceof SkipRoundAction) {
            return new RoundSkippedEvent(entity);
        } else if (action instanceof CastSpellAction) {
            CastSpellAction castSpellAction = (CastSpellAction) action;
            return new SpellCastedEvent(castSpellAction.getSpell(), entity, calculateTargetEntity(((CastSpellAction) action).getTargetX(), castSpellAction.getTargetY()));
        } else {
            throw new ActionNotAllowedException("Unsupported Action");
        }
    }

    private int calculateTargetEntity(int x, int y) {
        EntityWorld world = getEntityWorld.get();
        Optional<Integer> targetEntity = world.list(PositionComponent.class).stream()
                .filter(e -> world.getComponent(e, PositionComponent.class).get().getX() == x
                        && world.getComponent(e, PositionComponent.class).get().getY() == y)
                .min((e1, e2) -> Boolean.compare(world.hasComponents(e2, PlayerComponent.class), world.hasComponents(e1, PlayerComponent.class)));

        return targetEntity.orElse(-1);
    }
}
