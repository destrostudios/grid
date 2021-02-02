package com.destrostudios.grid.client;

import com.destrostudios.grid.client.appstates.GameAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.water.WaterFilter;

import java.awt.image.BufferedImage;

public class ClientApplication extends SimpleApplication {

    public ClientApplication() {
        settings = new AppSettings(true);
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setVSync(true);
        settings.setTitle("Grid");
        settings.setIcons(new BufferedImage[] {
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
        Vector3f lightDirection = new Vector3f(-0.8f, -1, -0.8f).normalizeLocal();
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

        stateManager.attach(new GameAppState());
    }
}
