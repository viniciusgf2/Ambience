package vazkii.ambience.Screens;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;

@OnlyIn(Dist.CLIENT)
public class CreateAreaScreen extends ContainerScreen<GuiContainerMod> {

	private Button cancelBtn;
	private Button confirmBtn;
	private TextFieldWidget AreaName;
	private CheckboxButton instaPlayChk;
	private CheckboxButton PlayatNight;
	private static final ResourceLocation textureBackground = new ResourceLocation("ambience:textures/gui/edit_window_back.png");
	private boolean error;

	public CreateAreaScreen(GuiContainerMod screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		// this.xSize = 232;
		// this.ySize = 112;

		this.cancelBtn = new Button(this.width / 2 - 105, this.height / 4 + 120, 100, 20,
				I18n.format("GUI.CancelButton"), (close) -> {					
					this.close();
				});

		this.confirmBtn = new Button(this.xSize / 2 + 5, this.ySize / 4 + 120, 100, 20,
				I18n.format("GUI.ConfirmButton"), (confirm) -> {

					if(AreaName.getText()=="" || AreaName.getText().hashCode()==0) {
						error=true;
						//inv.player.sendStatusMessage((ITextComponent)new TranslationTextComponent(I18n.format("GUI.CreateAreaError")),(true));
						
					}else {					
						Ambience.selectedArea.setOperation(Operation.CREATE);
						Ambience.selectedArea.setName(AreaName.getText());
						Ambience.selectedArea.setInstantPlay(instaPlayChk.isChecked());
						Ambience.selectedArea.setPlayAtNight(PlayatNight.isChecked());
						AmbiencePackageHandler.sendToServer(new MyMessage(Ambience.selectedArea.SerializeThis()));
						Ambience.sync = true;
	
						Ambience.selectedArea=new Area("Area1");
						this.close();
					}
				});

		this.buttons.add(cancelBtn);
		this.buttons.add(confirmBtn);
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		if (this.AreaName == null) {
			this.AreaName = new TextFieldWidget(this.font, this.width / 2 - 80, this.height / 4 + 15, 160, 20, I18n.format("GUI.AreaNameField"));
			AreaName.setText(I18n.format("GUI.AreaNameField"));
			AreaName.setFocused2(true);
			AreaName.setVisible(true);
			AreaName.setEnabled(false);

			this.instaPlayChk = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 50, 20, 20,
					I18n.format("GUI.InstantPlayChk"), false);
			this.PlayatNight = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 80, 20, 20,
					I18n.format("GUI.PlayAtNight"), false);
		}

		confirmBtn.x = this.width / 2 + 5;
		confirmBtn.y = this.height / 2 + 60;
		cancelBtn.x = this.width / 2 - 105;
		cancelBtn.y = this.height / 2 + 60;

		this.instaPlayChk.x = this.width / 2 - 80;
		this.instaPlayChk.y = this.height / 2 - 5;
		this.PlayatNight.x = this.width / 2 - 80;
		this.PlayatNight.y = this.height / 2 + 20;

		this.AreaName.x = this.width / 2 - 80;
		this.AreaName.y = this.height / 2 - 40;

		this.AreaName.render(mouseX, mouseY, partialTicks);

		this.cancelBtn.render(mouseX, mouseY, partialTicks);
		this.confirmBtn.render(mouseX, mouseY, partialTicks);
		this.instaPlayChk.render(mouseX, mouseY, partialTicks);
		this.PlayatNight.render(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);

		// this.minecraft.getTextureManager().bindTexture(textureBackground);
		// int x=(this.width - this.xSize)/2;
		// int y=(this.height - this.ySize)/2;

		// this.blit(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		this.drawCenteredString(this.font, I18n.format("GUI.CreateArea"), this.xSize / 2, this.ySize / 2 - 80,16777215);
		if(error)
			this.drawCenteredString(this.font,"�4"+I18n.format("GUI.CreateAreaError"), this.xSize / 2 ,	this.ySize / 2 - 54, 16777215);
	}

	// ----------------------------

	private void close() {
		this.minecraft.displayGuiScreen((Screen) null);
	}

	public void onClose() {
		this.close();
	}

	@Override
	public boolean changeFocus(boolean p_changeFocus_1_) {
		if (AreaName.isFocused())
			AreaName.setFocused2(false);
		else
			AreaName.setFocused2(true);

		return super.changeFocus(p_changeFocus_1_);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

		boolean specialKey = false;
		// user.setFocused2(true);
		if (AreaName.isFocused()) {
			specialKey = AreaName.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}

		if (!specialKey) {
			if (p_keyPressed_1_ != 340 & p_keyPressed_1_ != 341) {
				if (p_keyPressed_3_ == 0) {
					char charTyped = (char) p_keyPressed_1_;
					String lowerCaseChar = String.valueOf(charTyped).toLowerCase();
					AreaName.charTyped(lowerCaseChar.charAt(0), p_keyPressed_2_);
					error=false;
				} else {
					
					//if(p_keyPressed_1_ == 69 340)
					AreaName.charTyped((char) p_keyPressed_1_, p_keyPressed_2_);
					error=false;
				}
			}
		}
		
		if(p_keyPressed_1_== 69)
			return true;

		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {

		if (instaPlayChk.isHovered()) 
		{
			instaPlayChk.playDownSound(minecraft.getSoundHandler());
			instaPlayChk.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
		
		if (PlayatNight.isHovered()) {
			PlayatNight.playDownSound(minecraft.getSoundHandler());
			PlayatNight.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
			
		if (cancelBtn.isHovered()) {
			cancelBtn.playDownSound(minecraft.getSoundHandler());
			cancelBtn.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}

		if (confirmBtn.isHovered()) {
			confirmBtn.playDownSound(minecraft.getSoundHandler());
			confirmBtn.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
			
		boolean clicked = AreaName.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		AreaName.setFocused2(clicked);
		
		if(AreaName.getText().contains(I18n.format("GUI.AreaNameField")) & clicked) {
			AreaName.setText("");
			AreaName.setEnabled(true);
		}

		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
