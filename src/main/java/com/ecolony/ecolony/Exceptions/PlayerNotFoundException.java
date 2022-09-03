package com.ecolony.ecolony.Exceptions;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String playerName) {
        super("Error! Player " + playerName + " not found.");
    }
}
