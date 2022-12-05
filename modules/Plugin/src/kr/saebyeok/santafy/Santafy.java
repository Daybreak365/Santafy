package kr.saebyeok.santafy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import kr.saebyeok.santafy.util.SessionServer;
import kr.saebyeok.santafy.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.mineskin.MineskinClient;
import org.mineskin.data.MineskinException;
import org.mineskin.data.Texture;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Santafy extends JavaPlugin implements Listener, CommandExecutor {

	public static final String prefix = "§cSantafy §2》§f";
	private static Santafy plugin;
	private static final MineskinClient CLIENT = new MineskinClient("MyUserAgent");
	private static final BufferedImage SANTA_HAT;

	static {
		try {
			SANTA_HAT = ImageIO.read(Santafy.class.getResourceAsStream("/santa.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final Cache<UUID, Texture> textures = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.MINUTES)
			.build();
	private final NoSanta noSanta = new NoSanta();;

	public Santafy() {
		plugin = this;
	}

	public static Santafy getPlugin() throws IllegalStateException {
		if (plugin == null) throw new IllegalStateException();
		return plugin;
	}

	@Override
	public void onEnable() {
		getCommand("santa").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getConsoleSender().sendMessage(prefix + "플러그인이 활성화되었습니다. §8(§7" + getDescription().getVersion() + "§8)");
	}

	@EventHandler
	private void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) throws IOException, ExecutionException, InterruptedException {
		if (!noSanta.getState(e.getUniqueId())) return;
		if (textures.getIfPresent(e.getUniqueId()) != null) return;
		final BufferedImage userSkin = SessionServer.getSkin(e.getUniqueId());
		final BufferedImage newSkin = new BufferedImage(userSkin.getWidth(), userSkin.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = newSkin.getGraphics();
		graphics.drawImage(userSkin, 0, 0, null);
		graphics.drawImage(SANTA_HAT, 0, 0, null);
		graphics.dispose();
		try {
			final Texture texture = CLIENT.generateUpload(newSkin).get().data.texture;
			textures.put(e.getUniqueId(), texture);
		} catch (RuntimeException exception) {
			if (exception.getCause() instanceof MineskinException) {
				Bukkit.getConsoleSender().sendMessage(prefix + "산타 모자 적용 중 문제가 발생했습니다.");
			} else throw exception;
		}
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent e) {
		if (!noSanta.getState(e.getPlayer().getUniqueId())) return;
		final Texture texture = textures.getIfPresent(e.getPlayer().getUniqueId());
		if (texture == null) return;
		Skins.setTexture(
				e.getPlayer(),
				texture.value,
				texture.signature
		);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player) {
			if (noSanta.toggleState(((Player) sender).getUniqueId())) {
				sender.sendMessage(prefix + "이제 서버에 접속할 때 산타 모자를 §a착용합니다§f.");
			} else {
				sender.sendMessage(prefix + "이제 서버에 접속할 때 산타 모자를 §c착용하지 않습니다§f.");
			}
		} else sender.sendMessage(prefix + "콘솔에서 사용할 수 없는 명령어입니다.");
		return true;
	}

	@Override
	public void onDisable() {
		noSanta.save();
		Bukkit.getConsoleSender().sendMessage(prefix + "플러그인이 비활성화되었습니다.");
	}

}
