package com.destrostudios.grid.eventbus.action.gameover;

import com.destrostudios.grid.eventbus.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameOverEvent implements Event {
  private final int winnerTeam;
}
