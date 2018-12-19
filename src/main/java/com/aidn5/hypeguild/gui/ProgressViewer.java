package com.aidn5.hypeguild.gui;

import java.awt.Color;
import java.io.IOException;

import com.aidn5.hypeguild.ModConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ProgressViewer extends GuiScreen {
	private static int white = Color.WHITE.getRGB();

	public int total = 0;
	public int currentProgress = 0;

	public String messageToView = "Hello beautiful :)";
	public Runnable onCancel;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRendererObj, ModConfig.NAME, width / 2, 5, white);
		drawCenteredString(fontRendererObj, "Version " + ModConfig.VERSION + " by " + ModConfig.AUTHOR, width / 2, 16,
				white);

		drawCenteredString(fontRendererObj, "", width / 2, (height / 2) - 30, white);
		drawCenteredString(fontRendererObj, total + "/" + currentProgress, width / 2, (height / 2) - 10, white);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@Override
	public void onGuiClosed() {
		if (this.onCancel != null) onCancel.run();
		super.onGuiClosed();
	}
}
