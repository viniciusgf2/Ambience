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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vaskii.ambience.GUI.Utils.ImageButtom;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vazkii.ambience.Ambience;
import vazkii.ambience.SongPicker;
import vazkii.ambience.Util.Handlers.SoundHandler;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;

public class EditAreaGUI extends GuiScreen {

	public static int GUIID = 2;
	public static HashMap guiinventory = new HashMap();
	public static Area currentArea;
	public static String SelectedItem;
	public static int SelectedItemIndex = 0;
	
	public EditAreaGUI(Ambience instance) {
		// super(instance);
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
			this.xSize = 282;
			this.ySize = 230;

			// Localization
			if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				I18n.format("GUI.ConfirmButton");
				I18n.format("GUI.DeleteButton");
				I18n.format("GUI.SaveButton");
				I18n.format("GUI.ShowArea");
				I18n.format("GUI.AreaNameLbl");
				I18n.format("GUI.InstantPlayChk");
				I18n.format("GUI.anoite");
			}
		}

		private static final ResourceLocation texture = new ResourceLocation("ambience:textures/gui/edit_window_back.png");

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
			
			for(GuiButton button : this.buttonList)
			{
				if(button instanceof ImageButtom)
				{
					((ImageButtom) button).drawScreen(mc);
				}
			}			

			areasList.drawScreen(mouseX, mouseY, partialTicks);	
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.drawModalRectWithCustomSizedTexture(k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);

			// zLevel = 100.0F;
		}

		

		@Override
		protected void keyTyped(char typedChar, int keyCode) {
			try {

				if (keyCode == 1) {
					this.mc.player.closeScreen();
				}
				
			} catch (Exception ignored) {
			}
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		
			this.fontRenderer.drawString(I18n.format("GUI.AreaNameLbl"), 55, -18, -1);
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

			this.buttonList.add(new GuiButton(0, this.guiLeft -34, this.guiTop + 162, 110, 20, I18n.format("GUI.DeleteButton")));
			this.buttonList.add(new GuiButton(1, this.guiLeft + 90, this.guiTop + 162, 116, 20, I18n.format("GUI.SaveButton")));
								
			int px = I18n.format("GUI.InstantPlayChk").contains("Tocar") ? this.guiLeft + 21 : this.guiLeft + 50;
			this.buttonList.add(new GuiCheckBox(2, px, this.guiTop + 144, I18n.format("GUI.InstantPlayChk"),	currentArea.isInstantPlay()));
			GuiCheckBox check = (GuiCheckBox) buttonList.get(2);
			guiinventory.put("check:InstantPlay", check);

			ImageButtom regionbtn=new ImageButtom(3, 1, this.guiLeft -45, this.guiTop-21, 20, 20, null, new ResourceLocation("ambience:textures/gui/areabtn.png"));			
			this.buttonList.add(regionbtn);
						
			px = I18n.format("GUI.anoite").contains("Tocar") ? this.guiLeft + 21 : this.guiLeft + 50;
			this.buttonList.add(new GuiCheckBox(4, px, this.guiTop + 125, I18n.format("GUI.anoite"),	currentArea.isPlayatNight()));
			GuiCheckBox check2 = (GuiCheckBox) buttonList.get(4);
			guiinventory.put("check:PlayatNight", check2);
						
			for (Map.Entry<String, String[]> entry : SongPicker.areasMap.entrySet()) {
				if(!entry.getKey().contains(".")) 
					strings.add(entry.getKey());				
			}
			
			
			areasList = new GuiScrollingList(this.mc, 212, 0, this.guiTop ,	this.height - this.guiTop - 48, this.guiLeft - 18, 14) {
				int selectedIndex = getListSelectedIndex(currentArea.getName());

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
					EditAreaGUI.SelectedItem = strings.get(index);
					this.isSelected(selectedIndex);
				}

				protected void drawSlot(int var1, int width, int height, int var4, Tessellator tess) {
					mc.fontRenderer.drawString(strings.get(var1), this.left + 10, height, 0xFFFFFF);
				}

				protected void drawBackground() {

				}
			};
		}

		private int getListSelectedIndex(String selectedArea) {

			int SelectedItemIndex = 0;
			if (selectedArea != null)
				
				for (Map.Entry<String, String[]> entry : SongPicker.areasMap.entrySet()) {
				    
					if(entry.getKey().contains(selectedArea))
						break;	
					SelectedItemIndex++;
				}
											
			return SelectedItemIndex;
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
					DeleteArea();
				}
			}
			if (button.id == 1) {
				{
					java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
					$_dependencies.put("guiinventory", guiinventory);
					EditArea($_dependencies);
				}
			}
			if (button.id == 3) {

				if(Ambience.previewArea == currentArea)
					Ambience.previewArea=new Area("Area1");
				else {
				
					if(Ambience.selectedArea.getPos1()==null & Ambience.selectedArea.getPos2()==null)
						Ambience.previewArea=currentArea;
					else
						Ambience.previewArea=new Area("Area1");
				}
					
				
				this.mc.player.closeScreen();
			}
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}

		private void DeleteArea() {

			// Send the selected area to the server to delete it
			currentArea.setOperation(Operation.DELETE);
			NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));

			currentArea = null;
			// Ambience.selectedArea.resetSelection();

			this.mc.player.closeScreen();
		}

		private void EditArea(java.util.HashMap<String, Object> dependencies) {

			HashMap guiinventory = (HashMap) dependencies.get("guiinventory");

			currentArea.setName(SelectedItem);			
			
			GuiCheckBox playAtNight = (GuiCheckBox) guiinventory.get("check:PlayatNight");
			currentArea.setPlayAtNight(playAtNight.isChecked());

			GuiCheckBox instanPlay = (GuiCheckBox) guiinventory.get("check:InstantPlay");
			currentArea.setInstantPlay(instanPlay.isChecked());

			currentArea.setOperation(Operation.EDIT);
			// Send the selected area to the server to save it
			NetworkHandler4.sendToServer(new MyMessage4(currentArea.SerializeThis()));

			Ambience.sync = true;
			currentArea = null;

			this.mc.player.closeScreen();
		}
	}
}
