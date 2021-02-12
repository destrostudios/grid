package com.destrostudios.grid.gamestate;

import com.destrostudios.grid.components.AttackPointsComponent;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.HealthPointsComponent;
import com.destrostudios.grid.components.MaxHealthComponent;
import com.destrostudios.grid.components.MovementPointsComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;
import com.destrostudios.grid.components.RoundComponent;
import com.destrostudios.grid.components.TeamComponent;
import com.destrostudios.grid.components.TreeComponent;
import com.destrostudios.grid.components.WalkableComponent;
import com.destrostudios.grid.components.spells.AttackPointCostComponent;
import com.destrostudios.grid.components.spells.DamageComponent;
import com.destrostudios.grid.components.spells.SpellComponent;
import com.destrostudios.grid.eventbus.events.AttackPointsChangedEvent;

import java.util.Arrays;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ComponentAdapter extends XmlAdapter<String, Component> {
    public static final String CLASS_SEPERATOR = ":";
    public static final String VALUE_SEPERATOR = ",";

    @Override
    public Component unmarshal(String s) throws Exception {
        String[] split = s.split(CLASS_SEPERATOR);
        if (split.length == 0) {
            return null;
        }
        AdapterValues adapterValues = AdapterValues.fromSimpleName(split[0]);

        switch (adapterValues) {
            case ATTACK_POINTS_COST:
                return new AttackPointCostComponent(Integer.parseInt(split[1]));
            case DAMAGE:
                return new DamageComponent(Integer.parseInt(split[1]));
            case SPELL:
                return new SpellComponent(Integer.parseInt(split[1]));
            case POSITION:
                String[] values = split[1].split(VALUE_SEPERATOR);
                return new PositionComponent(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
            case MOVEMENT_POINTS:
                return new MovementPointsComponent(Integer.parseInt(split[1]));
            case ATTACK_POINTS:
                return new AttackPointsComponent(Integer.parseInt(split[1]));
            case MAX_HEALTH_POINTS:
                return new MaxHealthComponent(Integer.parseInt(split[1]));
            case HEALTH_POINTS:
                return new HealthPointsComponent(Integer.parseInt(split[1]));
            case PLAYER:
                return new PlayerComponent(split[1]);
            case TEAM:
                return new TeamComponent(Integer.parseInt(split[1]));
            case ACTIVE_ROUND:
                return new RoundComponent();
            case WALKABLE:
                return new WalkableComponent();
            case TREE:
                return new TreeComponent();
            case DEFAUlT:
                break;
        }
        return null;
    }

    @Override
    public String marshal(Component component) throws Exception {
        return component.toMarshalString();
    }

    public enum AdapterValues {
        ATTACK_POINTS_COST(AttackPointsComponent.class),
        DAMAGE(DamageComponent.class),
        SPELL(SpellComponent.class),
        POSITION(PositionComponent.class),
        MOVEMENT_POINTS(MovementPointsComponent.class),
        ATTACK_POINTS(AttackPointsComponent.class),
        MAX_HEALTH_POINTS(MaxHealthComponent.class),
        HEALTH_POINTS(HealthPointsComponent.class),
        PLAYER(PlayerComponent.class),
        TEAM(TeamComponent.class),
        ACTIVE_ROUND(RoundComponent.class),
        WALKABLE(WalkableComponent.class),
        TREE(TreeComponent.class),
        DEFAUlT(Component.class);

        private final Class<? extends Component> classz;

        AdapterValues(Class<? extends Component> classz) {
            this.classz = classz;
        }

        public Class<? extends Component> getClassz() {
            return classz;
        }

        public static AdapterValues fromClass(Class<? extends Component> classz) {
            return Arrays.stream(AdapterValues.values()).filter(e -> e.getClassz().equals(classz))
                    .findFirst()
                    .orElse(DEFAUlT);
        }

        public static AdapterValues fromSimpleName(String simpleName) {
            return Arrays.stream(AdapterValues.values()).filter(e -> e.getClassz().getSimpleName().equals(simpleName))
                    .findFirst()
                    .orElse(DEFAUlT);
        }
    }
}
