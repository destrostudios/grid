package com.destrostudios.grid.components;

public interface SpellComponent extends Component {

    String getSpellName();
    int getApCost();
    int getBpCost();
    int getRange();
    RangeIndicator getRangeIndicator();
}
