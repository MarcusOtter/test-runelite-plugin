package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private ScheduledExecutorService executor;

	Clip clip = null;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);

			executor.submit(() -> {
				try (var resourceStream = ExamplePlugin.class.getResourceAsStream("/DyingHCIMCompleted_r1.wav")) {
					if (resourceStream == null) {
						log.error("Failed to load C Engineer sound as resource stream was null!");
					} else {
						InputStream fileStream = new BufferedInputStream(resourceStream);
						AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileStream);
						if (clip == null) {
							clip = AudioSystem.getClip();
						}
						clip.open(audioInputStream);
						clip.start();
						log.info("C Engineer sound loaded and played!");

						// Stop the clip after 500ms:
						// Thread.sleep(500);
						// clip.stop();
					}
				}
				catch (Exception e) {
					log.error(e.getMessage());
				}
			});
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
