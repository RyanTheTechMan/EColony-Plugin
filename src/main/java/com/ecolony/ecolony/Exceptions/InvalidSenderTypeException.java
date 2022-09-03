package com.ecolony.ecolony.Exceptions;

import org.bukkit.command.CommandSender;

public class InvalidSenderTypeException extends Exception {
    public InvalidSenderTypeException(CommandSender sender, CommandSenderType wanted) {
        super("Error! Expected " + wanted.toString() + " but got " + sender.getClass().getSimpleName());
    }
}
