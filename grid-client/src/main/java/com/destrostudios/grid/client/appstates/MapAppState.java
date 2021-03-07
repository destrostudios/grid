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
import com.destrostudios.grid.client.characters.CharacterModel;
import com.destrostudios.grid.client.characters.CharacterModels;
import com.destrostudios.grid.client.characters.PlayerVisual;
import com.destrostudios.grid.client.maps.Map;
import com.destrostudios.grid.client.maps.Maps;
import com.destrostudios.grid.client.models.ModelObject;
import com.destrostudios.grid.components.Component;
import com.destrostudios.grid.components.character.PlayerComponent;
import com.destrostudios.grid.components.map.ObstacleComponent;
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
import com.simsilica.lemur.Label;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

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
    private HashMap<Integer, PlayerVisual> playerVisuals = new HashMap<>();
    private HashMap<Integer, ModelObject> obstacleModels = new HashMap<>();
    private List<Integer> validTargetEntities = new LinkedList<>();
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

        // Obstacles
        obstacleModels.forEach((obstacleEntity, modelObject) -> {
            if (!hasEntity(entityData, obstacleEntity)) {
                rootNode.detachChild(modelObject);
                tmpRemovedEntities.add(obstacleEntity);
            }
        });
        for (int entityToRemove : tmpRemovedEntities) {
            obstacleModels.remove(entityToRemove);
        }
        tmpRemovedEntities.clear();
        List<Integer> obstacleEntities = entityData.list(ObstacleComponent.class).stream()
                .filter(entity -> !entityData.hasComponents(entity, PlayerComponent.class))
                .collect(Collectors.toList());
        for (int obstacleEntity : obstacleEntities) {
            ModelObject obstacleModel = obstacleModels.computeIfAbsent(obstacleEntity, pe -> {
                String modelName = entityData.getComponent(obstacleEntity, VisualComponent.class).getName();
                ModelObject newObstacleModel = new ModelObject(mainApplication.getAssetManager(), "models/" + modelName + "/skin.xml");
                rootNode.attachChild(newObstacleModel);
                return newObstacleModel;
            });

            PositionComponent positionComponent = entityData.getComponent(obstacleEntity, PositionComponent.class);
            obstacleModel.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), PositionUtil.CHARACTER_Y, PositionUtil.get3dCoordinate(positionComponent.getY()));
        }

        // Players
        playerVisuals.forEach((playerEntity, playerVisual) -> {
            if (!hasEntity(entityData, playerEntity)) {
                rootNode.detachChild(playerVisual.getModelObject());
                guiNode.detachChild(playerVisual.getLblName());
                guiNode.detachChild(playerVisual.getHealthBar());
                tmpRemovedEntities.add(playerEntity);
            }
        });
        for (int entityToRemove : tmpRemovedEntities) {
            playerVisuals.remove(entityToRemove);
        }
        tmpRemovedEntities.clear();
        for (int playerEntity : entityData.list(PlayerComponent.class)) {
            PlayerVisual playerVisual = playerVisuals.computeIfAbsent(playerEntity, pe -> {
                String characterName = entityData.getComponent(playerEntity, VisualComponent.class).getName();
                CharacterModel characterModel = CharacterModels.get(characterName);
                PlayerVisual newPlayerVisual = new PlayerVisual(mainApplication.getCamera(), mainApplication.getAssetManager(), characterModel, map.getPlayerNameColor());
                rootNode.attachChild(newPlayerVisual.getModelObject());
                guiNode.attachChild(newPlayerVisual.getLblName());
                guiNode.attachChild(newPlayerVisual.getHealthBar());
                return newPlayerVisual;
            });

            ModelObject modelObject = playerVisual.getModelObject();
            PositionComponent positionComponent = entityData.getComponent(playerEntity, PositionComponent.class);
            HealthPointsComponent healthPointsComponent = entityData.getComponent(playerEntity, HealthPointsComponent.class);
            MaxHealthComponent maxHealthComponent = entityData.getComponent(playerEntity, MaxHealthComponent.class);
            modelObject.setLocalTranslation(PositionUtil.get3dCoordinate(positionComponent.getX()), 3, PositionUtil.get3dCoordinate(positionComponent.getY()));

            Label lblName = playerVisual.getLblName();
            String name = entityData.getComponent(playerEntity, NameComponent.class).getName();
            lblName.setText(name);

            playerVisual.setMaximumHealth(maxHealthComponent.getMaxHealth());
            playerVisual.setCurrentHealth(healthPointsComponent.getHealth());
        }
    }

    private boolean hasEntity(EntityData entityData, int entity) {
        List<Component> components = entityData.getComponents(entity);
        return ((components != null) && (components.size() > 0));
    }

    public void setValidTargetEntities(List<Integer> validTargetEntities) {
        this.validTargetEntities = validTargetEntities;
        updateTerrain();
    }

    private void updateTerrain() {
        blockTerrainControl.removeBlockArea(new Vector3Int(), new Vector3Int(mapSizeX, 1, mapSizeY));
        for (int groundEntity : entityData.list(WalkableComponent.class)) {
            PositionComponent positionComponent = entityData.getComponent(groundEntity, PositionComponent.class);
            String gridBlockName = entityData.getComponent(groundEntity, VisualComponent.class).getName();
            GridBlock gridBlock = GridBlocks.get(gridBlockName);
            Block block = (validTargetEntities.contains(groundEntity) ? gridBlock.getBlockTopTarget() : gridBlock.getBlockTopGrid());
            blockTerrainControl.setBlock(new Vector3Int(positionComponent.getX(), 0, positionComponent.getY()), block);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mainApplication.getRootNode().detachChild(rootNode);
        mainApplication.getRootNode().detachChild(guiNode);
    }

    public PlayerVisual getPlayerVisual(int playerEntity) {
        return playerVisuals.get(playerEntity);
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
