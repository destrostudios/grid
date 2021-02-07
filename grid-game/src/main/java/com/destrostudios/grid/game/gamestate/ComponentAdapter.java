package com.destrostudios.grid.game.gamestate;

import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.MovingComponent;
import com.destrostudios.grid.components.PlayerComponent;
import com.destrostudios.grid.components.PositionComponent;

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
            case MOVING:
                return new MovingComponent();
            case PLAYER:
                return new PlayerComponent(split[1]);
            case DEFAUlT:
                break;
        }
        return null;
    }

    @Override
    public String marshal(Component component) throws Exception {
        AdapterValues adapterValues = AdapterValues.fromClass(component.getClass());

        switch (adapterValues) {
            case POSITION:
                PositionComponent positionComponent = (PositionComponent) AdapterValues.POSITION.getClassz().cast(component);
                return positionComponent.toMarshalString();
            case MOVING:
                MovingComponent movingComponent = (MovingComponent) AdapterValues.MOVING.getClassz().cast(component);
                return movingComponent.toMarshalString();
            case PLAYER:
                PlayerComponent playerComponent = (PlayerComponent) AdapterValues.PLAYER.getClassz().cast(component);
                return playerComponent.toMarshalString();
            case DEFAUlT:
                break;

        }
        return "";
    }

    public enum AdapterValues {
        POSITION(PositionComponent.class),
        MOVING(MovingComponent.class),
        PLAYER(PlayerComponent.class),
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
