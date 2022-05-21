
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import java.util.*;

public class RpsCommand extends Command
{
    public RpsCommand() {
        super("rps", new String[] { "rock, paper, scissors" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1 || commands.length == 0) {
            sendMessage("RPS > specify type u monke");
            return;
        }
        final Random rng = new Random();
        final int rngNumber = rng.nextInt(3);
        final String check = commands[0];
        if (check.equalsIgnoreCase("rock")) {
            final String value = this.getValue(rngNumber);
            if (value.equalsIgnoreCase("Rock")) {
                sendSilentMessage("RPS > u draw noob");
            }
            if (value.equalsIgnoreCase("Paper")) {
                sendSilentMessage("RPS > LOL, you lose. what a loser");
            }
            if (value.equalsIgnoreCase("Scissors")) {
                sendSilentMessage("RPS > u won :O");
            }
        }
        else if (check.equalsIgnoreCase("paper")) {
            final String value = this.getValue(rngNumber);
            if (value.equalsIgnoreCase("Paper")) {
                sendSilentMessage("RPS > u draw noob");
            }
            if (value.equalsIgnoreCase("Scissors")) {
                sendSilentMessage("RPS > LOL, you lose. what a loser");
            }
            if (value.equalsIgnoreCase("Rock")) {
                sendSilentMessage("RPS > u won :O");
            }
        }
        else if (check.equalsIgnoreCase("scissors")) {
            final String value = this.getValue(rngNumber);
            if (value.equalsIgnoreCase("Scissors")) {
                sendSilentMessage("RPS > u draw noob");
            }
            if (value.equalsIgnoreCase("Rock")) {
                sendSilentMessage("RPS > LOL, you lose. what a loser");
            }
            if (value.equalsIgnoreCase("Paper")) {
                sendSilentMessage("RPS > u won :O");
            }
        }
        else {
            sendSilentMessage("RPS > lol use rock/paper/scissors");
        }
    }
    
    String getValue(final int a) {
        if (a == 1) {
            return "Rock";
        }
        if (a == 2) {
            return "Paper";
        }
        return "Scissors";
    }
}
