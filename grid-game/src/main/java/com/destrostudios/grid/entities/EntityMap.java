package com.destrostudios.grid.entities;

import com.destrostudios.grid.components.PositionComponent;
import lombok.Getter;

import java.util.logging.Logger;

public class EntityMap {
    private final static Logger logger = Logger.getGlobal();

    @Getter
    private int[][] map;

    public EntityMap(int x, int y) {
        this.map = new int[x][y];
    }

    public void addEntityToMap(int entity, PositionComponent positionComponent) {
        this.map[positionComponent.getX()][positionComponent.getY()] = entity;
    }

    public void removeFromMap(PositionComponent positionComponent) {
        this.map[positionComponent.getX()][positionComponent.getY()] = 0;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }
}
