package kr.saebyeok.santafy.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Skins {

    private Skins() {}

    private static GameProfile getProfile(Player player) {
        try {
            return (GameProfile) player.getClass().getMethod("getProfile").invoke(player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTextureValue(Player player) {
        return getProfile(player).getProperties().get("textures").iterator().next().getValue();
    }

    public static void setTexture(Player player, String value, String signature) {
        final GameProfile profile = getProfile(player);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.removeAll("textures");
        propertyMap.put("textures", new Property("textures", value, signature));
    }

}
