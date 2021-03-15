package com.destrostudios.grid.client.appstates;

import com.destroflyer.jme3.cubes.Block;
import com.destroflyer.jme3.cubes.BlockNavigator;
import com.destroflyer.jme3.cubes.BlockTerrainControl;
import com.destroflyer.jme3.cubes.Vector3Int;
import com.destrostudios.grid.client.BaseApplication;
import com.destrostudios.grid.client.PositionUtil;
import com.destrostudios.grid.client.blocks.BlockAssets;
import com.destrostudios.grid.client.blocks.GridBlock;
import com.destrostudios.grid.client.blocks.GridBlocks;
import com.destrostudios.grid.client.characters.ModelInfo;
import com.destrostudios.grid.client.characters.ModelInfos;
import com.destrostudios.grid.client.characters.EntityVisual;
import com.destrostudios.grid.client.maps.Map;
import com.destrostudios.grid.client.maps.Maps;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.map.PositionComponent;
import com.destrostudios.grid.components.map.VisualComponent;
import com.destrostudios.grid.components.map.WalkableComponent;
import com.destrostudios.grid.components.properties.HealthPointsComponent;
import com.destrostudios.grid.components.properties.MaxHealthComponent;
import com.destrostudios.grid.components.properties.NameComponent;
import com.destrostudios.grid.entities.EntityData;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MapAppState extends BaseAppState<BaseApplication> {

    private Map map;
    @Getter
    private EntityData entityData;
    private int mapSizeX;
    private int mapSizeY;
    private Node rootNode;
    private Node guiNode;
    private Node blockTerrainNode;
    private BlockTerrainControl blockTerrainControl;
    private HashMap<Integer, EntityVisual> entityVisuals = new HashMap<>();
    private List<Integer> validGroundEntities = new LinkedList<>();
    private List<Integer> invalidGroundEntities = new LinkedList<>();
    private List<Integer> impactedGroundEntities = new LinkedList<>();
    private List<Integer> reachableGroundEntities = new LinkedList<>();
    private List<Integer> tmpRemovedEntities = new LinkedList<>();

    public MapAppState(String mapName, EntityData entityData) {
        this.map = Maps.get(mapName);
        this.entityData = entityData;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application application) {
        super.initialize(stateManager, application);
        rootNode = new Node();
        mainApplication.getRootNode().attachChild(rootNode);

        guiNode = new Node();
        mainApplication.getGuiNode().attachChild(guiNode);

        calculateMapSize();

        if (map.getEnvironmentBlock() != null) {
            Node environmentNode = new Node();
            environmentNode.setShadowMode(RenderQueue.ShadowMode.Receive);
            BlockTerrainControl environmentBlockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, mapSizeX, mapSizeY, new Vector3Int(3, 1, 3));
            // Fill a total of 3x3 of the terrain size
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(0, 1, 0), new Vector3Int(3 * mapSizeX, 1, 3 * mapSizeY), map.getEnvironmentBlock());
            // Remove the "inner" part, where the actual map terran will be
            environmentBlockTerrainControl.removeBlockArea(new Vector3Int(mapSizeX, 1, mapSizeY), new Vector3Int(mapSizeX, 1, mapSizeY));
            // Add a layer below the "inner" part, where the actual map terran will be
            environmentBlockTerrainControl.setBlockArea(new Vector3Int(mapSizeX, 0, mapSizeY), new Vector3Int(mapSizeX, 1, mapSizeY), map.getEnvironmentBlock());
            environmentNode.addControl(environmentBlockTerrainControl);
            environmentNode.setLocalTranslation(-1 * mapSizeX * BlockAssets.BLOCK_SIZE, -1 * BlockAssets.BLOCK_SIZE, -1 * mapSizeY * BlockAssets.BLOCK_SIZE);
            rootNode.attachChild(environmentNode);
        }

        blockTerrainNode = new Node();
        blockTerrainNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        blockTerrainControl = BlockAssets.createNewBlockTerrain(mainApplication, mapSizeX, mapSizeY, new Vector3Int(1, 1, 1));
        blockTerrainNode.addControl(blockTerrainControl);
        rootNode.attachChild(blockTerrainNode);

        Camera camera = mainApplication.getCamera();
        camera.setLocation(map.getCameraPosition());
        camera.setRotation(map.getCameraRotation());

        updateVisuals();
    }

    private void calculateMapSize() {
        int maxX = -1;
        int maxY = -1;
        for (int groundEntity : entityData.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityData.getComponent(groundEntity, PositionComponent.class);
            if (positionComponent.getX() > maxX) {
                maxX = positionComponent.getX();
            }
            if (positionComponent.getY() > maxY) {
                maxY = positionComponent.getY();
            }
        }
        mapSizeX = (maxX + 1);
        mapSizeY = (maxY + 1);
    }

    public void updateVisuals() {
        updateTerrain();

        entityVisuals.forEach((entity, entityVisual) -> {
            if (!entityData.hasEntity(entity)) {
                rootNode.detachChild(entityVisual.getModelObject());
                guiNode.detachChild(entityVisual.getLblName());
                guiNode.detachChild(entityVisual.getHealthBar());
                tmpRemovedEntities.add(entity);
            }
        });
        for (int entityToRemove : tmpRemovedEntities) {
            entityVisuals.remove(entityToRemove);
        }
        tmpRemovedEntities.clear();
        for (int entity : entityData.list(VisualComponent.class)) {
            EntityVisual entityVisual = entityVisuals.get(entity);

            if (entityVisual == null) {
                String visualName = entityData.getComponent(entity, VisualComponent.class).getName();
                ModelInfo modelInfo = ModelInfos.get(visualName);
                // Ground entities or other stuff without an implicit model
                if (modelInfo == null) {
                    continue;
                }
                entityVisual = new EntityVisual(mainApplication.getCamera(), mainApplication.getAssetManager(), modelInfo, map.getPlayerNameColor());
                rootNode.attachChild(entityVisual.getModelObject());
                entityVisuals.put(entity, entityVisual);
            }

            // Position
            PositionComponent positionComponent = entityData.getComponent(entity, PositionComponent.class);
            ModelObject modelObject = entityVisual.getModelObject();
            modelObject.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), 3, PositionUtil.get3dCoordinate(positionComponent.getY()));

            // Name
            NameComponent nameComponent = entityData.getComponent(entity, NameComponent.class);
            if (nameComponent != null) {
                entityVisual.getLblName().setText(nameComponent.getName());
                guiNode.attachChild(entityVisual.getLblName());
            } else {
                guiNode.detachChild(entityVisual.getLblName());
            }

            // Health
            MaxHealthComponent maxHealthComponent = entityData.getComponent(entity, MaxHealthComponent.class);
            if (maxHealthComponent != null) {
                entityVisual.setMaximumHealth(maxHealthComponent.getMaxHealth());
            }
            HealthPointsComponent healthPointsComponent = entityData.getComponent(entity, HealthPointsComponent.class);
            if (healthPointsComponent != null) {
                entityVisual.setCurrentHealth(healthPointsComponent.getHealth());
            }
            if ((maxHealthComponent != null) && (healthPointsComponent != null)) {
                guiNode.attachChild(entityVisual.getHealthBar());
            } else {
                guiNode.detachChild(entityVisual.getHealthBar());
            }
        }
    }

    public void setValidGroundEntities(List<Integer> validGroundEntities, List<Integer> invalidGroundEntities) {
        this.validGroundEntities = validGroundEntities;
        this.invalidGroundEntities = invalidGroundEntities;
        updateTerrain();
    }

    public void setImpactedGroundEntities(List<Integer> impactedGroundEntities) {
        this.impactedGroundEntities = impactedGroundEntities;
        updateTerrain();
    }

    public void setReachableGroundEntities(List<Integer> reachableGroundEntities) {
        this.reachableGroundEntities = reachableGroundEntities;
        updateTerrain();
    }

    private void updateTerrain() {
        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(mapSizeX, 1, mapSizeY));
        for (int groundEntity : entityData.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityData.getComponent(groundEntity, PositionComponent.class);
            String gridBlockName = entityData.getComponent(groundEntity, VisualComponent.class).getName();
            GridBlock gridBlock = GridBlocks.get(gridBlockName);
            Block block;
            if (impactedGroundEntities.contains(groundEntity) || reachableGroundEntities.contains(groundEntity)) {
                block = gridBlock.getBlockImpacted();
            } else if (invalidGroundEntities.contains(groundEntity)) {
                block = gridBlock.getBlockInvalid();
            } else if (validGroundEntities.contains(groundEntity)) {
                block = gridBlock.getBlockValid();
            } else {
                block = gridBlock.getBlockGrid();
            }
            blockTerrainControl.setBlock(new Vector3Int(positionComponent.getX(), 0, positionComponent.getY()), block);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getRootNode().detachChild(rootNode);
        mainApplication.getGuiNode().detachChild(guiNode);
    }

    public EntityVisual getEntityVisual(int playerEntity) {
        return entityVisuals.get(playerEntity);
    }

    public Vector3Int getHoveredPosition(boolean getNeighborLocation) {
        CollisionResults results = mainApplication.getRayCastingResults_Cursor(blockTerrainNode);
        if (results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            return BlockNavigator.getPointedBlockLocation(blockTerrainControl, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }
}
