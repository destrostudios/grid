package com.destrostudios.grid.components;

import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.components.spells.SpellComponent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "c")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttackPointCostComponent.class, name = "apc"),
        @JsonSubTypes.Type(value = DamageComponent.class, name = "dmg"),
        @JsonSubTypes.Type(value = SpellComponent.class, name = "spell"),
        @JsonSubTypes.Type(value = AttackPointsComponent.class, name = "ap"),
        @JsonSubTypes.Type(value = HealthPointsComponent.class, name = "hp"),
        @JsonSubTypes.Type(value = MaxHealthComponent.class, name = "mhp"),
        @JsonSubTypes.Type(value = MovementPointsComponent.class, name = "mp"),
        @JsonSubTypes.Type(value = NameComponent.class, name = "name"),
        @JsonSubTypes.Type(value = PlayerComponent.class, name = "player"),
        @JsonSubTypes.Type(value = PositionComponent.class, name = "pos"),
        @JsonSubTypes.Type(value = RoundComponent.class, name = "round"),
        @JsonSubTypes.Type(value = TeamComponent.class, name = "team"),
        @JsonSubTypes.Type(value = TreeComponent.class, name = "tree"),
        @JsonSubTypes.Type(value = WalkableComponent.class, name = "wkbl"),
        @JsonSubTypes.Type(value = ObstacleComponent.class, name = "osbl"),
        @JsonSubTypes.Type(value = StartingFieldComponent.class, name = "start")
})
public interface Component {

}
