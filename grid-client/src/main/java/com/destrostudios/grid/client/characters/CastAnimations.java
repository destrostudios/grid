package com.destrostudios.grid.client.characters;

import java.util.HashMap;

public class CastAnimations {

    private static HashMap<String, CustomBlockingAnimationInfo> ANIMATIONS = new HashMap<>();

    public static CustomBlockingAnimationInfo get(String spellName) {
        return ANIMATIONS.computeIfAbsent(spellName, sn -> {
            switch (spellName) {
                // Iop
                case "Jump":
                    return new CustomBlockingAnimationInfo("spell2", 0.7f, 1);
                case "Intimidation":
                    return new CustomBlockingAnimationInfo("slash4", 0.4f, 0.8f);
                case "Blow":
                    return new CustomBlockingAnimationInfo("spell1", 0.5f, 1);
                case "Concentration":
                    return new CustomBlockingAnimationInfo("spin", 0.2f, 0.4f);
                case "Sword of Iop":
                    return new CustomBlockingAnimationInfo("slash2", 0.4f, 0.8f);
                case "Iop's Wrath":
                    return new CustomBlockingAnimationInfo("slash1", 0.5f, 1);
                case "Pressure":
                    return new CustomBlockingAnimationInfo("slash3", 0.5f, 1);
                // Alice
                case "AP Buff":
                case "Regen Buff":
                    return new CustomBlockingAnimationInfo("spell1", 0.6f, 1.15f);
                case "Reflection Buff":
                    return new CustomBlockingAnimationInfo("spell10", 0.865f, 1.815f);
                case "Alice AOE":
                    return new CustomBlockingAnimationInfo("spell4", 0.85f, 1.7f);
                case "Alice Push":
                    return new CustomBlockingAnimationInfo("spell6", 0.8f, 1.01f);
                case "Nothing":
                    return new CustomBlockingAnimationInfo("spell5", 0.935f, 1.635f);
                case "Alice Comet":
                    return new CustomBlockingAnimationInfo("spell5", 0.385f, 1.1f);
                // Garmon
                case "Arcane Laser":
                    return new CustomBlockingAnimationInfo("spell11", 1.042f, 3.292f);
                case "Cursed Ground":
                    return new CustomBlockingAnimationInfo("spell2", 0.9f, 2.667f);
                case "Fortify":
                    return new CustomBlockingAnimationInfo("spell6", 1.042f, 2.292f);
                case "Lunar Strike":
                    return new CustomBlockingAnimationInfo("spell8", 1.333f, 2.917f);
                case "Magic Pillar":
                    return new CustomBlockingAnimationInfo("spell12", 2.167f, 3.542f);
                case "Magic Wall":
                    return new CustomBlockingAnimationInfo("spell9", 1.792f, 3.208f);
                case "Garmons Protection":
                    return new CustomBlockingAnimationInfo("spell4", 0.708f, 2.25f);
            }
            return null;
        });
    }
}
