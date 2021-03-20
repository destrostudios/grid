package com.destrostudios.grid.components.spells.range;

public enum SpellAreaShape {
    DIAMOND,
    SQUARE,
    PLUS,
    LINE,
    SINGLE;

    public String toTooltipString() {
        switch (this) {
            case DIAMOND:
                return "diamond";
            case SQUARE:
                return "square";
            case PLUS:
                return "plus";
            case LINE:
                return "line";
            case SINGLE:
                return "single";
            default:
                return "";
        }
    }
}
