package com.destrostudios.grid.client;

import com.destrostudios.grid.client.appstates.GameAppState;
import com.destrostudios.grid.client.appstates.GuiAppState;
import com.destrostudios.grid.client.gameproxy.GameProxy;
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
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.water.WaterFilter;
import java.awt.image.BufferedImage;

public class ClientApplication extends SimpleApplication {

    private final GameProxy gameProxy;

    public ClientApplication(GameProxy gameProxy) {
        this.gameProxy = gameProxy;
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

        flyCam.setMoveSpeed(100);
        flyCam.setEnabled(false);

        stateManager.attach(new GuiAppState());
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
