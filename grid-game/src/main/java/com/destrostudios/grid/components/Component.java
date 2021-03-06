package com.destrostudios.grid.components;

import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.character.TurnComponent;
import com.destrostudios.grid.components.character.TeamComponent;
import com.destrostudios.grid.components.map.*;
import com.destrostudios.grid.components.properties.*;
import com.destrostudios.grid.components.properties.resistence.AttackPointResistenceComponent;
import com.destrostudios.grid.components.properties.resistence.MovementPointResistenceComponent;
import com.destrostudios.grid.components.spells.base.DamageComponent;
import com.destrostudios.grid.components.spells.base.HealComponent;
import com.destrostudios.grid.components.spells.base.TooltipComponent;
import com.destrostudios.grid.components.spells.buffs.*;
import com.destrostudios.grid.components.spells.limitations.CooldownComponent;
import com.destrostudios.grid.components.spells.limitations.CostComponent;
import com.destrostudios.grid.components.spells.limitations.OnCooldownComponent;
import com.destrostudios.grid.components.spells.movements.DisplacementComponent;
import com.destrostudios.grid.components.spells.movements.TeleportComponent;
import com.destrostudios.grid.components.spells.perturn.*;
import com.destrostudios.grid.components.spells.range.AffectedAreaComponent;
import com.destrostudios.grid.components.spells.range.RangeComponent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "component")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CastsPerTurnComponent.class, name = "CastsPerTurnComponent"),
        @JsonSubTypes.Type(value = CostComponent.class, name = "CostComponent"),
        @JsonSubTypes.Type(value = HealPerTurnComponent.class, name = "HealPerTurnComponent"),
        @JsonSubTypes.Type(value = DamageComponent.class, name = "DamageComponent"),
        @JsonSubTypes.Type(value = HealComponent.class, name = "HealComponent"),
        @JsonSubTypes.Type(value = SpellsComponent.class, name = "SpellsComponent"),
        @JsonSubTypes.Type(value = AttackPointsComponent.class, name = "AttackPointsComponent"),
        @JsonSubTypes.Type(value = HealthPointsComponent.class, name = "HealthPointsComponent"),
        @JsonSubTypes.Type(value = MaxHealthComponent.class, name = "MaxHealthComponent"),
        @JsonSubTypes.Type(value = MovementPointsComponent.class, name = "MovementPointsComponent"),
        @JsonSubTypes.Type(value = NameComponent.class, name = "NameComponent"),
        @JsonSubTypes.Type(value = PlayerComponent.class, name = "PlayerComponent"),
        @JsonSubTypes.Type(value = PositionComponent.class, name = "PositionComponent"),
        @JsonSubTypes.Type(value = TurnComponent.class, name = "TurnComponent"),
        @JsonSubTypes.Type(value = TeamComponent.class, name = "TeamComponent"),
        @JsonSubTypes.Type(value = VisualComponent.class, name = "VisualComponent"),
        @JsonSubTypes.Type(value = WalkableComponent.class, name = "WalkableComponent"),
        @JsonSubTypes.Type(value = ObstacleComponent.class, name = "ObstacleComponent"),
        @JsonSubTypes.Type(value = MaxAttackPointsComponent.class, name = "MaxAttackPointsComponent"),
        @JsonSubTypes.Type(value = MaxMovementPointsComponent.class, name = "MaxMovementPointsComponent"),
        @JsonSubTypes.Type(value = TooltipComponent.class, name = "TooltipComponent"),
        @JsonSubTypes.Type(value = RangeComponent.class, name = "RangeComponent"),
        @JsonSubTypes.Type(value = AttackPointsBuffComponent.class, name = "AttackPointsBuffComponent"),
        @JsonSubTypes.Type(value = MovementPointBuffComponent.class, name = "MovementPointBuffComponent"),
        @JsonSubTypes.Type(value = HealthPointBuffComponent.class, name = "HealthPointBuffComponent"),
        @JsonSubTypes.Type(value = AttackPointsPerTurnComponent.class, name = "AttackPointsPoisonComponent"),
        @JsonSubTypes.Type(value = MovementPointsPerTurnComponent.class, name = "MovementPointsPoisonComponent"),
        @JsonSubTypes.Type(value = DamagePerTurnComponent.class, name = "DamagePerTurnComponent"),
        @JsonSubTypes.Type(value = TeleportComponent.class, name = "TeleportComponent"),
        @JsonSubTypes.Type(value = OnCooldownComponent.class, name = "OnCooldownComponent"),
        @JsonSubTypes.Type(value = CooldownComponent.class, name = "CooldownComponent"),
        @JsonSubTypes.Type(value = BuffsComponent.class, name = "BuffsComponent"),
        @JsonSubTypes.Type(value = StartingFieldComponent.class, name = "StartingFieldComponent"),
        @JsonSubTypes.Type(value = DamageBuffComponent.class, name = "DamageBuffComponent"),
        @JsonSubTypes.Type(value = AttackPointResistenceComponent.class, name = "AttackPointResistenceComponent"),
        @JsonSubTypes.Type(value = MovementPointResistenceComponent.class, name = "MovementPointResistenceComponent"),
        @JsonSubTypes.Type(value = DisplacementComponent.class, name = "DisplacementComponent"),
        @JsonSubTypes.Type(value = HealBuffComponent.class, name = "HealBuffComponent"),
        @JsonSubTypes.Type(value = AffectedAreaComponent.class, name = "AffectedAreaComponent"),
        @JsonSubTypes.Type(value = StatsPerRoundComponent.class, name = "StatsPerRoundComponent")
})
public interface Component {
}
