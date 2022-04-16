package com.destrostudios.grid.client.characters;

import java.util.HashMap;

public class ModelInfos {

    private static HashMap<String, ModelInfo> MODEL_INFOS = new HashMap<>();

    public static ModelInfo get(String visualName) {
        return MODEL_INFOS.computeIfAbsent(visualName, vn -> {
            switch (visualName) {
                case "aland":
                    return new ModelInfo(
                        "aland",
                        new AnimationInfo("idle", 11.267f),
                        new AnimationInfo("run", 6),
                        new AnimationInfo("death", 6, false)
                    );
                case "alice":
                    return new ModelInfo(
                        "alice",
                        new AnimationInfo("idle1", 1.867f),
                        new AnimationInfo("run", 6),
                        new AnimationInfo("death2", 3.2f, false)
                    );
                case "dosaz":
                    return new ModelInfo(
                        "dosaz",
                        new AnimationInfo("idle", 7.417f),
                        new AnimationInfo("run2", 7),
                        new AnimationInfo("death", 3.125f, false)
                    );
                case "dwarf_warrior":
                    return new ModelInfo(
                        "dwarf_warrior",
                        new AnimationInfo("idle1", 7.875f),
                        new AnimationInfo("run2", 6),
                        new AnimationInfo("death", 2.667f, false)
                    );
                case "elven_archer":
                    return new ModelInfo(
                        "elven_archer",
                        new AnimationInfo("idle1", 5.1f),
                        new AnimationInfo("run1", 6),
                        new AnimationInfo("death1", 2.567f, false)
                    );
                case "garmon":
                    return new ModelInfo(
                        "garmon",
                        new AnimationInfo("idle2", 10),
                        new AnimationInfo("walk2", 6),
                        new AnimationInfo("death", 2.28f, false)
                    );
                case "scarlet":
                    return new ModelInfo(
                        "scarlet",
                        new AnimationInfo("idle", 2),
                        new AnimationInfo("run3", 7),
                        new AnimationInfo("death", 5, false)
                    );
                case "tristan":
                    return new ModelInfo(
                        "tristan",
                        new AnimationInfo("idle1", 7.567f),
                        new AnimationInfo("run1", 7.8f),
                        new AnimationInfo("death2", 2.3f, false)
                    );
                case "rock": return new ModelInfo("rock");
                case "tree": return new ModelInfo("tree");
                case "pillar": return new ModelInfo("pillar");
                case "pillar_script": return new ModelInfo("pillar_script");
                case "cursed_ground": return new ModelInfo("cursed_ground");
            }
            return null;
        });
    }
}
