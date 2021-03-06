package com.destrostudios.grid.tools.mapeditor;

import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.BaseApplication;
import com.destrostudios.grid.client.appstates.MapAppState;
import com.destrostudios.grid.components.map.ObstacleComponent;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.VisualComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.entities.EntityWorld;
import com.destrostudios.grid.serialization.ComponentsContainerSerializer;
import com.destrostudios.grid.serialization.container.MapContainer;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.*;

import javax.swing.*;
import java.io.IOException;

public class MapEditorApplication extends BaseApplication implements ActionListener {

    private static final String MAP_NAME = "DestroMap";
    private static final String[] VISUALS_GROUND = new String[] { "grass", "sand", "snow" };
    private static final String[] VISUALS_OBSTACLES = new String[] { "tree", "rock" };

    private int marginX = 100;
    private int marginBottom = 50;
    private int barHeight = 80;
    private int buttonWidth = 150;
    private int containerToolWidth = 200;
    private MapEditorTool tool;
    private Label lblTool;
    private Label lblSelection;
    private int visualIndexGround;
    private int visualIndexObstacle;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        Container containerBackground = new Container();
        containerBackground.setLocalTranslation(marginX, marginBottom + barHeight, 0);
        containerBackground.setPreferredSize(new Vector3f(context.getSettings().getWidth() - (2 * marginX), barHeight, 0));
        guiNode.attachChild(containerBackground);

        Container containerTool = new Container();
        containerTool.setLocalTranslation(marginX, marginBottom + barHeight, 0);
        containerTool.setPreferredSize(new Vector3f(containerToolWidth, barHeight, 0));
        containerTool.setInsets(new Insets3f(0, 10, 0, 0));
        containerTool.setBackground(null);
        lblTool = new Label("");
        lblTool.setTextVAlignment(VAlignment.Center);
        lblTool.setFontSize(16);
        lblTool.setColor(ColorRGBA.White);
        containerTool.addChild(lblTool);
        lblSelection = new Label("");
        lblSelection.setTextVAlignment(VAlignment.Center);
        lblSelection.setFontSize(16);
        lblSelection.setColor(ColorRGBA.White);
        containerTool.addChild(lblSelection);
        guiNode.attachChild(containerTool);

        int x = (marginX + containerToolWidth);
        addButton("Ground", x, () -> setTool(MapEditorTool.GROUND));
        x += buttonWidth;
        addButton("Obstacle", x, () -> setTool(MapEditorTool.OBSTACLE));
        x += buttonWidth;
        addButton("Camera", x, () -> setTool(MapEditorTool.CAMERA));
        x += buttonWidth;
        addButton("Save", x, () -> saveMap(MAP_NAME));

        inputManager.addMapping("add", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("remove", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("previous", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("next", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, "add", "remove", "previous", "next");

        setTool(MapEditorTool.GROUND);

        loadMap(MAP_NAME);
    }

    private void addButton(String text, float x, Runnable action) {
        Button button = new Button(text);
        button.setLocalTranslation(x, marginBottom + barHeight, 0);
        button.setPreferredSize(new Vector3f(buttonWidth, barHeight, 0));
        button.setTextHAlignment(HAlignment.Center);
        button.setTextVAlignment(VAlignment.Center);
        button.setFontSize(16);
        button.setColor(ColorRGBA.White);
        button.addCommands(Button.ButtonAction.Up, source -> {
            action.run();
        });
        guiNode.attachChild(button);
    }

    private void setTool(MapEditorTool tool) {
        this.tool = tool;
        lblTool.setText("Tool: " + tool.name());
        flyCam.setEnabled(tool == MapEditorTool.CAMERA);
        updateSelectionText();
    }

    private void changeVisualIndexGround(int direction) {
        visualIndexGround = Math.max(0, Math.min(visualIndexGround + direction, VISUALS_GROUND.length - 1));
        updateSelectionText();
    }

    private void changeVisualIndexObstacle(int direction) {
        visualIndexObstacle = Math.max(0, Math.min(visualIndexObstacle + direction, VISUALS_OBSTACLES.length - 1));
        updateSelectionText();
    }

    private void updateSelectionText() {
        String selectionText = null;
        if (tool == MapEditorTool.GROUND) {
            selectionText = VISUALS_GROUND[visualIndexGround];
        } else if (tool == MapEditorTool.OBSTACLE) {
            selectionText = VISUALS_OBSTACLES[visualIndexObstacle];
        }
        lblSelection.setText((selectionText != null) ? "Selection: " + selectionText : "");
    }

    private void loadMap(String mapName) {
        MapAppState mapAppState = stateManager.getState(MapAppState.class);
        if (mapAppState != null) {
            stateManager.detach(mapAppState);
        }
        try {
            MapContainer mapContainer = ComponentsContainerSerializer.readSeriazableFromRessources(mapName, MapContainer.class);
            EntityWorld entityWorld = new EntityWorld();
            entityWorld.getWorld().putAll(mapContainer.getComponents());
            stateManager.attach(new MapAppState(mapName, entityWorld));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveMap(String mapName) {
        MapAppState mapAppState = stateManager.getState(MapAppState.class);
        MapContainer mapContainer = new MapContainer(mapAppState.getEntityWorld().getWorld());
        try {
            ComponentsContainerSerializer.writeSeriazableToResources(mapContainer, mapName);
            JOptionPane.showMessageDialog(null, "Map '" + mapName  + "' saved successfully.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAction(String actionName, boolean isPressed, float tpf) {
        MapAppState mapAppState = stateManager.getState(MapAppState.class);
        EntityWorld entityWorld = mapAppState.getEntityWorld();
        if (isPressed) {
            switch (actionName) {
                case "add":
                    Vector3Int positionToAdd = mapAppState.getHoveredPosition(tool == MapEditorTool.GROUND);
                    if ((positionToAdd != null) && (positionToAdd.getY() == 0)) {
                        if (tool == MapEditorTool.GROUND) {
                            int groundEntity = entityWorld.createEntity();
                            entityWorld.addComponent(groundEntity, new PositionComponent(positionToAdd.getX(), positionToAdd.getZ()));
                            entityWorld.addComponent(groundEntity, new WalkableComponent());
                            entityWorld.addComponent(groundEntity, new VisualComponent(VISUALS_GROUND[visualIndexGround]));
                            mapAppState.updateVisuals();
                        } else if (tool == MapEditorTool.OBSTACLE) {
                            int obstacleEntity = entityWorld.createEntity();
                            entityWorld.addComponent(obstacleEntity, new PositionComponent(positionToAdd.getX(), positionToAdd.getZ()));
                            entityWorld.addComponent(obstacleEntity, new ObstacleComponent());
                            entityWorld.addComponent(obstacleEntity, new VisualComponent(VISUALS_OBSTACLES[visualIndexObstacle]));
                            mapAppState.updateVisuals();
                        }
                    }
                    break;
                case "remove":
                    Vector3Int positionToRemove = mapAppState.getHoveredPosition(false);
                    if (positionToRemove != null) {
                        if (tool == MapEditorTool.GROUND) {
                            int groundEntity = getEntity(entityWorld, new Class[]{PositionComponent.class, WalkableComponent.class}, positionToRemove.getX(), positionToRemove.getZ());
                            entityWorld.removeEntity(groundEntity);
                            mapAppState.updateVisuals();
                        } else if (tool == MapEditorTool.OBSTACLE) {
                            Integer obstacleEntity = getEntity(entityWorld, new Class[]{PositionComponent.class, ObstacleComponent.class}, positionToRemove.getX(), positionToRemove.getZ());
                            if (obstacleEntity != null) {
                                entityWorld.removeEntity(obstacleEntity);
                                mapAppState.updateVisuals();
                            }
                        }
                    }
                    break;
                case "previous":
                    if (tool == MapEditorTool.GROUND) {
                        changeVisualIndexGround(-1);
                    } else if (tool == MapEditorTool.OBSTACLE) {
                        changeVisualIndexObstacle(-1);
                    }
                    break;
                case "next":
                    if (tool == MapEditorTool.GROUND) {
                        changeVisualIndexGround(1);
                    } else if (tool == MapEditorTool.OBSTACLE) {
                        changeVisualIndexObstacle(1);
                    }
                    break;
            }
        }
    }

    private Integer getEntity(EntityWorld entityWorld, Class<?>[] components, int x, int y) {
        return entityWorld.list(components).stream()
                .filter(entity -> {
                    PositionComponent positionComponent = entityWorld.getComponent(entity, PositionComponent.class);
                    return ((positionComponent.getX() == x) && (positionComponent.getY() == y));
                })
                .findFirst()
                .orElse(null);
    }
}
