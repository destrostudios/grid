package com.destrostudios.grid.client.characters;

import java.util.HashMap;

public class CastAnimations {

    private static HashMap<String, BlockingAnimation> ANIMATIONS = new HashMap<>();

    public static BlockingAnimation get(String spellName) {
        return ANIMATIONS.computeIfAbsent(spellName, sn -> {
            switch (spellName) {
                // Iop
                case "Jump":
                    return new BlockingAnimation("spell2", 0.7f, 1);
                case "Intimidation":
                    return new BlockingAnimation("slash4", 0.4f, 0.8f);
                case "Blow":
                    return new BlockingAnimation("spell1", 0.5f, 1);
                case "Concentration":
                    return new BlockingAnimation("spin", 0.2f, 0.4f);
                case "Sword of Iop":
                    return new BlockingAnimation("slash2", 0.4f, 0.8f);
                case "Iop's Wrath":
                    return new BlockingAnimation("slash1", 0.5f, 1);
                case "Pressure":
                    return new BlockingAnimation("slash3", 0.5f, 1);
            }
            return null;
        });
    }
}
