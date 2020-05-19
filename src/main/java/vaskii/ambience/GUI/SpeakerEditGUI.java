package vaskii.ambience.GUI;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.primitives.Ints;
import com.ibm.icu.text.DecimalFormat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vaskii.ambience.network4.MyMessage4;
import vaskii.ambience.network4.NetworkHandler4;
import vaskii.ambience.objects.blocks.Speaker;
import vaskii.ambience.objects.blocks.SpeakerTileEntity;
import vazkii.ambience.Util.Handlers.SoundHandler;

public class SpeakerEditGUI extends GuiScreen {

	public static int GUIID = 3;
	public static HashMap guiinventory = new HashMap();
	public static String SelectedItem;
	public static int SelectedItemIndex = 0;

	public SpeakerEditGUI() {

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
		GuiScrollingList soundList;
		GuiTextField DelayTime;
		BlockPos pos = new BlockPos(0, 0, 0);

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.entity = entity;
			this.xSize = 232;
			this.ySize = 230;

			pos = new BlockPos(x, y, z);
		}

		private static final ResourceLocation texture = new ResourceLocation(
				"ambience:textures/gui/edit_window_back.png");

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);

			soundList.drawScreen(mouseX, mouseY, partialTicks);			
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(texture);
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.drawModalRectWithCustomSizedTexture(k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
			try {
				super.mouseClicked(mouseX, mouseY, mouseButton);
				DelayTime.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			} catch (Exception ignored) {
			}
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
			DelayTime.updateCursorCounter();
		}

		@Override
		protected void keyTyped(char typedChar, int keyCode) {
			try {

				if (keyCode == 1) {
					this.mc.player.closeScreen();
				}

				// Allows only numbers
				List<Integer> allowedKeys = Ints.asList(
						new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 71, 72, 73, 75, 76, 77, 79, 80, 81, 82 });																													

				if (allowedKeys.contains(keyCode))
					DelayTime.textboxKeyTyped(typedChar, keyCode);

			} catch (Exception ignored) {
			}
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(I18n.format("GUI.SelectSoundLbl"), 59, -18, -1);
			this.fontRenderer.drawString(I18n.format("Volume:"), -14, 128, -1);
			this.fontRenderer.drawString(I18n.format("Delay:"), -14, 148, -1);

			DelayTime.drawTextBox();
		}

		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}

		GuiCheckBox check;
		GuiSlider distance;
		@Override
		public void initGui() {
			super.initGui();
			this.guiLeft = (this.width - 176) / 2;
			this.guiTop = (this.height - 166) / 2;
			Keyboard.enableRepeatEvents(true);
			this.buttonList.clear();
			this.buttonList.add(
					new GuiButton(0, this.guiLeft + 39, this.guiTop + 170, 98, 20, I18n.format("GUI.ConfirmButton")));

			DelayTime = new GuiTextField(1, this.fontRenderer, 20, 143, 120, 20);
			DelayTime.setMaxStringLength(5);
			DelayTime.setText(Speaker.delaySound + "");
			
			
			this.buttonList.add(new GuiCheckBox(2, this.guiLeft + 150, this.guiTop + 148, "Loop", Speaker.loop));
			check = (GuiCheckBox) buttonList.get(1);
									
			distance= new GuiSlider(3, this.guiLeft + 25, this.guiTop + 124, 169, 15, "", "", 0, 10, Speaker.Distance, true, true);			
			this.buttonList.add(distance);
			
			
			getListSelectedIndex();
			soundList = new GuiScrollingList(this.mc, 212, 0, this.guiTop ,
					this.height - this.guiTop - 48, this.guiLeft - 18, 14) {

				List<String> strings = SoundHandler.SOUNDS;

				int selectedIndex = SpeakerEditGUI.SelectedItemIndex;

				protected boolean isSelected(int index) {
					if (index == selectedIndex) {
						return true;
					}

					return false;
				}

				protected int getSize() {
					return SoundHandler.SOUNDS.size();
				}

				protected void elementClicked(int index, boolean doubleClick) {
					this.selectedIndex = index;
					SpeakerEditGUI.SelectedItem = strings.get(index);
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

		private void getListSelectedIndex() {
			String selected_sound = Speaker.selectedSound;

			SelectedItemIndex = 0;
			if (selected_sound != null)
				for (String sound : SoundHandler.SOUNDS) {

					if (sound.contains(selected_sound)) {
						break;
					}
					SelectedItemIndex++;
				}
		}

		@Override
		protected void actionPerformed(GuiButton button) {

			if (button.id == 0) {
				{
					if (SelectedItem == null)
						SelectedItem = SoundHandler.SOUNDS.get(0);
					
					int Delay=0;
					if(DelayTime.getText() == null | DelayTime.getText().isEmpty()) {						
						Delay=10;
					}else if(Integer.parseInt(DelayTime.getText())<10) {
						Delay=10;
					}else {
						Delay=Integer.parseInt(DelayTime.getText());
					}

					String convertedFloat= String.format ("%.1f", (float)distance.getValue());
			       // String convertedFloat = decimalFormat.format((float)distance.getValue());
					
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("SoundEvent", SelectedItem);
					nbt.setInteger("x", this.pos.getX());
					nbt.setInteger("y", this.pos.getY());
					nbt.setInteger("z", this.pos.getZ());
					nbt.setInteger("delay",Delay);
					nbt.setBoolean("loop",check.isChecked());
					nbt.setFloat("distance",Float.parseFloat(convertedFloat));
					
					System.out.println(convertedFloat);

					
					
					NetworkHandler4.sendToServer(new MyMessage4(nbt));

					this.mc.player.closeScreen();
				}
			}
		}

		@Override
		public boolean doesGuiPauseGame() {
			return true;
		}
	}
}
