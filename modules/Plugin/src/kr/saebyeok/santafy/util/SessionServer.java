package kr.saebyeok.santafy.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class SessionServer {

    private SessionServer() {}

    public static JsonObject getProfile(UUID uniqueId) throws IOException {
        final StringBuilder response = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString()).openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }

    public static BufferedImage getSkin(UUID uniqueId) throws IOException {
        final JsonObject value = JsonParser.parseString(new String(Base64.getDecoder().decode(getProfile(uniqueId).getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString()))).getAsJsonObject();
        final String url = value.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        return ImageIO.read(new URL(url));
    }

}
