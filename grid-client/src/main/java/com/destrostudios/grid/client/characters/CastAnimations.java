package com.destrostudios.grid.client.characters;

import java.util.HashMap;

public class CastAnimations {

    private static HashMap<String, ModelAnimationInfo> ANIMATIONS = new HashMap<>();

    public static ModelAnimationInfo get(String spellName) {
        return ANIMATIONS.computeIfAbsent(spellName, sn -> {
            switch (spellName) {
                // Iop
                case "Jump":
                    return new ModelAnimationInfo("spell2", 0.7f, 1);
                case "Intimidation":
                    return new ModelAnimationInfo("slash4", 0.4f, 0.8f);
                case "Blow":
                    return new ModelAnimationInfo("spell1", 0.5f, 1);
                case "Concentration":
                    return new ModelAnimationInfo("spin", 0.2f, 0.4f);
                case "Sword of Iop":
                    return new ModelAnimationInfo("slash2", 0.4f, 0.8f);
                case "Iop's Wrath":
                    return new ModelAnimationInfo("slash1", 0.5f, 1);
                case "Pressure":
                    return new ModelAnimationInfo("slash3", 0.5f, 1);
            }
            return null;
        });
    }
}
