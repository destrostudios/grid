package com.destrostudios.grid.client;

import com.destrostudios.grid.client.appstates.GameAppState;
import com.destrostudios.grid.client.appstates.GameGuiAppState;
import com.destrostudios.grid.client.appstates.MenuAppState;
import com.destrostudios.grid.client.gameproxy.GameProxy;
import com.destrostudios.grid.shared.PlayerInfo;
import com.destrostudios.turnbasedgametools.network.client.ToolsClient;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import lombok.Getter;

import java.awt.image.BufferedImage;

public class ClientApplication extends SimpleApplication {

    @Getter
    private final ToolsClient toolsClient;
    @Getter
    private final PlayerInfo playerInfo;

    public ClientApplication(ToolsClient toolsClient, PlayerInfo playerInfo) {
        this.toolsClient = toolsClient;
        this.playerInfo = playerInfo;
        settings = new AppSettings(true);
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setVSync(true);
        settings.setTitle("Grid");
        settings.setIcons(new BufferedImage[]{
            FileAssets.getImage("textures/icon/16.png"),
            FileAssets.getImage("textures/icon/32.png"),
            FileAssets.getImage("textures/icon/64.png"),
            FileAssets.getImage("textures/icon/128.png")
        });
        setShowSettings(false);
        setPauseOnLostFocus(false);
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLocator(FileAssets.ROOT, FileLocator.class);
        setDisplayStatView(false);
        setDisplayFps(false);

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        rootNode.addLight(ambientLight);
        DirectionalLight directionalLight = new DirectionalLight();
        Vector3f lightDirection = new Vector3f(-0.5f, -1, -0.5f).normalizeLocal();
        directionalLight.setDirection(lightDirection);
        directionalLight.setColor(new ColorRGBA(1, 1, 1, 1));
        rootNode.addLight(directionalLight);

        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        shadowRenderer.setLight(directionalLight);
        shadowRenderer.setShadowIntensity(0.2f);
        viewPort.addProcessor(shadowRenderer);

        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(filterPostProcessor);

        WaterFilter waterFilter = new WaterFilter(rootNode, lightDirection);
        waterFilter.setUseFoam(false);
        waterFilter.setUseRipples(false);
        waterFilter.setReflectionDisplace(0);
        filterPostProcessor.addFilter(waterFilter);

        SSAOFilter ssaoFilter = new SSAOFilter(3, 25, 6, 0.1f);
        filterPostProcessor.addFilter(ssaoFilter);

        addSky("miramar");

        flyCam.setEnabled(false);

        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        stateManager.attach(new MenuAppState());
    }

    private void addSky(String skyName) {
        Texture textureWest = assetManager.loadTexture("textures/skies/" + skyName + "/left.png");
        Texture textureEast = assetManager.loadTexture("textures/skies/" + skyName + "/right.png");
        Texture textureNorth = assetManager.loadTexture("textures/skies/" + skyName + "/front.png");
        Texture textureSouth = assetManager.loadTexture("textures/skies/" + skyName + "/back.png");
        Texture textureUp = assetManager.loadTexture("textures/skies/" + skyName + "/up.png");
        Texture textureDown = assetManager.loadTexture("textures/skies/" + skyName + "/down.png");
        rootNode.attachChild(SkyFactory.createSky(assetManager, textureWest, textureEast, textureNorth, textureSouth, textureUp, textureDown));
    }

    public void startGame(GameProxy gameProxy) {
        stateManager.detach(stateManager.getState(MenuAppState.class));
        stateManager.attach(new GameGuiAppState());
        stateManager.attach(new GameAppState(gameProxy));
    }

    public CollisionResults getRayCastingResults_Cursor(Spatial spatial) {
        return getRayCastingResults_Screen(spatial, inputManager.getCursorPosition());
    }

    public CollisionResults getRayCastingResults_Screen(Spatial spatial, Vector2f screenLocation) {
        Vector3f cursorRayOrigin = cam.getWorldCoordinates(screenLocation, 0);
        Vector3f cursorRayDirection = cam.getWorldCoordinates(screenLocation, 1).subtractLocal(cursorRayOrigin).normalizeLocal();
        return getRayCastingResults(spatial, new Ray(cursorRayOrigin, cursorRayDirection));
    }

    private CollisionResults getRayCastingResults(Spatial spatial, Ray ray) {
        CollisionResults results = new CollisionResults();
        spatial.collideWith(ray, results);
        return results;
    }
}
