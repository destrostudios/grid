package com.destrostudios.grid.shared;

import java.util.Arrays;

public class Util {

    public static float[] parseToFloatArray(String[] array) {
        float[] floatArray = new float[array.length];
        for (int i = 0; i < floatArray.length; i++) {
            floatArray[i] = Float.parseFloat(array[i]);
        }
        return floatArray;
    }

    public static <T> T createObjectByClassName(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while creating object of class '" + className + "'.");
        }
        return null;
    }

    public static int getIndexOfEquals(int[][] array, int[] value) {
        for (int i = 0; i < array.length; i++) {
            if (Arrays.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }
}
