
package me.cum.fusion.features.command.commands;

import me.cum.fusion.features.command.*;
import java.net.*;
import java.io.*;
import com.google.gson.*;

public class QueueCommand extends Command
{
    public QueueCommand() {
        super("queue", new String[] { "priority, regular" });
    }
    
    public void execute(final String[] commands) {
        if (commands.length == 1 || commands.length == 0) {
            sendMessage("ayo, specify the type! (priority/regular)");
            return;
        }
        final String check = commands[0];
        if (check.equalsIgnoreCase("regular")) {
            try {
                final HttpURLConnection request = (HttpURLConnection)new URL("https://2bqueue.info/*").openConnection();
                request.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                request.connect();
                final JsonParser jp = new JsonParser();
                final JsonElement root = jp.parse((Reader)new BufferedReader(new InputStreamReader(request.getInputStream())));
                final JsonObject rootobj = root.getAsJsonObject();
                final String aaaaaa = rootobj.get("regular").getAsString();
                sendMessage("Regular queue currently have: " + aaaaaa);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (check.equalsIgnoreCase("priority")) {
            try {
                final HttpURLConnection request = (HttpURLConnection)new URL("https://2bqueue.info/*").openConnection();
                request.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                request.connect();
                final JsonParser jp = new JsonParser();
                final JsonElement root = jp.parse((Reader)new BufferedReader(new InputStreamReader(request.getInputStream())));
                final JsonObject rootobj = root.getAsJsonObject();
                final String aaaaaa = rootobj.get("prio").getAsString();
                sendMessage("Priority queue currently have: " + aaaaaa);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
