package com.destrostudios.grid.client.characters;

import com.destrostudios.grid.client.JMonkeyUtil;
import com.destrostudios.grid.client.models.MaterialFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;

public class ModelColorizer {

    private static final String USER_DATA_NAME_ORIGINAL_QUEUE_BUCKET = "originalQueueBucketOrdinal";

    public static void setColor(Spatial spatial, ColorRGBA color) {
        for (Geometry geometry : JMonkeyUtil.getAllGeometryChilds(spatial)) {
            // Update color
            boolean wasModified = false;
            Material material = geometry.getMaterial();
            if (MaterialFactory.DEFINITION_NAME_LIGHTING.equals(material.getMaterialDef().getAssetName())) {
                if (color != null) {
                    material.setBoolean("UseMaterialColors", true);
                    material.setColor("Diffuse", color);
                    material.setColor("Ambient", color);
                } else {
                    material.clearParam("UseMaterialColors");
                    material.clearParam("Diffuse");
                    material.clearParam("Ambient");
                }
                wasModified = true;
            } else if (MaterialFactory.DEFINITION_NAME_UNSHADED.equals(material.getMaterialDef().getAssetName())) {
                if (color != null) {
                    material.setColor("Color", color);
                } else {
                    material.clearParam("Color");
                }
                wasModified = true;
            }
            // Update transparency
            if (wasModified) {
                Integer queueBucketOrdinal = geometry.getUserData(USER_DATA_NAME_ORIGINAL_QUEUE_BUCKET);
                if (queueBucketOrdinal == null) {
                    queueBucketOrdinal = geometry.getQueueBucket().ordinal();
                    geometry.setUserData(USER_DATA_NAME_ORIGINAL_QUEUE_BUCKET, queueBucketOrdinal);
                }
                RenderQueue.Bucket queueBucket = RenderQueue.Bucket.values()[queueBucketOrdinal];
                if (queueBucket == RenderQueue.Bucket.Opaque) {
                    boolean isTransparent = ((color != null) && (color.getAlpha() < 1));
                    setTransparent(material, isTransparent);
                    if (isTransparent) {
                        queueBucket = RenderQueue.Bucket.Transparent;
                    }
                }
                geometry.setQueueBucket(queueBucket);
            }
        }
    }

    private static void setTransparent(Material material, boolean isTransparent) {
        if (isTransparent) {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            material.setFloat("AlphaDiscardThreshold", 0.05f);
        } else {
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
            material.clearParam("AlphaDiscardThreshold");
        }
    }
}
