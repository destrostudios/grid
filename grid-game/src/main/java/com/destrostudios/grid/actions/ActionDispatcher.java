package com.destrostudios.grid.actions;

import com.destrostudios.grid.components.character.ActiveTurnComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.entities.EntityData;
import com.destrostudios.grid.eventbus.Event;
import com.destrostudios.grid.eventbus.action.spellcasted.SpellCastedEvent;
import com.destrostudios.grid.eventbus.action.walk.WalkEvent;
import com.destrostudios.grid.eventbus.update.turn.UpdatedTurnEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class ActionDispatcher {
  private final Supplier<EntityData> entityDataSupplier;

  // todo generate all possible actions

  public Event dispatchAction(Action action) throws ActionNotAllowedException {
    int entity = Integer.parseInt(action.getPlayerIdentifier());
    ActiveTurnComponent component =
        entityDataSupplier.get().getComponent(entity, ActiveTurnComponent.class);

    if (component == null) {
      throw new ActionNotAllowedException("not player turn");
    } else if (action instanceof PositionUpdateAction actionCasted) {
      return new WalkEvent(entity, new PositionComponent(actionCasted.getNewX(), actionCasted.getNewY()));
    } else if (action instanceof SkipRoundAction) {
      return new UpdatedTurnEvent(entity);
    } else if (action instanceof CastSpellAction actionCasted) {
      return new SpellCastedEvent(actionCasted.getSpell(), entity, ((CastSpellAction) action).getTargetX(),
              actionCasted.getTargetY());
    }
    throw new ActionNotAllowedException("Unsupported Action");
  }
}
