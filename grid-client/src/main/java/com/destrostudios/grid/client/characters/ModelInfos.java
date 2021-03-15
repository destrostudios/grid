package com.destrostudios.grid.client.characters;

public class ModelInfos {

    public static ModelInfo get(String visualName) {
        switch (visualName) {
            case "aland": return new ModelInfo("aland",  new AnimationInfo("idle", 11.267f), new AnimationInfo("run", 6));
            case "alice": return new ModelInfo("alice", new AnimationInfo("idle1", 1.867f), new AnimationInfo("run", 6));
            case "dosaz": return new ModelInfo("dosaz", new AnimationInfo("idle", 7.417f), new AnimationInfo("run2", 7));
            case "dwarf_warrior": return new ModelInfo("dwarf_warrior", new AnimationInfo("idle1", 7.875f), new AnimationInfo("run2", 6));
            case "elven_archer": return new ModelInfo("elven_archer", new AnimationInfo("idle1", 5.1f), new AnimationInfo("run1", 6));
            case "garmon": return new ModelInfo("garmon", new AnimationInfo("idle2", 10), new AnimationInfo("walk2", 6));
            case "scarlet": return new ModelInfo("scarlet", new AnimationInfo("idle", 2), new AnimationInfo("run3", 7));
            case "tristan": return new ModelInfo("tristan", new AnimationInfo("idle1", 7.567f), new AnimationInfo("run1", 7.8f));
            case "rock": return new ModelInfo("rock", null, null);
            case "tree": return new ModelInfo("tree", null, null);
            case "pillar": return new ModelInfo("pillar", null, null);
            case "pillar_script": return new ModelInfo("pillar_script", null, null);
        }
        return null;
    }
}
