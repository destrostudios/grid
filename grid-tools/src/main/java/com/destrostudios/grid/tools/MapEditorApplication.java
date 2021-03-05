package com.destrostudios.grid.tools;

import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.BaseApplication;
import com.destrostudios.grid.client.appstates.MapAppState;
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
import com.jme3.input.controls.MouseButtonTrigger;

import javax.swing.*;
import java.io.IOException;

public class MapEditorApplication extends BaseApplication implements ActionListener {

    private static final String MAP_NAME = "DestroMap";

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        inputManager.addMapping("add", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("remove", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("save", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "add", "remove", "save");

        loadMap(MAP_NAME);
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
                    Vector3Int positionToAdd = mapAppState.getHoveredPosition(true);
                    if ((positionToAdd != null) && (positionToAdd.getY() == 0)) {
                        int groundEntity = entityWorld.createEntity();
                        entityWorld.addComponent(groundEntity, new PositionComponent(positionToAdd.getX(), positionToAdd.getZ()));
                        entityWorld.addComponent(groundEntity, new WalkableComponent());
                        entityWorld.addComponent(groundEntity, new VisualComponent(getSampleGroundVisual(entityWorld)));
                        mapAppState.updateVisuals();
                    }
                    break;
                case "remove":
                    Vector3Int positionToRemove = mapAppState.getHoveredPosition(false);
                    if (positionToRemove != null) {
                        int groundEntity = getGroundEntity(entityWorld, positionToRemove.getX(), positionToRemove.getZ());
                        entityWorld.removeEntity(groundEntity);
                        mapAppState.updateVisuals();
                    }
                    break;
                case "save":
                    saveMap(MAP_NAME);
                    break;
            }
        }
    }

    private Integer getGroundEntity(EntityWorld entityWorld, int x, int y) {
        return entityWorld.list(PositionComponent.class, WalkableComponent.class).stream()
                .filter(entity -> {
                    PositionComponent positionComponent = entityWorld.getComponent(entity, PositionComponent.class);
                    return ((positionComponent.getX() == x) && (positionComponent.getY() == y));
                })
                .findFirst()
                .orElse(null);
    }

    private String getSampleGroundVisual(EntityWorld entityWorld) {
        int groundEntity = entityWorld.list(WalkableComponent.class, VisualComponent.class).get(0);
        return entityWorld.getComponent(groundEntity, VisualComponent.class).getName();
    }
}
