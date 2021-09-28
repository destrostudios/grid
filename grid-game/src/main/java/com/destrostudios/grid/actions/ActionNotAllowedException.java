package com.destrostudios.grid.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ActionNotAllowedException extends Exception {
  private final String message;
}
