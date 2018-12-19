package com.aidn5.hypeguild.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class ListViewer extends GuiScreen {
	private static final int White = Color.WHITE.getRGB();

	private final List<String> listString;
	private GuiScrollingList list;

	public ListViewer(List<String> guildMembers) {
		super();

		this.listString = guildMembers;
	}

	@Override
	public void initGui() {
		this.list = new GuiScrollingList(this.mc, this.width + 100, this.height, 0, this.height, -100, 14) {

			@Override
			protected int getSize() {
				return listString.size();
			}

			@Override
			protected void drawSlot(int i, int width, int height, int var4, Tessellator tess) {
				drawCenteredString(fontRendererObj, listString.get(i), width / 2, height, White);
			}

			@Override
			protected void drawBackground() {}

			@Override
			protected void elementClicked(int index, boolean doubleClick) {}

			@Override
			protected boolean isSelected(int index) {
				return false;
			}
		};
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.list.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
