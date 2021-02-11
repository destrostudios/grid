package com.destrostudios.grid.client;

public class PositionUtil {

    public static final float CHARACTER_Y = 3;

    public static float get3dCoordinate(int coordinate) {
        return (coordinate + 0.5f) * 3;
    }
}
