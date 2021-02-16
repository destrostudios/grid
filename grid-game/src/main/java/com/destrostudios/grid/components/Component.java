package com.destrostudios.grid.components;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.RoundComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.*;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.spells.*;
import com.destrostudios.grid.components.spells.buffs.AttackPointsBuffComponent;
import com.destrostudios.grid.components.spells.buffs.HealthPointBuffComponent;
import com.destrostudios.grid.components.spells.buffs.MovementPointBuffComponent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "c")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttackPointCostComponent.class, name = "apc"),
        @JsonSubTypes.Type(value = DamageComponent.class, name = "dmg"),
        @JsonSubTypes.Type(value = SpellsComponent.class, name = "spell"),
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
        @JsonSubTypes.Type(value = MovementPointsCostComponent.class, name = "mpc"),
        @JsonSubTypes.Type(value = MaxAttackPointsComponent.class, name = "maxap"),
        @JsonSubTypes.Type(value = MaxMovementPointsComponent.class, name = "maxmp"),
        @JsonSubTypes.Type(value = TooltipComponent.class, name = "ttp"),
        @JsonSubTypes.Type(value = RangeComponent.class, name = "rng"),
        @JsonSubTypes.Type(value = AttackPointsBuffComponent.class, name = "apb"),
        @JsonSubTypes.Type(value = MovementPointBuffComponent.class, name = "mpb"),
        @JsonSubTypes.Type(value = HealthPointBuffComponent.class, name = "hpb"),
        @JsonSubTypes.Type(value = OnCooldownComponent.class, name = "oncd"),
        @JsonSubTypes.Type(value = CooldownComponent.class, name = "cd"),
        @JsonSubTypes.Type(value = BuffsComponent.class, name = "buffs"),
        @JsonSubTypes.Type(value = StartingFieldComponent.class, name = "start")
})
public interface Component {
}
