package com.ecolony.ecolony.Exceptions;

public class WorldNameException extends Exception {
    public WorldNameException(String worldName) {
        super("Error! World " + worldName + " not found.");
    }
}