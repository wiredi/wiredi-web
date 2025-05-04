package com.wiredi.web.utils;

public class Arrays {

    public static byte[] append(byte[] base, byte[] appendix) {
        byte[] combinedArray = new byte[base.length + appendix.length];

        System.arraycopy(base, 0, combinedArray, 0, base.length);
        System.arraycopy(appendix, 0, combinedArray, base.length, appendix.length);

        return combinedArray;
    }

    public static byte[] prepend(byte[] base, byte[] prefix) {
        byte[] combinedArray = new byte[base.length + prefix.length];
        System.arraycopy(prefix, 0, combinedArray, 0, prefix.length);
        System.arraycopy(base, 0, combinedArray, prefix.length, base.length);
        return combinedArray;
    }

}
