package com.destrostudios.grid.game.gamestate;

import com.destrostudios.grid.components.*;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;

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
        POSITION(PositionComponent.class),
        MOVEMENT_POINTS(MovementPointsComponent.class),
        ATTACK_POINTS(AttackPointsComponent.class),
        MAX_HEALTH_POINTS(MaxHealthComponent.class),
        HEALTH_POINTS(HealthPointsComponent.class),
        PLAYER(PlayerComponent.class),
        TEAM(TeamComponent.class),
        ACTIVE_ROUND(RoundComponent.class),
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
