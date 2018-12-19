package com.aidn5.hypeguild;

import java.io.File;
import java.io.IOException;

import com.aidn5.hypeguild.commands.Command;
import com.aidn5.hypeguild.util.CacheController;
import com.aidn5.hypeguild.util.IgnUuidResolver;
import com.aidn5.hypeguild.util.Settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = ModConfig.MODID, name = ModConfig.NAME, version = ModConfig.VERSION, clientSideOnly = true)
public class HypeGuild {
	public static HypeGuild instance;

	public final CacheController cacheController;
	public final IgnUuidResolver ignUuidResolver;
	public final Settings settings;

	public boolean onHypixel = false;
	public GuiScreen guiToDisplay = null;

	// TODO: Remove IOException
	public HypeGuild() throws IOException {
		instance = this;
		String mcDataDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();

		// Create and configure cacheController
		String cacheFolder = mcDataDir + "/mods/" + ModConfig.MODID + "/cache/";
		this.cacheController = new CacheController(new File(cacheFolder));
		this.cacheController.isReady();

		// Create configure IgnUuidResolver
		String ignCacheFile = mcDataDir + "/mods/" + ModConfig.MODID + "/ign.cache";
		this.ignUuidResolver = new IgnUuidResolver(new File(ignCacheFile));
		this.ignUuidResolver.load();

		// Load settings
		String settingsFileName = mcDataDir + "/config/" + ModConfig.AUTHOR + "-" + ModConfig.MODID + ".cfg";
		settings = new Settings(settingsFileName, false);
		settings.reloadUserSettings();
	}

	// register events
	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register command(s).
		ClientCommandHandler.instance.registerCommand(new Command());

		// register triggers to receive events
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ChatApiDetector(this.settings));
	}

	// Display GUI screen if it is provided in this.guiToDisplay
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onGameTick(TickEvent.ClientTickEvent event) {
		if (this.guiToDisplay != null) {

			Minecraft.getMinecraft().displayGuiScreen(this.guiToDisplay);
			this.guiToDisplay = null;

		}
	}

	// on login check whether the server is from Hypixel Network
	@SubscribeEvent
	public void playerLoggedIn(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		try {// In case of exception (somehow), Do NOT break THIS event (at any cost :P)

			Minecraft mc = Minecraft.getMinecraft();
			String serverIp = mc.getCurrentServerData().serverIP;
			onHypixel = serverIp.toLowerCase().contains("hypixel.net");

		} catch (Exception ignored) {}
	}

	// on logout flag the client as not on hypixel network
	@SubscribeEvent
	public void onLoggedOut(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		onHypixel = false;
	}
}
