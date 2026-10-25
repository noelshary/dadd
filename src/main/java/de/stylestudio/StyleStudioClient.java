package de.stylestudio;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class StyleStudioClient implements ClientModInitializer {
	public static final String MOD_ID = "style-studio";

	@Override
	public void onInitializeClient() {
		StylesConfig.load();

		HudElementRegistry.addLast(
			Identifier.of(MOD_ID, "info"),
			(context, tickCounter) -> HudStyler.render(context)
		);

		ScreenEvents.AFTER_INIT.register(MenuStyler::onScreenInit);

		// /stylestudio öffnet den Style-Browser im Spiel
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommandManager.literal("stylestudio").executes(context -> {
				MinecraftClient mc = MinecraftClient.getInstance();
				mc.send(() -> mc.setScreen(new StyleBrowserScreen(null)));
				return 1;
			}))
		);
	}
}
