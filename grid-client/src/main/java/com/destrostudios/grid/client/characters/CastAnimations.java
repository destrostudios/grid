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
                // Alice
                case "AP Buff":
                case "Regen Buff":
                    return new BlockingAnimation("spell1", 0.6f, 1.15f);
                case "Reflection Buff":
                    return new BlockingAnimation("spell10", 0.865f, 1.815f);
                case "Alice AOE":
                    return new BlockingAnimation("spell4", 0.85f, 1.7f);
                case "Alice Push":
                    return new BlockingAnimation("spell6", 0.8f, 1.01f);
                case "Nothing":
                    return new BlockingAnimation("spell5", 0.935f, 1.635f);
                case "Alice Comet":
                    return new BlockingAnimation("spell5", 0.385f, 1.1f);
            }
            return null;
        });
    }
}
