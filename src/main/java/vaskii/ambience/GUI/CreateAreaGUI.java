package vaskii.ambience.GUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area.Operation;

public class CreateAreaGUI extends GuiScreen {

	public static int GUIID = 5;
	public static HashMap guiinventory = new HashMap();
	public static String SelectedItem;
	public static int SelectedItemIndex = 0;

	public CreateAreaGUI(Ambience instance) {

	}

	public static class GuiContainerMod extends Container {

		World world;
		EntityPlayer entity;
		int x, y, z;

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;

			if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				// Localization
				I18n.format("GUI.ConfirmButton");
				I18n.format("GUI.AreaNameLbl");
				I18n.format("GUI.AreaNameField");
				I18n.format("GUI.InstantPlayChk");
			}
		}

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return true;
		}

		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
		}
	}

	public static class GuiWindow extends GuiContainer {

		World world;
		int x, y, z;
		EntityPlayer entity;
		GuiScrollingList areasList;
		List<String> strings= new ArrayList();

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.entity = entity;
			this.xSize = 232;
			this.ySize = 230;
		}

		// private static final ResourceLocation texture = new
		// ResourceLocation("ambience:textures/gui/default_window_back.png");
		private static final ResourceLocation texture = new ResourceLocation("ambience:textures/gui/edit_window_back.png");

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
			

			areasList.drawScreen(mouseX, mouseY, partialTicks);	
			
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			// this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
			this.drawModalRectWithCustomSizedTexture(k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
			// zLevel = 100.0F;
		}

	/*	@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			try {
				super.mouseClicked(mouseX, mouseY, mouseButton);
				AreaName.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			} catch (Exception ignored) {
			}
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
			AreaName.updateCursorCounter();
		}*/

		@Override
		protected void keyTyped(char typedChar, int keyCode) {
			try {

				if (keyCode == 1) {
					this.mc.player.closeScreen();
				}

				//AreaName.textboxKeyTyped(typedChar, keyCode);

			} catch (Exception ignored) {
			}
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(I18n.format("GUI.AreaNameLbl"), 59, -18, -1);

			// this.fontRenderer.drawString(I18n.format("container.inscription", new
			// Object[0]), 5, (this.ySize - 203) + 2, 4210752);

			//AreaName.drawTextBox();
		}

		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}
		
		@Override
		public void initGui() {
			super.initGui();
			this.guiLeft = (this.width - 176) / 2;
			this.guiTop = (this.height - 166) / 2;
			Keyboard.enableRepeatEvents(true);
			this.buttonList.clear();
			
			this.buttonList.add(new GuiButton(0, this.guiLeft + 39, this.guiTop + 162, 98, 20, I18n.format("GUI.ConfirmButton")));
			
			int px = I18n.format("GUI.InstantPlayChk").contains("Tocar") ? this.guiLeft + 21 : this.guiLeft + 50;
			
			this.buttonList.add(new GuiCheckBox(1, px, this.guiTop + 125, I18n.format("GUI.anoite"), false));			
			GuiCheckBox checkover = (GuiCheckBox) buttonList.get(1);
			guiinventory.put("check:PlayatNight", checkover);
			
			this.buttonList.add(new GuiCheckBox(2, px, this.guiTop + 144, I18n.format("GUI.InstantPlayChk"), false));			
			GuiCheckBox check = (GuiCheckBox) buttonList.get(2);
			guiinventory.put("check:InstantPlay", check);
			
						
			for (Map.Entry<String, String[]> entry : SongPicker.areasMap.entrySet()) {
			    
			    strings.add(entry.getKey());				
			}
			
			areasList = new GuiScrollingList(this.mc, 212, 0, this.guiTop ,	this.height - this.guiTop - 48, this.guiLeft - 18, 14) {
				int selectedIndex = 0;

				protected boolean isSelected(int index) {
					if (index == selectedIndex) {
						return true;
					}

					return false;
				}

				protected int getSize() {
					return strings.size();
				}

				protected void elementClicked(int index, boolean doubleClick) {
					this.selectedIndex = index;
				    CreateAreaGUI.SelectedItem = strings.get(index);
					this.isSelected(selectedIndex);
				}

				protected void drawSlot(int var1, int width, int height, int var4, Tessellator tess) {
					//mc.fontRenderer.drawString(strings.get(var1), (width - 106) / 2 + 10, height, 0xFFFFFF);
					//mc.fontRenderer.drawString(strings.get(var1), width / 2 + 20, height, 0xFFFFFF);
					mc.fontRenderer.drawString(strings.get(var1), this.left + 10, height, 0xFFFFFF);
				}

				protected void drawBackground() {

				}
			};
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			
			if (SelectedItem == null)
				if(strings.size()>0)
					SelectedItem = strings.get(0);
				else
					SelectedItem="";
			
			if (button.id == 0) {
				{
					HashMap<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("guiinventory", guiinventory);
					CreateArea($_dependencies);
				}
			}
		}

		@Override
		public boolean doesGuiPauseGame() {
			return true;
		}

		private void CreateArea(HashMap<String, Object> dependencies) {

			HashMap guiinventory = (HashMap) dependencies.get("guiinventory");
			
			GuiCheckBox playAtNight = (GuiCheckBox) guiinventory.get("check:PlayatNight");
			GuiCheckBox instanPlay = (GuiCheckBox) guiinventory.get("check:InstantPlay");

			Ambience.selectedArea.setName(SelectedItem);
			Ambience.selectedArea.setOperation(Operation.CREATE);
			Ambience.selectedArea.setPlayAtNight(playAtNight.isChecked());
			Ambience.selectedArea.setInstantPlay(instanPlay.isChecked());
			// Send the selected area to the server to save it
			NetworkHandler4.sendToServer(new MyMessage4(Ambience.selectedArea.SerializeThis()));
			Ambience.sync = true;
			// Ambience.selectedArea.resetSelection();

			this.mc.player.closeScreen();
		}
	}
	
	
}
