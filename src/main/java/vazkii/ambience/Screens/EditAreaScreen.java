package vazkii.ambience.Screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.ambience.Ambience;
import vazkii.ambience.World.Biomes.Area;
import vazkii.ambience.World.Biomes.Area.Operation;
import vazkii.ambience.network.AmbiencePackageHandler;
import vazkii.ambience.network.MyMessage;
import vazkii.ambience.render.ScrollListWidget;

@OnlyIn(Dist.CLIENT)
public class EditAreaScreen extends ContainerScreen<EditAreaContainer> {

	public static Area currentArea=new Area("Area1");
	private Button cancelBtn;
	private Button confirmBtn;
	private ImageButton showAreaBtn;
	//private TextFieldWidget AreaName;
	private CheckboxButton instaPlayChk;
	private CheckboxButton PlayatNight;
	private static final ResourceLocation textureBackground = new ResourceLocation("ambience:textures/gui/speaker_gui.png");
	public static final ResourceLocation textureBackground2 = new ResourceLocation("ambience:textures/gui/speaker_gui2.png");
	private boolean error;
	private ScrollListWidget list;

	public EditAreaScreen(EditAreaContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 256;
		this.ySize = 200;
		
		this.showAreaBtn = new ImageButton(10,10,20,20,0,0,-1,new ResourceLocation("ambience:textures/gui/areabtn.png"),20,20,
				(showArea) -> {		
					
										
					if(Ambience.previewArea.getName().contains(currentArea.getName()))
						Ambience.previewArea=new Area("Area1");
					else {					
						Ambience.previewArea=currentArea;
					}
					
					this.close();
				},(ITextComponent) new StringTextComponent(I18n.format("show Area")));
		
		this.cancelBtn = new Button(this.width / 2 - 105, this.height / 4 + 120, 100, 20,
				(ITextComponent) new StringTextComponent(I18n.format("GUI.DeleteButton")), (close) -> {	
					
					currentArea.setOperation(Operation.DELETE);
					AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
					
					currentArea = null;
					
					this.close();
				});

		this.confirmBtn = new Button(this.xSize / 2 + 5, this.ySize / 4 + 120, 100, 20,
				(ITextComponent) new StringTextComponent(I18n.format("GUI.ConfirmButton")), (confirm) -> {
							
						currentArea.setOperation(Operation.EDIT);
						currentArea.setName(list.getSelected().getText());
						currentArea.setInstantPlay(instaPlayChk.isChecked());
						currentArea.setPlayAtNight(PlayatNight.isChecked());
						AmbiencePackageHandler.sendToServer(new MyMessage(currentArea.SerializeThis()));
						Ambience.sync = true;
							
						this.close();
				});

		this.buttons.add(cancelBtn);
		this.buttons.add(confirmBtn);
		this.buttons.add(showAreaBtn);
	}

	@Override
	public void render(MatrixStack matrixStack,final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground(matrixStack);
		//super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		renderHoveredTooltip(matrixStack,mouseX, mouseY);

		if (this.list == null) {
			/*this.AreaName = new TextFieldWidget(this.font, this.width / 2 - 80, this.height / 4 + 15, 160, 20, I18n.format("GUI.AreaNameField"));
			AreaName.setText(currentArea.getName());
			AreaName.setFocused2(true);
			AreaName.setVisible(true);*/

			this.list = new ScrollListWidget(Minecraft.getInstance(),this.width,this.height, font, currentArea.getName());		
			
			this.instaPlayChk = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 50, 20, 20,(ITextComponent) new StringTextComponent(I18n.format("GUI.InstantPlayChk")), currentArea.isInstantPlay());
			this.PlayatNight = new CheckboxButton(this.width / 2 - 80, this.height / 4 + 80, 20, 20,(ITextComponent) new StringTextComponent(I18n.format("GUI.PlayAtNight")), currentArea.isPlayatNight());
		}

		confirmBtn.x = this.width / 2 + 5;
		confirmBtn.y = this.height / 2 + 60;
		cancelBtn.x = this.width / 2 - 105;
		cancelBtn.y = this.height / 2 + 60;
		

		showAreaBtn.x = this.width / 2 + 100;
		showAreaBtn.y = this.height / 2 - 95;

		this.instaPlayChk.x = this.width / 2 - 80;
		this.instaPlayChk.y = this.height / 2 + 5;
		this.PlayatNight.x = this.width / 2 - 80;
		this.PlayatNight.y = this.height / 2 + 30;
		
	/*	this.AreaName.x = this.width / 2 - 80;
		this.AreaName.y = this.height / 2 - 40;*/

		//this.AreaName.render(mouseX, mouseY, partialTicks);
		this.list.render(matrixStack,mouseX, mouseY, partialTicks);
		
		this.minecraft.getTextureManager().bindTexture(textureBackground2);
		int x=(this.width - this.xSize)/2;
		int y=(this.height - this.ySize)/2;

		this.blit(matrixStack,x, y, 0, 0, this.xSize, this.ySize);


		this.drawCenteredString(matrixStack,this.font, I18n.format("GUI.EditArea"), this.width / 2-86, this.height / 2 -88,16777215);

		this.showAreaBtn.render(matrixStack,mouseX, mouseY, partialTicks);
		this.cancelBtn.render(matrixStack,mouseX, mouseY, partialTicks);
		this.confirmBtn.render(matrixStack,mouseX, mouseY, partialTicks);
		this.instaPlayChk.render(matrixStack,mouseX, mouseY, partialTicks);
		this.PlayatNight.render(matrixStack,mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack,float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);
		
		this.minecraft.getTextureManager().bindTexture(textureBackground);
		int x=(this.width - this.xSize)/2;
		int y=(this.height - this.ySize)/2;

		this.blit(matrixStack,x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack,int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(matrixStack,mouseX, mouseY);

		//this.drawCenteredString(this.font, I18n.format("GUI.EditArea"), this.xSize / 2, this.ySize / 2 - 80,16777215);
		if(error)
			this.drawCenteredString(matrixStack,this.font,"§4"+I18n.format("GUI.CreateAreaError"), this.xSize / 2 ,	this.ySize / 2 - 54, 16777215);
	}

	// ----------------------------

	private void close() {
		 this.minecraft.player.closeScreen();
	      super.closeScreen();
	}

	public void onClose() {
		  super.onClose();
	}

	/*@Override
	public boolean changeFocus(boolean p_changeFocus_1_) {
		if (AreaName.isFocused())
			AreaName.setFocused2(false);
		else
			AreaName.setFocused2(true);

		return super.changeFocus(p_changeFocus_1_);
	}*/

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

		boolean specialKey = false;
		// user.setFocused2(true);
		/*if (AreaName.isFocused()) {
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
					AreaName.charTyped((char) p_keyPressed_1_, p_keyPressed_2_);
					error=false;
				}
			}
		}
		*/
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
		
		if (showAreaBtn.isHovered()) {
			showAreaBtn.playDownSound(minecraft.getSoundHandler());
			showAreaBtn.onClick(p_mouseClicked_1_, p_mouseClicked_3_);
		}
		
		if(list.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_))
			list.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
			
		/*boolean clicked = AreaName.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		AreaName.setFocused2(clicked);
		
		if(AreaName.getText().contains(I18n.format("GUI.AreaNameField")) & clicked) {
			AreaName.setText("");
		}
*/
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}
	
	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		list.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
		
		return super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_,
			double p_mouseDragged_6_, double p_mouseDragged_8_) {

		if(list.isMouseOver(p_mouseDragged_1_, p_mouseDragged_3_))
			list.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
		
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		 
		list.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
			
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	@Override
	public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
		list.setSize(p_resize_2_, p_resize_3_);		
		list.setLeftPos(23);
		
		super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
