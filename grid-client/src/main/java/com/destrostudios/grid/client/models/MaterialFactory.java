package com.destrostudios.grid.client.models;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;

public class MaterialFactory {

    public static final String DEFINITION_NAME_LIGHTING = "Common/MatDefs/Light/Lighting.j3md";

    public static Material generateLightingMaterial(AssetManager assetManager, ColorRGBA color) {
        Material material = new Material(assetManager, DEFINITION_NAME_LIGHTING);
        material.setBoolean("UseMaterialColors", true);
        material.setColor("Diffuse",  color);
        return material;
    }

    public static void generateAmbientColor(Material material, float ambient) {
        if ((material.getParam("Diffuse") != null) && (material.getParam("Ambient") != null)) {
            ColorRGBA diffuseColor = (ColorRGBA) (material.getParam("Diffuse").getValue());
            Vector4f newAmbient = diffuseColor.toVector4f().multLocal(ambient, ambient, ambient, 1);
            material.setVector4("Ambient", newAmbient);
        }
    }

    public static Material generateLightingMaterial(AssetManager assetManager, String textureFilePath) {
        return generateLightingMaterial(assetManager, textureFilePath, null);
    }

    public static Material generateLightingMaterial(AssetManager assetManager, String textureFilePath, String normalMapFilePath){
        Material material = new Material(assetManager, DEFINITION_NAME_LIGHTING);
        Texture textureDiffuse = loadTexture(assetManager, textureFilePath);
        textureDiffuse.setWrap(Texture.WrapMode.Repeat);
        material.setTexture("DiffuseMap", textureDiffuse);
        if (normalMapFilePath != null) {
            Texture textureNormalMap = loadTexture(assetManager, normalMapFilePath);
            material.setTexture("NormalMap", textureNormalMap);
        }
        material.setFloat("Shininess", 5);
        return material;
    }

    public static Texture loadTexture(AssetManager assetManager, String filePath) {
        return assetManager.loadTexture(new TextureKey(filePath, false));
    }

    public static void setFilter_Nearest(Material material) {
        String textureParameterName = ((material.getParam("DiffuseMap") != null)?"DiffuseMap":"ColorMap");
        Texture texture = material.getTextureParam(textureParameterName).getTextureValue();
        texture.setMagFilter(Texture.MagFilter.Nearest);
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    }

    public static void setTransparent(Material material, boolean isTransparent) {
        if (isTransparent) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            material.setFloat("AlphaDiscardThreshold", 0.05f);
        } else {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
            material.clearParam("AlphaDiscardThreshold");
        }
    }
}
