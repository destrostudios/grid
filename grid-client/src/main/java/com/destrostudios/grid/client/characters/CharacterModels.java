package com.destrostudios.grid.client.characters;

public class CharacterModels {

    public static CharacterModel get(String name) {
        switch (name) {
            case "aland": return new CharacterModel("aland",  "idle", 11.267f, "run", 6);
            case "alice": return new CharacterModel("alice", "idle1", 1.867f, "run", 6);
            case "dosaz": return new CharacterModel("dosaz", "idle", 7.417f, "run2", 7);
            case "dwarf_warrior": return new CharacterModel("dwarf_warrior", "idle1", 7.875f, "run2", 6);
            case "elven_archer": return new CharacterModel("elven_archer", "idle1", 5.1f, "run1", 6);
            case "garmon": return new CharacterModel("garmon", "idle2", 10, "walk2", 6);
            case "scarlet": return new CharacterModel("scarlet", "idle", 2, "run3", 7);
            case "tristan": return new CharacterModel("tristan", "idle1", 7.567f, "run1", 7.8f);
        }
        throw new IllegalArgumentException(name);
    }
}
