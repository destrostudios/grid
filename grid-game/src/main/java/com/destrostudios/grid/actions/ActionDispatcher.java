package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.action.move.MoveEvent;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.update.round.RoundUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class ActionDispatcher {
    private final Supplier<EntityWorld> getEntityWorld;

    public Event dispatchAction(Action action) throws ActionNotAllowedException {
        int entity = Integer.parseInt(action.getPlayerIdentifier());
        TurnComponent component = getEntityWorld.get().getComponent(entity, TurnComponent.class);

        if (component == null) {
            throw new ActionNotAllowedException("not player turn");
        } else if (action instanceof PositionUpdateAction) {
            PositionUpdateAction posAction = (PositionUpdateAction) action;
            return new MoveEvent(entity, new PositionComponent(posAction.getNewX(), posAction.getNewY()));
        } else if (action instanceof SkipRoundAction) {
            return new RoundUpdatedEvent(entity);
        } else if (action instanceof CastSpellAction) {
            CastSpellAction castSpellAction = (CastSpellAction) action;
            return new SpellCastedEvent(castSpellAction.getSpell(), entity,
                    ((CastSpellAction) action).getTargetX(), castSpellAction.getTargetY());
        } else {
            throw new ActionNotAllowedException("Unsupported Action");
        }
    }

}
