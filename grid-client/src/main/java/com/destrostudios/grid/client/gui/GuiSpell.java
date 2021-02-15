package com.destrostudios.grid.client.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuiSpell {
    private String name;
    private Runnable cast;
}
