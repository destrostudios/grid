package com.destrostudios.grid.client.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GuiSpell {
    private String name;
    private String tooltip;
    private Integer remainingCooldown;
    private boolean isCostPayable;
    private boolean isTargeting;
    private Runnable cast;
}
